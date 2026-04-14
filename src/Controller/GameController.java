package Controller;

import Model.Game;
import Model.GameDatabase;
import Model.Review;
import Model.User;
import java.util.List;

public class GameController {

    private GameDatabase gameDatabase;

    public GameController(GameDatabase gameDatabase) {

    }

    public List<Game> search(String criteria) { return null; }
    public List<Game> filter(String genre, int players, double minRating) { return null; }
    public Game viewDetails(String gameID, User currentUser) { return null; }
    public Review annotateGame(Game game, User currentUser, int rating, String comment) { return null; }
}
