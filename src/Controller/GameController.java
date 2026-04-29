package Controller;

import Model.*;
import Model.Collection;

import java.util.*;

/**
 * Handles all game operations: searching, filtering, viewing details,
 * review submission, and recently viewed tracking.
 */
public class GameController {

    private final GameDatabase gameDatabase;
    private final List<Game> recentlyViewed;

    /**
     * @param gameDatabase the loaded game database to operate on
     */
    public GameController(GameDatabase gameDatabase) {
        this.gameDatabase   = gameDatabase;
        this.recentlyViewed = new ArrayList<>();
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
     * Loads the detail view for a game and tracks it in recently viewed.
     *
     * @param game        the game to view
     * @param currentUser the active session user, or null for guests
     * @return the same game (for chaining), or null if game was null
     */
    public Game viewDetails(Game game, User currentUser) {
        if (game == null) return null;
        if (currentUser != null) currentUser.getRecentlyViewed().add(game);
        addRecentlyViewed(game);
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
     * @param genre     genre string to match, empty string = any
     * @param players   minimum player count, 0 = any
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
            if (players > 0 && game.getMaxPlayers() < players)
                matches = false;
            if (minRating >= 0 && game.getAverageRating() < minRating)
                matches = false;
            if (matches) results.add(game);
        }
        return results;
    }

    /**
     * Adds a game to the top of the recently viewed list.
     * Removes duplicates and caps the list at 10 entries.
     *
     * @param game the game that was just viewed
     */
    public void addRecentlyViewed(Game game) {
        if (game == null) return;
        recentlyViewed.remove(game);
        recentlyViewed.add(0, game);
        if (recentlyViewed.size() > 10) recentlyViewed.remove(recentlyViewed.size() - 1);
    }

    /**
     * @return a snapshot of the recently viewed list (newest first, max 10)
     */
    public List<Game> getRecentlyViewed() {
        return new ArrayList<>(recentlyViewed);
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