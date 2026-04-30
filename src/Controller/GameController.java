package Controller;

import Model.*;
import Model.Collection;

import java.util.*;

/**
 * Handles all game operations: searching, filtering, viewing details,
 * review submission, and recently viewed tracking.
 *
 * Recently viewed is stored on the User object when a user is logged in,
 * so the list persists across logout/login for the same account.
 * Guests use the controller's own temporary list, which is cleared on logout.
 */
public class GameController {

    private final GameDatabase gameDatabase;

    // Guest-only fallback list — used when no user is logged in.
    // When a real user is active, recentlyViewed lives on User instead.
    private final List<Game> guestRecentlyViewed;

    /**
     * @param gameDatabase the loaded game database to operate on
     */
    public GameController(GameDatabase gameDatabase) {
        this.gameDatabase        = gameDatabase;
        this.guestRecentlyViewed = new ArrayList<>();
    }

    /**
     * @return every game currently in the database
     */
    public List<Game> getAllGames() {
        return gameDatabase.getAllGames();
    }

    /**
     * Searches the full game database by title keyword.
     *
     * @param criteria keyword to match (case-insensitive)
     * @return matching games, empty list if nothing matches
     */
    public List<Game> search(String criteria) {
        return gameDatabase.searchGames(criteria);
    }

    /**
     * Searches within a provided list of games rather than the full database.
     * Used for R8 — searching inside a personal collection.
     *
     * @param games    the list to search within
     * @param criteria keyword to match
     * @return matching games from the provided list
     */
    public List<Game> searchWithinList(List<Game> games, String criteria) {
        if (games == null || criteria == null || criteria.isBlank()) return games;
        List<Game> results = new ArrayList<>();
        for (Game g : games) {
            if (g.matchesSearch(criteria)) results.add(g);
        }
        return results;
    }

    /**
     * Records that a game was viewed and returns it.
     * If a user is logged in, the list is stored on their User object so it
     * survives logout and is restored when they log back in.
     * Guests use the controller's own temporary list, cleared on logout.
     *
     * @param game        the game to view
     * @param currentUser the active session user, or null for guests
     * @return the same game, or null if game was null
     */
    public Game viewDetails(Game game, User currentUser) {
        if (game == null) return null;
        addRecentlyViewed(game, currentUser);
        return game;
    }

    /**
     * Returns all games in a specific user collection.
     *
     * @param user           the logged-in user
     * @param collectionName name of the collection to retrieve
     * @return games in that collection, empty list if not found
     */
    public List<Game> getGamesByCollection(User user, String collectionName) {
        List<Game> results = new ArrayList<>();
        if (user == null || collectionName == null) return results;
        for (Collection c : user.getCollections()) {
            if (collectionName.equals(c.getName())) {
                results.addAll(c.getGames());
                break;
            }
        }
        return results;
    }

    /**
     * Creates a review, attaches it to the game and user, then
     * recalculates the game's average rating.
     *
     * @param game        the game being reviewed
     * @param currentUser the user submitting the review
     * @param rating      numeric rating 1-10
     * @param comment     written review text
     * @return the created Review, or null if either argument was null
     */
    public Review addReview(Game game, User currentUser, int rating, String comment) {
        if (game == null || currentUser == null) return null;
        Review review = new Review(rating, comment, currentUser, game);
        currentUser.addReview(review);
        game.addReview(review);
        recalculateAverage(game);
        return review;
    }

    /**
     * Adds a custom game entry to the database (admin only).
     *
     * @param game the game to add
     */
    public void addGame(Game game) {
        if (game != null) gameDatabase.getAllGames().add(game);
    }

    /**
     * Removes a game from the database by its list index (admin only).
     *
     * @param index position in the full game list
     */
    public void deleteGame(int index) {
        List<Game> games = gameDatabase.getAllGames();
        if (index >= 0 && index < games.size()) games.remove(index);
    }

    /**
     * Filters the full game list by genre, player count, and minimum rating.
     * Any parameter can be set to its "no filter" value (empty string, 0, or -1)
     * to skip that filter.
     *
     * Player count filter: the selected number must fall within the game's
     * supported player range (minPlayers <= selected <= maxPlayers).
     * For example, selecting 1 excludes Secret Hitler (5-10 players) because
     * 1 is below its minimum of 5.
     *
     * @param genre     genre string to match, empty string = any
     * @param players   exact player count to support, 0 = any
     * @param minRating minimum average rating, -1 = any
     * @return games that pass all active filters
     */
    public List<Game> filterGames(String genre, int players, double minRating) {
        List<Game> results = new ArrayList<>();
        for (Game game : gameDatabase.getAllGames()) {
            boolean matches = true;
            if (genre != null && !genre.isEmpty()
                    && (game.getGenre() == null || !game.getGenre().equalsIgnoreCase(genre)))
                matches = false;
            // Check that the selected player count falls within the game's supported range
            if (players > 0
                    && (players < game.getMinPlayers() || players > game.getMaxPlayers()))
                matches = false;
            if (minRating >= 0 && game.getAverageRating() < minRating)
                matches = false;
            if (matches) results.add(game);
        }
        return results;
    }

    /**
     * Adds a game to the top of the correct recently viewed list.
     * Uses the user's own list if logged in, otherwise the guest list.
     * Removes duplicates and caps the list at 10 entries.
     *
     * @param game        the game that was just viewed
     * @param currentUser the active session user, or null for guests
     */
    public void addRecentlyViewed(Game game, User currentUser) {
        if (game == null) return;
        List<Game> list = (currentUser != null)
                ? currentUser.getRecentlyViewed()
                : guestRecentlyViewed;
        list.remove(game);
        list.add(0, game);
        if (list.size() > 10) list.remove(list.size() - 1);
    }

    /**
     * Returns a snapshot of the recently viewed list for the given session.
     * If a user is logged in, returns their personal list.
     * If guest, returns the temporary guest list.
     *
     * @param currentUser the active session user, or null for guests
     * @return recently viewed games, newest first, max 10
     */
    public List<Game> getRecentlyViewed(User currentUser) {
        List<Game> list = (currentUser != null)
                ? currentUser.getRecentlyViewed()
                : guestRecentlyViewed;
        return new ArrayList<>(list);
    }

    /**
     * Clears the guest recently viewed list on logout.
     * Registered users keep their list on their User object — no clearing needed.
     */
    public void clearGuestRecentlyViewed() {
        guestRecentlyViewed.clear();
    }

    /**
     * Recomputes the average rating for a game from all its reviews.
     * Called automatically after every new review submission.
     *
     * @param game the game whose average needs updating
     */
    private void recalculateAverage(Game game) {
        List<Review> reviews = game.getReviews();
        if (reviews.isEmpty()) { game.setAverageRating(0.0); return; }
        double sum = 0;
        for (Review r : reviews) sum += r.getRating();
        game.setAverageRating(sum / reviews.size());
    }
}