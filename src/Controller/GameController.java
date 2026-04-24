package Controller;

import Model.Collection;
import Model.Game;
import Model.GameDatabase;
import Model.Review;
import Model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all game-related operations including searching,
 * filtering, viewing details, and adding reviews.
 */
public class GameController {

    private final GameDatabase gameDatabase;
    private final List<Game> recentlyViewed;

    public GameController(GameDatabase gameDatabase) {
        this.gameDatabase = gameDatabase;
        this.recentlyViewed = new ArrayList<>();
    }

    /**
     * Returns all games in the database.
     */
    public List<Game> getAllGames() {
        return gameDatabase.getAllGames();
    }

    /**
     * Searches games by title keyword.
     */
    public List<Game> search(String criteria) {
        return gameDatabase.searchGames(criteria);
    }

    /**
     * Returns game details and tracks recently viewed list.
     */
    public Game viewDetails(Game game, User currentUser) {
        if (game == null) {
            return null;
        }

        if (currentUser != null) {
            currentUser.getRecentlyViewed().add(game);
        }

        addRecentlyViewed(game);
        return game;
    }

    /**
     * Returns all games inside a specific user collection.
     *
     * @param user           current logged-in user
     * @param collectionName name of the collection
     * @return list of games in that collection, or empty list if not found
     */
    public List<Game> getGamesByCollection(User user, String collectionName) {
        List<Game> results = new ArrayList<>();

        if (user == null || collectionName == null) {
            return results;
        }

        for (Collection c : user.getCollections()) {
            if (collectionName.equals(c.getName())) {
                results.addAll(c.getGames());
                break;
            }
        }

        return results;
    }

    /**
     * Adds a review to a game by a user.
     */
    public Review addReview(Game game, User currentUser, int rating, String comment) {
        if (game == null || currentUser == null) {
            return null;
        }
        Review review = new Review(rating, comment, currentUser, game);
        currentUser.addReview(review);
        game.addReview(review);
        recalculateAverage(game);

        // If Game also tracks reviews, you could add: game.addReview(review);
        return review;
    }

    /**
     * Adds a new game to the database.
     */
    public void addGame(Game game) {
        if (game == null) {
            return;
        }
        gameDatabase.getAllGames().add(game);
    }

    /**
     * Deletes a game by list index.
     */
    public void deleteGame(int index) {
        List<Game> games = gameDatabase.getAllGames();
        if (index >= 0 && index < games.size()) {
            games.remove(index);
        }
    }

    /**
     * Filters games by genre, players, rating.
     */
    public List<Game> filterGames(String genre, int players, double minRating) {
        List<Game> allGames = gameDatabase.getAllGames();
        List<Game> results = new ArrayList<>();

        for (Game game : allGames) {
            boolean matches = true;

            if (genre != null && !genre.isEmpty()
                    && (game.getGenre() == null
                    || !game.getGenre().equalsIgnoreCase(genre))) {
                matches = false;
            }

            if (players > 0 && game.getMaxPlayers() < players) {
                matches = false;
            }

            if (minRating >= 0 && game.getAverageRating() < minRating) {
                matches = false;
            }

            if (matches) {
                results.add(game);
            }
        }

        return results;
    }

    /**
     * Recently viewed tracking.
     */
    public void addRecentlyViewed(Game game) {
        if (game == null) {
            return;
        }

        recentlyViewed.remove(game);
        recentlyViewed.add(0, game);

        if (recentlyViewed.size() > 10) {
            recentlyViewed.remove(recentlyViewed.size() - 1);
        }
    }

    public List<Game> getRecentlyViewed() {
        return new ArrayList<>(recentlyViewed);
    }

    /**
     *
     * @param game is used to track which game and then update their ratings
     */
    private void recalculateAverage(Game game) {
        List<Review> reviews = game.getReviews();
        if (reviews.isEmpty()) {
            game.setAverageRating(0.0);
            return;
        }

        double sum = 0;
        for (Review r : reviews) {
            sum += r.getRating();
        }

        double avg = sum / reviews.size();
        game.setAverageRating(avg);
    }

}
