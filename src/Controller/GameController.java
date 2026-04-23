package Controller;

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

    private GameDatabase gameDatabase;

    /**
     * Constructs a GameController with access to the GameDatabase.
     *
     * @param gameDatabase the database containing game data
     */
    public GameController(GameDatabase gameDatabase) {
        this.gameDatabase = gameDatabase;
    }

    /**
     * Searches for games based on given criteria.
     *
     * @param criteria search keyword or phrase
     * @return a list of matching games
     */
    public List<Game> search(String criteria) {
        return gameDatabase.searchGames(criteria);
    }

    /**
     * Retrieves detailed information for a specific game.
     * Also updates the user's recently viewed list.
     *
     * @param gameID the ID of the game
     * @param currentUser the currently logged-in user
     * @return the requested Game object, or null if not found
     */
    public Game viewDetails(String gameID, User currentUser) {
        Game game = gameDatabase.getGameByID(gameID);

        if (currentUser != null && game != null) {
            currentUser.getRecentlyViewed().add(game);
        }

        return game;
    }

    /**
     * Creates and adds a review for a game.
     *
     * @param game the game being reviewed
     * @param currentUser the user writing the review
     * @param rating the rating given to the game
     * @param comment the review comment
     * @return the created Review object
     */
    public Review annotateGame(Game game, User currentUser, int rating, String comment) {
        Review review = new Review(rating, comment, currentUser, game);
        currentUser.addReview(review);
        return review;
    }
}