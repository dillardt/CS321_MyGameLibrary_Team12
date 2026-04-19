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

    public boolean addGame(Game game) {
        if (game == null) return false;
        if (games.contains(game)) return false;
        games.add(game);
        return true;
    }

    public boolean removeGame(Game game) {
        if (game == null) return false;
        return games.remove(game);
    }

    public boolean containsGame(Game game) {
        if (game == null) return false;
        return games.contains(game);
    }

    public Game getGameById(String gameID) {
        if (gameID == null || gameID.isBlank()) return null;
        for (Game game : games) {
            if (game.getGameID().equals(gameID)) return game;
        }
        return null;
    }

    @Override
    public String toString() {
        return name + " (" + games.size() + " games)";
    }
}