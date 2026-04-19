package Model;

import java.util.ArrayList;
import java.util.List;

public class Collection {

    private String name;
    private List<Game> games;

    public Collection(String name) {
        this.name  = name;
        this.games = new ArrayList<>();
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getSize() { return games.size(); }

    public List<Game> getGames() { return new ArrayList<>(games); }

    public boolean containsGame(Game game) {
        if (game == null) return false;
        return games.contains(game);
    }

    @Override
    public String toString() {
        return name + " (" + games.size() + " games)";
    }

    public boolean addGame(Game game) {}
    public boolean removeGame(Game game) {}
    public Game getGameById(String gameID) {}
}