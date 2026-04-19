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

    public void loadGames() {
        games.clear();
        File file = new File(fileLocation);

        if (!file.exists()) {
            System.out.println("WARNING: Game database file not found: "
                    + fileLocation + ". Starting with empty game list.");
            return;
        }

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setExpandEntityReferences(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc       = db.parse(file);
            NodeList gameNodes = doc.getElementsByTagName("game");

            for (int i = 0; i < gameNodes.getLength(); i++) {
                Game game = parseGameNode(gameNodes.item(i), i + 1);
                if (game != null) games.add(game);
            }

        } catch (Exception e) {
            System.out.println("ERROR: Failed to parse game database file: "
                    + e.getMessage());
        }
    }

    private Game parseGameNode(Node gameNode, int entryNum) {
        try {
            NamedNodeMap attributes = gameNode.getAttributes();
            Node idNode = attributes.getNamedItem("id");
            if (idNode == null) {
                System.out.println("WARNING: Game entry #" + entryNum
                        + " is missing 'id' attribute. Skipping.");
                return null;
            }
            String gameID = idNode.getNodeValue().trim();

            String title         = parseTextField(gameNode, "title");
            String genre         = parseTextField(gameNode, "genre");
            String description   = parseTextField(gameNode, "description");
            String imagePath     = parseTextField(gameNode, "imagePath");
            int    minPlayers    = parseIntField(gameNode,    "minPlayers");
            int    maxPlayers    = parseIntField(gameNode,    "maxPlayers");
            double averageRating = parseDoubleField(gameNode, "averageRating");

            if (title       == null) title       = "Unknown Title";
            if (genre       == null) genre       = "Unknown Genre";
            if (description == null) description = "No description available.";
            if (imagePath   == null) imagePath   = "";

            return new Game(gameID, title, genre, minPlayers, maxPlayers,
                    description, imagePath, averageRating);

        } catch (Exception e) {
            System.out.println("WARNING: Skipping malformed game entry #"
                    + entryNum + ": " + e.getMessage());
            return null;
        }
    }

    private String parseTextField(Node parent, String fieldName) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName().equals(fieldName)) {
                Node valueNode = child.getAttributes().getNamedItem("value");
                if (valueNode != null) return valueNode.getNodeValue().trim();
            }
        }
        return null;
    }

    private int parseIntField(Node parent, String fieldName) {
        String raw = parseTextField(parent, fieldName);
        if (raw == null) {
            System.out.println("WARNING: Missing int field '" + fieldName + "'. Defaulting to 0.");
            return 0;
        }
        try { return Integer.parseInt(raw); }
        catch (NumberFormatException e) {
            System.out.println("WARNING: Could not parse int field '"
                    + fieldName + "' value: " + raw + ". Defaulting to 0.");
            return 0;
        }
    }

    private double parseDoubleField(Node parent, String fieldName) {
        String raw = parseTextField(parent, fieldName);
        if (raw == null) {
            System.out.println("WARNING: Missing double field '" + fieldName + "'. Defaulting to 0.0.");
            return 0.0;
        }
        try { return Double.parseDouble(raw); }
        catch (NumberFormatException e) {
            System.out.println("WARNING: Could not parse double field '"
                    + fieldName + "' value: " + raw + ". Defaulting to 0.0.");
            return 0.0;
        }
    }

    public Game getGameByID(String gameID) {
        if (gameID == null || gameID.isBlank()) return null;
        for (Game game : games) {
            if (game.getGameID() != null && game.getGameID().equals(gameID)) return game;
        }
        return null;
    }

    public List<Game> getAll() { return new ArrayList<>(games); }

    public List<Game> searchGames(String criteria) {
        List<Game> results = new ArrayList<>();
        if (criteria == null || criteria.isBlank()) return results;
        for (Game game : games) {
            if (game.matchesSearch(criteria)) results.add(game);
        }
        return results;
    }

    public List<Game> filterGames(String genre, int playerCount, double minRating) {
        List<Game> results = new ArrayList<>();
        boolean filterGenre  = (genre != null && !genre.isBlank());
        boolean filterCount  = (playerCount >= 1);
        boolean filterRating = (minRating >= 0.0 && minRating <= 10.0);

        for (Game game : games) {
            if (filterGenre  && !game.getGenre().equalsIgnoreCase(genre)) continue;
            if (filterCount  && (playerCount < game.getMinPlayers()
                    || playerCount > game.getMaxPlayers())) continue;
            if (filterRating && game.getAverageRating() < minRating) continue;
            results.add(game);
        }
        return results;
    }

    @Override
    public String toString() {
        return "GameDatabase | File: " + fileLocation
                + " | Games loaded: " + games.size();
    }
}