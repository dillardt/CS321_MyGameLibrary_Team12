package Model;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/** Loads and manages all board games from XML — supports search, filter, and ID lookup.
 *  * XML format expected:
 *            <games>
 *                <game id="...">
 *                <title value="..."/>
 *                <genre value="..."/>
 *                <minPlayers value="..."/>
 *                <maxPlayers value="..."/>
 *                <description value="..."/>
 *                <imagePath value="..."/>
 *                <averageRating value="..."/>
 *              </game>
 *            </games>
 */

public class GameDatabase {

    private List<Game> games;
    private final String fileLocation;

    /**
     * Sets up the database pointing at the given file — call loadGames() to actually read it.
     * @param fileLocation path to the XML file
     */
    public GameDatabase(String fileLocation) {
        this.fileLocation = fileLocation;
        this.games        = new ArrayList<>();
    }

    /** Reads all games from the XML file — skips malformed entries, warns if file is missing. */
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

    /**
     * Parses one <game> XML node into a Game object — returns null if the entry is malformed.
     * @param gameNode  the XML node to parse
     * @param entryNum  position in file, used for warning messages
     * @return a Game object, or null if parsing failed
     */
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

    /**
     * Reads a value attribute from a named child node — e.g. <title value="..."/>
     * @param parent    the parent XML node
     * @param fieldName the tag name to look for
     * @return the value string, or null if not found
     */
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

    /**
     * Same as parseTextField but parses the result as an int — defaults to 0 if missing or invalid.
     * @param parent    the parent XML node
     * @param fieldName the tag name to look for
     * @return parsed int, or 0 on failure
     */
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

    /**
     * Same as parseTextField but parses the result as a double — defaults to 0.0 if missing or invalid.
     * @param parent    the parent XML node
     * @param fieldName the tag name to look for
     * @return parsed double, or 0.0 on failure
     */
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

    /**
     * Finds a game by its unique ID.
     * @param gameID the ID to search for
     * @return matching game, or null if not found
     */
    public Game getGameByID(String gameID) {
        if (gameID == null || gameID.isBlank()) return null;
        for (Game game : games) {
            if (game.getGameID() != null && game.getGameID().equals(gameID)) return game;
        }
        return null;
    }

    /** @return defensive copy of all loaded games */
    public List<Game> getAll() { return new ArrayList<>(games); }

    /**
     * Returns all games whose title/genre match the criteria string.
     * @param criteria the search text
     * @return matching games, or empty list if criteria is blank
     */
    public List<Game> searchGames(String criteria) {
        List<Game> results = new ArrayList<>();
        if (criteria == null || criteria.isBlank()) return results;
        for (Game game : games) {
            if (game.matchesSearch(criteria)) results.add(game);
        }
        return results;
    }

    /**
     * Filters games by genre, player count, and min rating — pass null/0/-1 to skip a filter.
     * @param genre       genre to match, or null to skip
     * @param playerCount required player count, or 0 to skip
     * @param minRating   minimum rating 0-10, or -1 to skip
     * @return games passing all active filters
     */
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

    /** e.g. "GameDatabase | File: games.xml | Games loaded: 42" */
    @Override
    public String toString() {
        return "GameDatabase | File: " + fileLocation
                + " | Games loaded: " + games.size();
    }
}