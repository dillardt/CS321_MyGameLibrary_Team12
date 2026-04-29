package Model;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

/**
 * Manages all system users.
 * Handles account creation, login validation, and persistent storage.
 *
 * Saves to users.xml in proper XML format, including each user's
 * collections and the game IDs in each collection (R3, R6).
 *
 */
public class UserDatabase {

    private List<User> users;
    private String fileLocation;

    /**
     * Constructs a UserDatabase and immediately loads from the given file.
     *
     * @param filePath path to users.xml
     */
    public UserDatabase(String filePath) {
        users = new ArrayList<>();
        this.fileLocation = filePath;
        loadUsers(filePath);
        seedAdminIfMissing();
    }

    /**
     * Creates the default admin account if it doesn't already exist.
     * Username: admin  Password: admin
     * This account has ADMIN role and can add/delete games.
     */
    private void seedAdminIfMissing() {
        if (getUser("admin") == null) {
            User admin = new User("admin", "admin", UserRole.ADMIN);
            users.add(admin);
            saveUsers();
        }
    }


    /**
     * Loads users and their collections from the XML file.
     * Uses the same DOM parser pattern as GameDatabase.
     * If the file does not exist yet, starts with an empty list.
     *
     * @param filePath path to users.xml
     */
    public void loadUsers(String filePath) {
        this.fileLocation = filePath;
        users.clear();

        File file = new File(filePath);
        if (!file.exists()) return; // first run — no file yet, that's fine

        // If the file doesn't start with '<' it's the old CSV format — skip it.
        // A fresh users.xml will be written on the next saveUsers() call.
        try (java.io.BufferedReader peek = new java.io.BufferedReader(new java.io.FileReader(file))) {
            int first = peek.read();
            if (first != '<') {
                System.out.println("users.xml contains old CSV format — it will be replaced on next save.");
                return;
            }
        } catch (IOException e) {
            System.err.println("Could not read users.xml: " + e.getMessage());
            return;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList userNodes = doc.getElementsByTagName("user");
            for (int i = 0; i < userNodes.getLength(); i++) {
                Node node = userNodes.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) continue;

                Element userEl  = (Element) node;
                String username = getText(userEl, "username");
                String password = getText(userEl, "password");
                if (username == null || password == null) continue;

                User user = new User(username, password);

                // Restore role — defaults to USER if tag is missing (backward compat)
                String roleStr = getText(userEl, "role");
                if (roleStr != null) {
                    try {
                        user.setRole(UserRole.valueOf(roleStr));
                    } catch (IllegalArgumentException ignored) {
                        // unknown role string — leave as USER
                    }
                }

                // Restore each saved collection
                NodeList collectionNodes = userEl.getElementsByTagName("collection");
                for (int j = 0; j < collectionNodes.getLength(); j++) {
                    Node cNode = collectionNodes.item(j);
                    if (cNode.getNodeType() != Node.ELEMENT_NODE) continue;
                    Element cEl   = (Element) cNode;
                    String cName  = cEl.getAttribute("name");
                    if (cName.isBlank()) continue;

                    Collection collection = user.createCollection(cName);

                    // Restore game IDs — GameDatabase must match them up on startup
                    NodeList gameIdNodes = cEl.getElementsByTagName("gameId");
                    for (int k = 0; k < gameIdNodes.getLength(); k++) {
                        Node gNode = gameIdNodes.item(k);
                        if (gNode.getNodeType() == Node.ELEMENT_NODE) {
                            String gameId = gNode.getTextContent().trim();
                            if (!gameId.isBlank()) {
                                // Store as a placeholder Game — AppCoordinator resolves
                                // the full Game object from GameDatabase after both load
                                collection.addGamePlaceholder(gameId);
                            }
                        }
                    }
                }

                users.add(user);
            }

        } catch (Exception e) {
            System.err.println("Failed to load users from XML: " + e.getMessage());
        }
    }


    /**
     * Saves all users and their collections to users.xml in proper XML format.
     * Called after every create/delete user action and after every
     * collection create/delete/add-game/remove-game action.
     */
    public void saveUsers() {
        if (fileLocation == null || fileLocation.isEmpty()) return;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("users");
            doc.appendChild(root);

            for (User u : users) {
                Element userEl = doc.createElement("user");

                Element usernameEl = doc.createElement("username");
                usernameEl.setTextContent(u.getUsername());
                userEl.appendChild(usernameEl);

                Element passwordEl = doc.createElement("password");
                passwordEl.setTextContent(u.getPassword());
                userEl.appendChild(passwordEl);

                // Save role so it survives restarts
                Element roleEl = doc.createElement("role");
                roleEl.setTextContent(u.getRole().name());
                userEl.appendChild(roleEl);
                for (Collection c : u.getCollections()) {
                    Element collEl = doc.createElement("collection");
                    collEl.setAttribute("name", c.getName());

                    for (Game g : c.getGames()) {
                        Element gameIdEl = doc.createElement("gameId");
                        gameIdEl.setTextContent(g.getGameID());
                        collEl.appendChild(gameIdEl);
                    }

                    userEl.appendChild(collEl);
                }

                root.appendChild(userEl);
            }

            // Write with indentation so the file is human-readable
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(doc), new StreamResult(new File(fileLocation)));

        } catch (Exception e) {
            System.err.println("Failed to save users to XML: " + e.getMessage());
        }
    }


    /**
     * Authenticates a user by username and password.
     *
     * @param username the login name
     * @param password the password attempt
     * @return the matching User if valid, null otherwise
     */
    public User authenticate(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.authenticate(password)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Creates a new user account if the username is not already taken.
     * Saves immediately so the account persists (R3).
     *
     * @param username desired username
     * @param password desired password
     * @return true if created, false if username already exists
     */
    public boolean createUser(String username, String password) {
        if (getUser(username) != null) return false;
        users.add(new User(username, password));
        saveUsers();
        return true;
    }

    /**
     * Retrieves a user by username.
     *
     * @param username the username to look up
     * @return the User, or null if not found
     */
    public User getUser(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username)) return u;
        }
        return null;
    }

    /**
     * Removes a user account by username and saves.
     *
     * @param username the username to delete
     */
    public void deleteUser(String username) {
        users.removeIf(u -> u.getUsername().equals(username));
        saveUsers();
    }

    /**
     * Returns all usernames in the database.
     * Used by AppCoordinator to resolve collection placeholders on startup.
     *
     * @return list of all usernames
     */
    public List<String> getAllUsernames() {
        List<String> names = new ArrayList<>();
        for (User u : users) {
            names.add(u.getUsername());
        }
        return names;
    }


    /**
     * Reads the text content of the first matching child element.
     * Matches the parseTextField pattern from the lecture XMLParserUtility.
     */
    private String getText(Element parent, String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        if (list.getLength() == 0) return null;
        Node node = list.item(0);
        return (node != null) ? node.getTextContent().trim() : null;
    }
}