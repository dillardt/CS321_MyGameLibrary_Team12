package Controller;

import Model.*;
import Model.Collection;
import View.*;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * Central coordinator for the entire application.
 *
 * Uses CardLayout so all views live in one container and swap cleanly.
 * All event wiring lives here — controllers handle business logic only.
 *
 * Design pattern: Mediator
 */
public class AppCoordinator {

    // Card names for the CardLayout switcher
    private static final String CARD_LOGIN       = "LOGIN";
    private static final String CARD_GAME_LIST   = "GAME_LIST";
    private static final String CARD_GAME_DETAIL = "GAME_DETAIL";
    private static final String CARD_COLLECTIONS = "COLLECTIONS";

    private final JFrame     frame;
    private final JPanel     cardPanel;
    private final CardLayout cardLayout;

    private final LoginView          loginView;
    private final GameListView       gameListView;
    private final GameDetailView     gameDetailView;
    private final CollectionListView collectionListView;

    private final AuthenticationController authController;
    private final GameController           gameController;
    private final CollectionController     collectionController;

    private final UserDatabase userDatabase;
    private final GameDatabase gameDatabase;

    // Holds the current filtered result so search always runs within it.
    // null means no filter is active — search uses the full game list.
    private List<Game> activeFilteredList = null;

    public AppCoordinator() {

        userDatabase = new UserDatabase("data/users.xml");
        gameDatabase = new GameDatabase("data/bgg90Games.xml");

        // R6 — after both databases have loaded, resolve any saved collection
        // game IDs back into full Game objects from the game database
        resolveCollectionPlaceholders();

        authController       = new AuthenticationController(userDatabase);
        gameController       = new GameController(gameDatabase);
        collectionController = new CollectionController();

        loginView          = new LoginView();
        gameListView       = new GameListView();
        gameDetailView     = new GameDetailView();
        collectionListView = new CollectionListView();

        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.add(loginView,          CARD_LOGIN);
        cardPanel.add(gameListView,       CARD_GAME_LIST);
        cardPanel.add(gameDetailView,     CARD_GAME_DETAIL);
        cardPanel.add(collectionListView, CARD_COLLECTIONS);

        frame = new JFrame("My Game Library");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 680);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(cardPanel);

        setupLoginView();
        setupGameListView();
        setupGameDetailView();
        setupCollectionListView();

        showCard(CARD_LOGIN);
        frame.setVisible(true);
    }


    private void setupLoginView() {

        loginView.addLoginListener(e -> {
            String username = loginView.getUsernameInput();
            String password = loginView.getPasswordInput();
            if (authController.login(username, password)) {
                loginView.setStatusMessage("Login successful!", false);
                loginView.clearAllInputs();
                refreshGameList();
                // Show Add/Delete Game buttons only for admin users
                gameListView.setAdminControlsVisible(authController.getCurrentUser().isAdmin());
                showCard(CARD_GAME_LIST);
            } else {
                loginView.setStatusMessage("Invalid username or password.", true);
                loginView.clearPassword();
            }
        });

        loginView.addCreateAccountListener(e -> {
            String username = loginView.getUsernameInput();
            String password = loginView.getPasswordInput();
            if (username.isBlank() || password.isBlank()) {
                loginView.setStatusMessage("Username and password required.", true);
                return;
            }
            if (authController.createAccount(username, password)) {
                loginView.setStatusMessage("Account created! You can now log in.", false);
            } else {
                loginView.setStatusMessage("Username already taken.", true);
            }
        });

        loginView.addGuestLoginListener(e -> {
            authController.logout();
            activeFilteredList = null;
            gameController.clearGuestRecentlyViewed();
            refreshGameList();
            gameListView.setAdminControlsVisible(false);
            showCard(CARD_GAME_LIST);
        });
    }


    private void setupGameListView() {

        // Search — runs within the active filtered list if one is set,
        // otherwise searches the full list or selected collection (R8)
        gameListView.addSearchListener(e -> {
            String criteria = gameListView.getSearchCriteria();
            User user = authController.getCurrentUser();
            String selectedCollection = gameListView.getSelectedCollectionName();

            if (user != null && selectedCollection != null && !selectedCollection.isBlank()) {
                // R8: collection selected — search within that collection
                List<Game> collectionGames = gameController.getGamesByCollection(user, selectedCollection);
                gameListView.setGames(gameController.searchWithinList(collectionGames, criteria));
            } else if (activeFilteredList != null) {
                // Filter is active — search within the filtered results
                gameListView.setGames(gameController.searchWithinList(activeFilteredList, criteria));
            } else {
                // No filter, no collection — search full list
                gameListView.setGames(gameController.search(criteria));
            }
        });

        // Filter by genre / players / min rating
        // If a collection is selected, filter within that collection only
        gameListView.addFilterListener(e -> {
            User user = authController.getCurrentUser();
            String selectedCollection = gameListView.getSelectedCollectionName();

            List<Game> base;
            if (user != null && selectedCollection != null && !selectedCollection.isBlank()) {
                base = gameController.getGamesByCollection(user, selectedCollection);
            } else {
                base = gameController.getAllGames();
            }

            activeFilteredList = gameController.filterWithinList(base,
                    gameListView.getGenreFilter(),
                    gameListView.getPlayersFilter(),
                    gameListView.getMinRatingFilter());
            gameListView.setGames(activeFilteredList);
        });

        // Clear filters — goes back to the collection if one is selected, else full list
        gameListView.addClearFiltersListener(e -> {
            activeFilteredList = null;
            User user = authController.getCurrentUser();
            String selectedCollection = gameListView.getSelectedCollectionName();
            if (user != null && selectedCollection != null && !selectedCollection.isBlank()) {
                gameListView.setGames(gameController.getGamesByCollection(user, selectedCollection));
            } else {
                gameListView.setGames(gameController.getAllGames());
            }
        });

        // View details — guard: must have a selection
        gameListView.addViewDetailsListener(e -> {
            Game selected = gameListView.getSelectedGame();
            if (selected == null) {
                JOptionPane.showMessageDialog(frame,
                        "Please select a game first.",
                        "No Selection", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            navigateToGameDetail(selected);
        });

        // Click recently-viewed row → open detail
        gameListView.addRecentlyViewedClickListener(e -> {
            Game selected = gameListView.getSelectedRecentlyViewedGame();
            if (selected != null) navigateToGameDetail(selected);
        });

        // Add Game (admin only)
        gameListView.addAddGameListener(e -> {
            String title = gameListView.getSearchCriteria();
            if (title.isBlank()) {
                JOptionPane.showMessageDialog(frame,
                        "Type a new game title in the Search field, then click Add Game.",
                        "No Title", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Add \"" + title + "\" as a new custom game entry?",
                    "Add Game", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                gameController.addGame(new Game(title));
                refreshGameList();
            }
        });

        // Delete the selected game from the master list (admin only)
        gameListView.addDeleteGameListener(e -> {
            Game selected = gameListView.getSelectedGame();
            if (selected == null) {
                JOptionPane.showMessageDialog(frame,
                        "Please select a game to delete.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Delete \"" + selected.getTitle() + "\"?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int realIndex = gameController.getAllGames().indexOf(selected);
                if (realIndex >= 0) {
                    gameController.deleteGame(realIndex);
                    refreshGameList();
                }
            }
        });

        // Collection dropdown — show only that collection's games
        // Only fires when user manually picks a collection, not on setModel rebuild
        gameListView.addCollectionSelectionListener(e -> {
            activeFilteredList = null; // switching collections resets any active filter
            User user = authController.getCurrentUser();
            String name = gameListView.getSelectedCollectionName();
            if (user != null && name != null && !name.isBlank()) {
                gameListView.setGames(gameController.getGamesByCollection(user, name));
            } else {
                gameListView.setGames(gameController.getAllGames());
            }
        });

        // Create a new collection
        gameListView.addCreateCollectionListener(e -> {
            User user = authController.getCurrentUser();
            if (user == null) {
                JOptionPane.showMessageDialog(frame,
                        "Log in to create collections.",
                        "Not Logged In", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String name = gameListView.getNewCollectionName();
            if (name.isBlank()) {
                JOptionPane.showMessageDialog(frame,
                        "Enter a collection name first.",
                        "No Name", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (collectionController.createCollection(user, name)) {
                userDatabase.saveUsers(); // R6 — persist immediately
                gameListView.setCollectionNames(user.getCollectionNames());
                gameListView.clearNewCollectionField();
                gameListView.resetCollectionSelection();
                refreshGameList();
            } else {
                JOptionPane.showMessageDialog(frame,
                        "A collection named \"" + name + "\" already exists.",
                        "Duplicate Name", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Delete the selected collection
        gameListView.addDeleteCollectionListener(e -> {
            User user = authController.getCurrentUser();
            String name = gameListView.getSelectedCollectionName();
            if (user == null || name == null || name.isBlank()) {
                JOptionPane.showMessageDialog(frame,
                        "Select a collection to delete.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Delete collection \"" + name + "\"?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                collectionController.deleteCollection(user, name);
                userDatabase.saveUsers(); // R6 — persist immediately
                gameListView.setCollectionNames(user.getCollectionNames());
                refreshGameList();
            }
        });

        // Open the dedicated collection manager screen
        gameListView.addManageCollectionsListener(e -> {
            User user = authController.getCurrentUser();
            if (user == null) {
                JOptionPane.showMessageDialog(frame,
                        "Log in to manage collections.",
                        "Not Logged In", JOptionPane.WARNING_MESSAGE);
                return;
            }
            collectionListView.setCollections(user.getCollections());
            showCard(CARD_COLLECTIONS);
        });

        // Logout — R5: prompt user to save before logging out
        gameListView.addLogoutListener(e -> {
            User user = authController.getCurrentUser();
            if (user != null) {
                int choice = JOptionPane.showConfirmDialog(frame,
                        "Save your collections before logging out?",
                        "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION);
                if (choice == JOptionPane.CANCEL_OPTION) return;
                if (choice == JOptionPane.YES_OPTION) {
                    userDatabase.saveUsers(); // R5 + R6 — explicit save on logout
                }
            }
            authController.logout();
            activeFilteredList = null;
            gameController.clearGuestRecentlyViewed();
            gameListView.setRecentlyViewedGames(gameController.getRecentlyViewed(null));
            loginView.clearAllInputs();
            gameListView.setAdminControlsVisible(false);
            showCard(CARD_LOGIN);
        });
    }


    private void setupGameDetailView() {

        // Back to game list
        gameDetailView.addBackListener(e -> showCard(CARD_GAME_LIST));

        // Submit a review — must be logged in, comment must not be empty
        gameDetailView.addSubmitReviewListener(e -> {
            Game game = gameDetailView.getDisplayedGame();
            User user = authController.getCurrentUser();

            if (user == null) {
                JOptionPane.showMessageDialog(frame,
                        "Log in to submit reviews.",
                        "Not Logged In", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (game == null) return;

            String comment = gameDetailView.getEnteredReviewComment();
            if (comment.isBlank()) {
                JOptionPane.showMessageDialog(frame,
                        "Write a comment before submitting.",
                        "Empty Review", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int rating = gameDetailView.getEnteredReviewRating();
            gameController.addReview(game, user, rating, comment);
            gameDetailView.setDisplayedReviews(game.getReviews());
            gameDetailView.clearReviewForm();
        });

        // Add the displayed game to one of the user's collections
        gameDetailView.addAddToCollectionListener(e -> {
            User user = authController.getCurrentUser();
            Game game = gameDetailView.getDisplayedGame();

            if (user == null) {
                JOptionPane.showMessageDialog(frame,
                        "Log in to add games to collections.",
                        "Not Logged In", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (game == null) return;

            List<String> names = user.getCollectionNames();
            if (names.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "You have no collections. Create one from the main screen first.",
                        "No Collections", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] options = names.toArray(new String[0]);
            String chosen = (String) JOptionPane.showInputDialog(
                    frame,
                    "Add \"" + game.getTitle() + "\" to which collection?",
                    "Add to Collection",
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

            if (chosen != null) {
                collectionController.addGameToCollection(user, game, chosen);
                userDatabase.saveUsers(); // R6 — persist immediately
                JOptionPane.showMessageDialog(frame,
                        "\"" + game.getTitle() + "\" added to \"" + chosen + "\".",
                        "Added", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }


    private void setupCollectionListView() {

        // Back to game list
        collectionListView.addBackListener(e -> showCard(CARD_GAME_LIST));

        // Create collection via prompt dialog
        collectionListView.addCreateCollectionListener(e -> {
            User user = authController.getCurrentUser();
            if (user == null) return;
            String name = collectionListView.promptForCollectionName();
            if (name == null || name.isBlank()) return;
            if (collectionController.createCollection(user, name)) {
                userDatabase.saveUsers(); // R6 — persist immediately
                collectionListView.setCollections(user.getCollections());
                gameListView.setCollectionNames(user.getCollectionNames());
            } else {
                JOptionPane.showMessageDialog(frame,
                        "A collection named \"" + name + "\" already exists.",
                        "Duplicate Name", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Delete selected collection
        collectionListView.addDeleteCollectionListener(e -> {
            User user = authController.getCurrentUser();
            Collection selected = collectionListView.getSelectedCollection();
            if (user == null || selected == null) {
                JOptionPane.showMessageDialog(frame,
                        "Select a collection to delete.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Delete \"" + selected.getName() + "\"?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                collectionController.deleteCollection(user, selected.getName());
                userDatabase.saveUsers(); // R6 — persist immediately
                collectionListView.setCollections(user.getCollections());
                gameListView.setCollectionNames(user.getCollectionNames());
            }
        });

        // Remove selected game from selected collection
        collectionListView.addRemoveGameListener(e -> {
            User user = authController.getCurrentUser();
            Collection col = collectionListView.getSelectedCollection();
            Game game      = collectionListView.getSelectedGameInCollection();

            if (col == null || game == null) {
                JOptionPane.showMessageDialog(frame,
                        "Select a collection and a game to remove.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            collectionController.removeGameFromCollection(user, game, col.getName());
            userDatabase.saveUsers(); // R6 — persist immediately
            collectionListView.setCollections(user.getCollections());
        });
    }

    /**
     * Navigates to the game detail screen for the given game.
     * Updates recently viewed, shows reviews.
     */
    private void navigateToGameDetail(Game game) {
        User user = authController.getCurrentUser();
        gameController.viewDetails(game, user);
        gameListView.setRecentlyViewedGames(gameController.getRecentlyViewed(user));
        gameDetailView.displayGame(game);
        gameDetailView.setDisplayedReviews(game.getReviews());
        showCard(CARD_GAME_DETAIL);
    }

    /** Switches the active card in the CardLayout. */
    private void showCard(String name) {
        cardLayout.show(cardPanel, name);
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Reloads the game list, recently viewed, and collection dropdown
     * based on the current session state.
     */
    private void refreshGameList() {
        User user = authController.getCurrentUser();
        gameListView.setGames(gameController.getAllGames());
        gameListView.setRecentlyViewedGames(gameController.getRecentlyViewed(user));
        gameListView.setCollectionNames(
                user != null ? user.getCollectionNames() : Collections.emptyList());
    }

    /**
     * After UserDatabase and GameDatabase have both loaded, this method
     * walks every user's collections and replaces stored gameId strings
     * with actual Game objects from the GameDatabase.
     *
     * This is necessary because users.xml only stores game IDs (not full
     * game data), and the GameDatabase is the authoritative source for games.
     */
    private void resolveCollectionPlaceholders() {
        for (String username : userDatabase.getAllUsernames()) {
            User user = userDatabase.getUser(username);
            if (user == null) continue;
            for (Collection col : user.getCollections()) {
                List<String> pendingIds = col.getPendingGameIds();
                for (String gameId : pendingIds) {
                    Game game = gameDatabase.getGameByID(gameId);
                    if (game != null) col.addGame(game);
                }
                col.clearPendingGameIds();
            }
        }
    }
}