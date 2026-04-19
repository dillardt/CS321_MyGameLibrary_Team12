package Model;

public class Review {

    private int rating;
    private String comment;
    private final User author;
    private final Game game;

    public Review(int rating, String comment, User author, Game game) {
        if (rating < 1) {
            this.rating = 1;
        } else this.rating = Math.min(rating, 10);

        if (comment == null || comment.isBlank()) {
            this.comment = "No comment provided.";
        } else {
            this.comment = comment;
        }

        this.author = author;
        this.game   = game;
    }

    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public User getAuthor() { return author; }
    public Game getGame() { return game; }

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

    public String getSummary() {
        String username   = (author != null) ? author.getUsername() : "Unknown";
        String safeComment = (comment != null) ? comment : "";
        String preview = safeComment.length() > 50
                ? safeComment.substring(0, 50) + "..."
                : safeComment;
        return username + " | Rating: " + rating + "/10 | " + preview;
    }

    @Override
    public String toString() { return getSummary(); }
}