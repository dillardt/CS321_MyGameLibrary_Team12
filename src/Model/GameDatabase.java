package Model;

import java.util.List;

public class GameDatabase {

    private List<Game> games;
    private String filePath;

    public GameDatabase(String filePath) {

    }

    public void loadGames() { }
    public void saveGames() { }
    public List<Game> searchGames(String criteria) { return null; }
    public Game getGameByID(String id) { return null; }
    public List<Game> filterGames(String genre, int players, double minRating) { return null; }
    public List<Game> getAll() { return null; }
}