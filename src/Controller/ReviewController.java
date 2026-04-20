package Controller;

import Model.Game;
import Model.Review;
import Model.User;

public class ReviewController {

    public ReviewController() {
    }

    public boolean addReview(User user, Game game, int rating, String comment) {
        return false;
    }

    public boolean editReview(User user, Game game, String newComment, int newRating) {
        return false;
    }

    public boolean deleteReview(User user, Game game) {
        return false;
    }

    public String toString() {
        return "ReviewController";
    }

}