package Model;

import java.util.ArrayList;
import java.util.List;

/** A registered user — owns their collections, reviews, and recently viewed list. */
public class User {

    private static final int MAX_RECENTLY_VIEWED = 10;

    private final String username;
    private final String passwordHash;
    private List<Collection> collections;
    private List<Review> reviews;
    private List<Game> recentlyViewed;

    /**
     * Creates a user with empty lists — username and password never change after this.
     * @param username     unique identifier
     * @param passwordHash hashed password for authentication
     */
    public User(String username, String passwordHash) {
        this.username     = username;
        this.passwordHash = passwordHash;
        this.collections    = new ArrayList<>();
        this.reviews        = new ArrayList<>();
        this.recentlyViewed = new ArrayList<>();
    }

    /** @return the username */
    public String getUsername() { return username; }

    /** @return the stored password hash */
    public String getPasswordHash() { return passwordHash; }

    /**
     * Checks if the given hash matches the stored one.
     * @param inputHash hash to verify
     * @return true if it matches, false if null or wrong
     */
    public boolean authenticate(String inputHash) {
        if (inputHash == null) return false;
        return passwordHash.equals(inputHash);
    }

    /** @return defensive copy of the collections list */
    public List<Collection> getCollections() { return new ArrayList<>(collections); }

    /**
     * Finds a collection by name, case-insensitive.
     * @param name collection name to search for
     * @return matching collection, or null if not found
     */
    public Collection getCollectionByName(String name) {
        if (name == null || name.isBlank()) return null;
        for (Collection c : collections) {
            if (c.getName().equalsIgnoreCase(name)) return c;
        }
        return null;
    }

    /**
     * Adds a collection — rejects null or duplicate names.
     * @param collection the collection to add
     * @return true if added, false if null or duplicate
     */
    public boolean addCollection(Collection collection) {
        if (collection == null) return false;
        if (getCollectionByName(collection.getName()) != null) return false;
        collections.add(collection);
        return true;
    }

    /**
     * Removes a collection by name, case-insensitive.
     * @param name name of the collection to remove
     * @return true if removed, false if not found or blank
     */
    public boolean removeCollection(String name) {
        if (name == null || name.isBlank()) return false;
        for (int i = 0; i < collections.size(); i++) {
            if (collections.get(i).getName().equalsIgnoreCase(name)) {
                collections.remove(i);
                return true;
            }
        }
        return false;
    }

    /** @return defensive copy of the reviews list */
    public List<Review> getReviews() { return new ArrayList<>(reviews); }

    /**
     * Finds the review this user wrote for a specific game.
     * @param game the game to look up
     * @return matching review, or null if none exists
     */
    public Review getReviewByGame(Game game) {
        if (game == null) return null;
        for (Review r : reviews) {
            if (r.getGame() != null
                    && r.getGame().getGameID() != null
                    && r.getGame().getGameID().equals(game.getGameID())) {
                return r;
            }
        }
        return null;
    }

    /**
     * Adds a review — rejects null and duplicate reviews for the same game.
     * @param review the review to add
     * @return true if added, false if null or game already reviewed
     */
    public boolean addReview(Review review) {
        if (review == null) return false;
        if (getReviewByGame(review.getGame()) != null) return false;
        reviews.add(review);
        return true;
    }

    /**
     * Removes a review — rejects null, uses reference equality.
     * @param review the review to remove
     * @return true if removed, false if null or not found
     */
    public boolean removeReview(Review review) {
        if (review == null) return false;
        return reviews.remove(review);
    }

    /** @return defensive copy of recently viewed, most recent first */
    public List<Game> getRecentlyViewed() { return new ArrayList<>(recentlyViewed); }

    /**
     * Adds a game to the front of recently viewed, capped at 10 — moves it if already present.
     * @param game the game to mark as recently viewed
     */
    public void addRecentlyViewed(Game game) {
        if (game == null) return;
        recentlyViewed.remove(game);
        recentlyViewed.add(0, game);
        if (recentlyViewed.size() > MAX_RECENTLY_VIEWED) {
            recentlyViewed.remove(recentlyViewed.size() - 1);
        }
    }

    /** e.g. "alice | Collections: 3 | Reviews: 5" */
    @Override
    public String toString() {
        return username
                + " | Collections: " + collections.size()
                + " | Reviews: "     + reviews.size();
    }
}