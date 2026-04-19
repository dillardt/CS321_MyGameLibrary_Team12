package Model;

/** A user's review of a board game — holds a rating, comment, author, and game. */
public class Review {

    private int rating;
    private String comment;
    private final User author;
    private final Game game;

    /**
     * Creates a review — clamps rating to 1-10, defaults comment if blank.
     * @param rating  score 1-10
     * @param comment review text
     * @param author  who wrote it
     * @param game    what game it's for
     */
    public Review(int rating, String comment, User author, Game game) {
        if (rating < 1) {
            this.rating = 1;
        } else if (rating > 10) {
            this.rating = 10;
        } else {
            this.rating = rating;
        }

        if (comment == null || comment.isBlank()) {
            this.comment = "No comment provided.";
        } else {
            this.comment = comment;
        }

        this.author = author;
        this.game   = game;
    }

    /** @return rating between 1 and 10 */
    public int getRating() { return rating; }

    /** @return the comment text */
    public String getComment() { return comment; }

    /** @return the user who wrote this */
    public User getAuthor() { return author; }

    /** @return the game being reviewed */
    public Game getGame() { return game; }

    /**
     * Updates comment and rating — rejects blank comments, clamps rating to 1-10.
     * @param newComment updated text
     * @param newRating  updated score
     * @return true if saved, false if comment was blank
     */
    public boolean editReview(String newComment, int newRating) {
        if (newComment == null || newComment.isBlank()) return false;

        if (newRating < 1) {
            newRating = 1;
        } else if (newRating > 10) {
            newRating = 10;
        }

        this.comment = newComment;
        this.rating  = newRating;
        return true;
    }

    /**
     * One-liner summary, comment truncated to 50 chars.
     * e.g. "alice | Rating: 8/10 | Really enjoyed this one..."
     * @return formatted summary string
     */
    public String getSummary() {
        String username    = (author != null) ? author.getUsername() : "Unknown";
        String safeComment = (comment != null) ? comment : "";
        String preview = safeComment.length() > 50
                ? safeComment.substring(0, 50) + "..."
                : safeComment;
        return username + " | Rating: " + rating + "/10 | " + preview;
    }

    /** Same as getSummary(). */
    @Override
    public String toString() { return getSummary(); }
}