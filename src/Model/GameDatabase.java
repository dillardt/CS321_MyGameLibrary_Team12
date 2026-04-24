package Model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
     * Constructs a GameDatabase and loads games immediately.
     */
    public GameDatabase(String filePath) {
        this();
        loadGames(filePath);
    }

    /**
     * Loads full BoardGameGeek "thing" XML data into the game's database.
     * <p>
     * This loader is designed specifically for the XML structure shown in the
     * user's bgg_full.xml file, which contains the following elements inside each
     * <item>:
     *
     * <ul>
     *     <li><thumbnail>URL</thumbnail></li>
     *     <li><image>URL</image></li>
     *     <li><name type="primary" value="Game Title"/></li>
     *     <li><description>Long text...</description></li>
     *     <li><yearpublished value="YYYY"/></li>
     *     <li><minplayers value="X"/></li>
     *     <li><maxplayers value="Y"/></li>
     *     <li><link type="boardgamecategory" value="Category Name"/></li>
     * </ul>
     *
     * The loader extracts:
     * <ul>
     *     <li>Game ID</li>
     *     <li>Primary name</li>
     *     <li>Description</li>
     *     <li>Year published</li>
     *     <li>Thumbnail URL</li>
     *     <li>Full image URL</li>
     *     <li>Min/max players</li>
     *     <li>First boardgamecategory as genre</li>
     * </ul>
     *
     * All fields are safely parsed, and missing values fall back to defaults.
     *
     * @param filePath path to the BGG XML file
     */
    public void loadGames(String filePath) {
        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList itemNodes = doc.getElementsByTagName("item");

            for (int i = 0; i < itemNodes.getLength(); i++) {
                Element item = (Element) itemNodes.item(i);

                // -------------------------------
                // Game ID
                // -------------------------------
                String id = item.getAttribute("id");

                // -------------------------------
                // Primary Name
                // -------------------------------
                String title = "";
                NodeList nameNodes = item.getElementsByTagName("name");
                for (int n = 0; n < nameNodes.getLength(); n++) {
                    Element el = (Element) nameNodes.item(n);
                    if ("primary".equals(el.getAttribute("type"))) {
                        title = el.getAttribute("value");
                        break;
                    }
                }

                // -------------------------------
                // Description
                // -------------------------------
                /**
                 * Extracts the game's description text. Many BGG entries include an empty
                 * <description/> tag, which produces an empty string. To ensure the UI always
                 * displays meaningful text, a default placeholder description is used when the
                 * XML provides no content.
                 */
                String description = "";
                NodeList descNodes = item.getElementsByTagName("description");
                if (descNodes.getLength() > 0) {
                    description = descNodes.item(0).getTextContent().trim();
                }

// Apply default description if XML is empty
                if (description.isBlank()) {
                    description = "No description available for this game.";
                }


                // -------------------------------
                // Year Published
                // -------------------------------
                int year = 0;
                NodeList yearNodes = item.getElementsByTagName("yearpublished");
                if (yearNodes.getLength() > 0) {
                    Element el = (Element) yearNodes.item(0);
                    try {
                        year = Integer.parseInt(el.getAttribute("value"));
                    } catch (Exception ignored) {}
                }

                // -------------------------------
                // Thumbnail (text content)
                // -------------------------------
                String thumbnail = "";
                NodeList thumbNodes = item.getElementsByTagName("thumbnail");
                if (thumbNodes.getLength() > 0) {
                    thumbnail = thumbNodes.item(0).getTextContent().trim();
                }

                // -------------------------------
                // Full Image (text content)
                // -------------------------------
                String image = "";
                NodeList imageNodes = item.getElementsByTagName("image");
                if (imageNodes.getLength() > 0) {
                    image = imageNodes.item(0).getTextContent().trim();
                }

                // -------------------------------
                // Min/Max Players
                // -------------------------------
                int minPlayers = 0, maxPlayers = 0;

                NodeList minNodes = item.getElementsByTagName("minplayers");
                if (minNodes.getLength() > 0) {
                    try {
                        minPlayers = Integer.parseInt(((Element) minNodes.item(0)).getAttribute("value"));
                    } catch (Exception ignored) {}
                }

                NodeList maxNodes = item.getElementsByTagName("maxplayers");
                if (maxNodes.getLength() > 0) {
                    try {
                        maxPlayers = Integer.parseInt(((Element) maxNodes.item(0)).getAttribute("value"));
                    } catch (Exception ignored) {}
                }

                // -------------------------------
                // Genre (first boardgamecategory)
                // -------------------------------
                String genre = "";
                NodeList linkNodes = item.getElementsByTagName("link");
                for (int n = 0; n < linkNodes.getLength(); n++) {
                    Element el = (Element) linkNodes.item(n);
                    if ("boardgamecategory".equals(el.getAttribute("type"))) {
                        genre = el.getAttribute("value");
                        break;
                    }
                }

                // -------------------------------
                // Build Game Object
                // -------------------------------
                Game game = new Game(id, title, year, thumbnail, genre);
                game.setDescription(description);
                game.setMinPlayers(minPlayers);
                game.setMaxPlayers(maxPlayers);

                // Prefer full image, fallback to thumbnail
                game.setImagePath(image.isEmpty() ? thumbnail : image);

                // Default rating
                game.setAverageRating(0.0);

                games.add(game);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }







    /** Helper: get text content of a tag */
    private String getTextContent(Element parent, String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        if (list.getLength() > 0) {
            return list.item(0).getTextContent().trim();
        }
        return "";
    }

    /** Helper: get int attribute from a tag */
    private int getIntAttribute(Element parent, String tag, String attr, int fallback) {
        NodeList list = parent.getElementsByTagName(tag);
        if (list.getLength() > 0) {
            Element el = (Element) list.item(0);
            try {
                return Integer.parseInt(el.getAttribute(attr));
            } catch (Exception ignored) {}
        }
        return fallback;
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
     * May be empty if no games are loaded
     */
    public List<Game> getAllGames() {
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