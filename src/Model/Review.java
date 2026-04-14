package Model;

public class Review {

    private int rating;
    private String comment;
    private User author;
    private Game game;

    public Review(int rating, String comment, User author, Game game) {

    }

    public void editReview(String newText, int newRating) { }
    public void deleteReview() { }

    public int getRating() { return 0; }
    public String getComment() { return null; }
    public User getAuthor() { return null; }
    public Game getGame() { return null; }
}
