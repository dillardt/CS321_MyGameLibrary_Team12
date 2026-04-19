package Model;

public class Review {

    private int rating;
    private String comment;
    private final User author;
    private final Game game;

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

    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public User getAuthor() { return author; }
    public Game getGame() { return game; }

    public boolean editReview(String newComment, int newRating) {}
    public String getSummary() {}

    @Override
    public String toString() {}
}