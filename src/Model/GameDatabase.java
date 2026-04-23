package Model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GameDatabase {

    private List<Game> games;
    private String loadedFilePath;

    /**
     * Constructs an empty GameDatabase.
     */
    public GameDatabase() {
        games = new ArrayList<>();
    }

    /**
     * Loads all games from the given XML file.
     *
     * @param filePath path to the XML game data file
     */
    public void loadGames(String filePath) {
        this.loadedFilePath = filePath;
        games.clear();

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));
            doc.getDocumentElement().normalize();

            NodeList items = doc.getElementsByTagName("item");
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);

                String id        = item.getAttribute("id");
                String title     = getAttributeValue(item, "name", "value");
                String yearStr   = getAttributeValue(item, "yearpublished", "value");
                int year         = yearStr.isEmpty() ? 0 : Integer.parseInt(yearStr);
                String thumbnail = getAttributeValue(item, "thumbnail", "value");

                games.add(new Game(id, title, year, thumbnail));
            }
        } catch (Exception e) {
            System.err.println("Failed to load games: " + e.getMessage());
        }
    }

    /**
     * Saves the current games list back to the loaded XML file.
     */
    public void saveGames() {
        if (loadedFilePath == null) return;

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("items");
            root.setAttribute("termsofuse", "https://boardgamegeek.com/xmlapi/termsofuse");
            doc.appendChild(root);

            int rank = 1;
            for (Game g : games) {
                Element item = doc.createElement("item");
                item.setAttribute("id", g.getGameID());
                item.setAttribute("rank", String.valueOf(rank++));

                Element thumbnail = doc.createElement("thumbnail");
                thumbnail.setAttribute("value", g.getThumbnailUrl());
                item.appendChild(thumbnail);

                Element name = doc.createElement("name");
                name.setAttribute("value", g.getTitle());
                item.appendChild(name);

                Element year = doc.createElement("yearpublished");
                year.setAttribute("value", String.valueOf(g.getYearPublished()));
                item.appendChild(year);

                root.appendChild(item);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(new File(loadedFilePath)));

        } catch (Exception e) {
            System.err.println("Failed to save games: " + e.getMessage());
        }
    }

    /**
     * Searches for games whose title matches the given criteria.
     *
     * @param criteria the search keyword
     * @return list of matching games
     */
    public List<Game> searchGames(String criteria) {
        List<Game> results = new ArrayList<>();
        for (Game g : games) {
            if (g.matchesSearch(criteria)) results.add(g);
        }
        return results;
    }

    /**
     * Retrieves a game by its unique ID.
     *
     * @param id the game ID to look up
     * @return the matching Game, or null if not found
     */
    public Game getGameByID(String id) {
        for (Game g : games) {
            if (g.getGameID().equals(id)) return g;
        }
        return null;
    }

    /**
     * Returns all games in the database.
     *
     * @return full list of games
     */
    public List<Game> getAll() {
        return games;
    }

    /**
     * Gets a named attribute from a child element of the given parent.
     *
     * @param parent   the parent XML element
     * @param tagName  the child tag to look for
     * @param attrName the attribute name to read
     * @return the attribute value, or empty string if not found
     */
    private String getAttributeValue(Element parent, String tagName, String attrName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) return "";
        return ((Element) nodes.item(0)).getAttribute(attrName);
    }
}