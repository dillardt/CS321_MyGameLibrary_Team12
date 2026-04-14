package Model;

import java.util.List;

public class User {

    private String username;
    private String passwordHash;
    private List<Collection> collections;
    private List<Review> reviews;
    private List<Game> recentlyViewed;

    public User(String username, String password) {

    }

    public User(String username, String passwordHash, boolean fromFile) {

    }

    public Collection createCollection(String name) { return null; }
    public boolean deleteCollection(String name) { return false; }
    public void addGameToCollection(Game game, String collectionName) { }
    public void removeGameFromCollection(Game game, String collectionName) { }
    public void addReview(Game game, Review review) { }
    public void addRecentlyViewed(Game game) { }
    public void logout() { }
    public boolean authenticate(String password) { return false; }

    public String getUsername() { return null; }
    public String getPasswordHash() { return null; }
    public List<Collection> getCollections() { return null; }
    public List<Review> getReviews() { return null; }
    public List<Game> getRecentlyViewed() { return null; }
}
