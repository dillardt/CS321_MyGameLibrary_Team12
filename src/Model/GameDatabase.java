package Model;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameDatabase {

    private List<Game> games;
    private String fileLocation;

    public GameDatabase(String fileLocation) {}

    public void loadGames() {}

    private Game parseGameNode(Node gameNode, int entryNum) {}
    private String parseTextField(Node parent, String fieldName) {}
    private int parseIntField(Node parent, String fieldName) {}
    private double parseDoubleField(Node parent, String fieldName) {}

    public Game getGameByID(String gameID) {}
    public List<Game> getAll() {}

    public List<Game> searchGames(String criteria) {}
    public List<Game> filterGames(String genre, int playerCount, double minRating) {}

    @Override
    public String toString() {}
}