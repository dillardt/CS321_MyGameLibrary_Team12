package Controller;

import Model.Game;
import Model.GameDatabase;
import Model.UserDatabase;
import Model.User;
import View.GameListView;
import View.GameDetailView;
import View.LoginView;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.util.List;

/**
 * Central coordinator for the entire application.
 *
 * <p>This class manages:
 * <ul>
 *     <li>View switching (Login → Game List → Game Details)</li>
 *     <li>Wiring UI events to controllers</li>
 *     <li>Synchronizing Model and View layers</li>
 *     <li>Handling user authentication and session state</li>
 * </ul>
 *
 * <p>AppCoordinator acts as the "glue" between MVC layers and ensures
 * that UI actions trigger the correct controller logic.
 */
public class AppCoordinator {

    /** Main application window. */
    private final JFrame frame;

    /** Login screen view. */
    private final LoginView loginView;

    /** Main game list / search screen. */
    private final GameListView gameListView;

    /** Detailed game information screen. */
    private final GameDetailView gameDetailView;

    /** Handles authentication logic. */
    private final AuthenticationController authController;

    /** Handles game-related operations. */
    private final GameController gameController;

    /** Handles user collection operations. */
    private final CollectionController collectionController;

    /** Persistent user database. */
    private final UserDatabase userDatabase;

    /** Persistent game database. */
    private final GameDatabase gameDatabase;

    /**
     * Constructs the AppCoordinator and initializes all core components.
     * Sets up controllers, views, listeners, and displays the login screen.
     */
    public AppCoordinator() {

        frame = new JFrame("My Game Library");

        userDatabase = new UserDatabase("data/users.xml");
        gameDatabase = new GameDatabase("data/bgg90Games.xml");



        authController = new AuthenticationController(userDatabase);
        gameController = new GameController(gameDatabase);
        collectionController = new CollectionController(); // matches your latest CollectionController

        loginView = new LoginView();
        gameListView = new GameListView();
        gameDetailView = new GameDetailView();

        setupLoginView();
        setupGameListView();
        setupGameDetailView();

        showLoginView();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 650);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // =========================================================
    // LOGIN VIEW SETUP
    // =========================================================

    /**
     * Wires login view buttons to authentication logic.
     * Handles login, account creation, and guest login.
     */
    private void setupLoginView() {

        loginView.addLoginListener(e -> {
            String username = loginView.getUsernameInput();
            String password = loginView.getPasswordInput();

            if (authController.login(username, password)) {
                loginView.setStatusMessage("Login successful!", false);
                refreshGameList();
                showGameListView();
            } else {
                loginView.setStatusMessage("Invalid credentials", true);
                loginView.clearPassword();
            }
        });

        loginView.addCreateAccountListener(e -> {
            String username = loginView.getUsernameInput();
            String password = loginView.getPasswordInput();

            if (authController.createAccount(username, password)) {
                loginView.setStatusMessage("Account created!", false);
            } else {
                loginView.setStatusMessage("Username already exists", true);
            }
        });

        loginView.addGuestLoginListener(e -> {
            authController.logout();
            refreshGameList();
            showGameListView();
        });
    }

    // =========================================================
    // GAME LIST VIEW SETUP
    // =========================================================

    /**
     * Wires all game list UI actions to GameController logic.
     * Handles searching, filtering, adding, deleting, viewing details,
     * selecting collections, and logging out.
     */
    private void setupGameListView() {

        refreshGameList();

        gameListView.addAddGameListener(e -> {
            String title = gameListView.getSearchCriteria();
            if (!title.isEmpty()) {
                gameController.addGame(new Game(title));
                refreshGameList();
            }
        });

        gameListView.addDeleteGameListener(e -> {
            int index = gameListView.getSelectedGameIndex();
            if (index >= 0) {
                gameController.deleteGame(index);
                refreshGameList();
            }
        });

        gameListView.addSearchListener(e -> {
            List<Game> results = gameController.search(gameListView.getSearchCriteria());
            gameListView.setGames(results);
        });

        gameListView.addFilterListener(e -> {
            List<Game> results = gameController.filterGames(
                    gameListView.getGenreFilter(),
                    gameListView.getPlayersFilter(),
                    gameListView.getMinRatingFilter()
            );
            gameListView.setGames(results);
        });

        gameListView.addViewDetailsListener(e -> {
            Game selected = gameListView.getSelectedGame();
            if (selected != null) {

                User user = authController.getCurrentUser();

                gameController.viewDetails(selected, user);

                gameListView.setRecentlyViewedGames(
                        gameController.getRecentlyViewed()
                );

                gameDetailView.displayGame(selected);
                // If Game later exposes reviews, you can call:
                // gameDetailView.setDisplayedReviews(selected.getReviews());

                showGameDetailView();
            }
        });

        gameListView.addCollectionSelectionListener(e -> {
            User user = authController.getCurrentUser();
            String collection = gameListView.getSelectedCollectionName();

            if (user != null && collection != null) {
                List<Game> games = gameController.getGamesByCollection(user, collection);
                gameListView.setGames(games);
            }
        });

        gameListView.addLogoutListener(e -> {
            authController.logout();
            showLoginView();
        });
    }

    // =========================================================
    // GAME DETAIL VIEW SETUP
    // =========================================================

    /**
     * Wires review submission and collection-adding actions
     * to the appropriate controllers. Also adds a Back button
     * to return to the game list view.
     */
    private void setupGameDetailView() {

        gameDetailView.addSubmitReviewListener(e -> {
            Game game = gameDetailView.getDisplayedGame();
            User user = authController.getCurrentUser();

            if (game != null && user != null) {
                int rating = gameDetailView.getEnteredReviewRating();
                String comment = gameDetailView.getEnteredReviewComment();

                gameController.addReview(game, user, rating, comment);

                // If Game later stores reviews, you can refresh:
                // gameDetailView.setDisplayedReviews(game.getReviews());

                gameDetailView.clearReviewForm();
            }
        });

        gameDetailView.addAddToCollectionListener(e -> {
            User user = authController.getCurrentUser();
            Game game = gameDetailView.getDisplayedGame();

            if (user != null && game != null) {
                String collectionName = gameListView.getSelectedCollectionName();
                if (collectionName != null) {
                    collectionController.addGameToCollection(user, game, collectionName);
                }
            }
        });

        gameListView.addCreateCollectionListener(e -> {
            User user = authController.getCurrentUser();
            String name = gameListView.getNewCollectionName();

            if (collectionController.createCollection(user, name)) {
                gameListView.setCollectionNames(user.getCollectionNames());
            }
        });

        gameListView.addDeleteCollectionListener(e -> {
            User user = authController.getCurrentUser();
            String name = gameListView.getSelectedCollectionName();

            if (user != null && name != null) {
                collectionController.deleteCollection(user, name);
                gameListView.setCollectionNames(user.getCollectionNames());
                gameListView.setGames(gameController.getAllGames()); // reset view
            }
        });



        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> showGameListView());
        gameDetailView.add(backButton, BorderLayout.WEST);
    }

    // =========================================================
    // VIEW SWITCHING
    // =========================================================

    /**
     * Displays the login screen.
     */
    private void showLoginView() {
        frame.setContentPane(loginView);
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Displays the main game list screen.
     */
    private void showGameListView() {
        frame.setContentPane(gameListView);
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Displays the detailed game information screen.
     */
    private void showGameDetailView() {
        frame.setContentPane(gameDetailView);
        frame.revalidate();
        frame.repaint();
    }


    // =========================================================
    // DATA REFRESH
    // =========================================================

    /**
     * Reloads the game list and recently viewed list from the controller.
     */
    private void refreshGameList() {
        gameListView.setGames(gameController.getAllGames());
        gameListView.setRecentlyViewedGames(gameController.getRecentlyViewed());
        User user = authController.getCurrentUser();
        if (user != null) {
            gameListView.setCollectionNames(user.getCollectionNames());
        }

    }



}
