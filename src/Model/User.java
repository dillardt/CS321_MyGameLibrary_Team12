package Model;

import java.util.*;

/**
 * Represents a registered system user.
 * Holds credentials, role, collections, reviews, and recently viewed games.
 */
public class User {

    private String username;
    private String password;
    private UserRole role;
    private List<Collection> collections;
    private List<Review> reviews;
    private List<Game> recentlyViewed;

    /**
     * Constructs a User with the given username and password.
     * Default role is USER. Use setRole() to promote to ADMIN.
     *
     * @param username the user's login name
     * @param password the user's password
     */
    public User(String username, String password) {
        this.username       = username;
        this.password       = password;
        this.role           = UserRole.USER;
        this.collections    = new ArrayList<>();
        this.reviews        = new ArrayList<>();
        this.recentlyViewed = new ArrayList<>();
    }

    /**
     * Constructs a User with an explicit role.
     * Used by UserDatabase when loading from XML.
     *
     * @param username the user's login name
     * @param password the user's password
     * @param role     the user's role
     */
    public User(String username, String password, UserRole role) {
        this(username, password);
        this.role = role;
    }

    /**
     * Creates a new named collection for this user.
     *
     * @param name the collection name
     * @return the newly created Collection
     */
    public Collection createCollection(String name) {
        Collection c = new Collection(name);
        collections.add(c);
        return c;
    }

    /**
     * Deletes a collection by name.
     *
     * @param name the name of the collection to remove
     */
    public void deleteCollection(String name) {
        collections.removeIf(c -> c.getName().equals(name));
    }

    /**
     * Adds a game to the named collection.
     *
     * @param game           the game to add
     * @param collectionName the target collection name
     */
    public void addGameToCollection(Game game, String collectionName) {
        for (Collection c : collections) {
            if (c.getName().equals(collectionName)) {
                c.addGame(game);
                return;
            }
        }
    }

    /**
     * Removes a game from the named collection.
     *
     * @param game           the game to remove
     * @param collectionName the target collection name
     */
    public void removeGameFromCollection(Game game, String collectionName) {
        for (Collection c : collections) {
            if (c.getName().equals(collectionName)) {
                c.removeGame(game);
                return;
            }
        }
    }

    /**
     * Returns the names of all collections owned by this user.
     *
     * @return a list of collection names
     */
    public List<String> getCollectionNames() {
        List<String> names = new ArrayList<>();
        for (Collection c : collections) {
            names.add(c.getName());
        }
        return names;
    }

    /**
     * Adds a review written by this user.
     *
     * @param review the review object
     */
    public void addReview(Review review) {
        reviews.add(review);
    }

    /**
     * Authenticates a password attempt against the stored password.
     *
     * @param password the password to check
     * @return true if the password matches
     */
    public boolean authenticate(String password) {
        return this.password.equals(password);
    }

    /**
     * Returns true if this user has ADMIN role.
     *
     * @return true if admin
     */
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    /**
     * Clears any session-specific state on logout.
     */
    public void logout() {
        // Session cleanup can be added here if needed
    }

    // --- Getters and Setters ---

    /** @return the username */
    public String getUsername()              { return username; }

    /** @return the stored password */
    public String getPassword()              { return password; }

    /** @return the user's role */
    public UserRole getRole()                { return role; }

    /** @param role the role to assign */
    public void setRole(UserRole role)       { this.role = role; }

    /** @return the user's collections */
    public List<Collection> getCollections() { return collections; }

    /** @return the user's reviews */
    public List<Review> getReviews()         { return reviews; }

    /** @return the user's recently viewed games */
    public List<Game> getRecentlyViewed()    { return recentlyViewed; }
}