package Model;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameDatabase {

    private List<Game> games;
    private final String fileLocation;

    public GameDatabase(String fileLocation) {
        this.fileLocation = fileLocation;
        this.games        = new ArrayList<>();
    }

    public List<Game> getAll() { return new ArrayList<>(games); }

    public Game getGameByID(String gameID) {
        if (gameID == null || gameID.isBlank()) return null;
        for (Game game : games) {
            if (game.getGameID() != null && game.getGameID().equals(gameID)) return game;
        }
        return null;
    }

    @Override
    public String toString() {
        return "GameDatabase | File: " + fileLocation
                + " | Games loaded: " + games.size();
    }

    public void loadGames() {}
    private Game parseGameNode(Node gameNode, int entryNum) {}
    private String parseTextField(Node parent, String fieldName) {}
    private int parseIntField(Node parent, String fieldName) {}
    private double parseDoubleField(Node parent, String fieldName) {}
    public List<Game> searchGames(String criteria) {}
    public List<Game> filterGames(String genre, int playerCount, double minRating) {}
}