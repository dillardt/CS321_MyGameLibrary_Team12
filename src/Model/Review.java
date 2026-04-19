package Model;

public class Review {

    private int rating;
    private String comment;
    private User author;
    private Game game;

    public Review(int rating, String comment, User author, Game game) {}

    public int getRating() {}
    public String getComment() {}
    public User getAuthor() {}
    public Game getGame() {}

    public boolean editReview(String newComment, int newRating) {}
    public String getSummary() {}

    @Override
    public String toString() {}
}