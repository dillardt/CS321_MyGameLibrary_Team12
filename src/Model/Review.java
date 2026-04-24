package Model;

/**
 * Represents a user's review of a specific game.
 * Stores a rating, comment, and references to the author and game.
 * Reviews are persistent and publicly viewable.
 */
public class Review {

    private int rating;
    private String comment;
    private User author;
    private Game game;

    /**
     * Constructs a Review.
     * This signature matches what GameController expects:
     * new Review(rating, comment, currentUser, game)
     *
     * @param rating  the numeric rating (e.g. 1-5)
     * @param comment the written review text
     * @param author  the user who wrote the review
     * @param game    the game being reviewed
     */
    public Review(int rating, String comment, User author, Game game) {
        this.rating = rating;
        this.comment = comment;
        this.author = author;
        this.game = game;
    }

    /**
     * Updates the review text and rating.
     *
     * @param newText   the updated comment
     * @param newRating the updated rating
     */
    public void editReview(String newText, int newRating) {
        this.comment = newText;
        this.rating = newRating;
    }

    /**
     * Clears the review content (soft delete).
     */
    public void deleteReview() {
        this.comment = "";
        this.rating = 0;
    }

    // --- Getters ---

    public int getRating()      { return rating; }
    public String getComment()  { return comment; }
    public User getAuthor()     { return author; }
    public Game getGame()       { return game; }

    @Override
    public String toString() {
        return author.getUsername() + " rated " + rating
                + "/10: " + comment;
    }
}