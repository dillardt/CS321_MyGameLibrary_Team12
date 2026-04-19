package Model;

import java.util.ArrayList;
import java.util.List;

public class User {

    private static final int MAX_RECENTLY_VIEWED = 10;

    private final String username;
    private final String passwordHash;
    private List<Collection> collections;
    private List<Review> reviews;
    private List<Game> recentlyViewed;

    public User(String username, String passwordHash) {
        this.username     = username;
        this.passwordHash = passwordHash;
        this.collections    = new ArrayList<>();
        this.reviews        = new ArrayList<>();
        this.recentlyViewed = new ArrayList<>();
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }

    public boolean authenticate(String inputHash) {
        if (inputHash == null) return false;
        return passwordHash.equals(inputHash);
    }

    public List<Collection> getCollections() { return new ArrayList<>(collections); }

    public List<Review> getReviews() { return new ArrayList<>(reviews); }

    public List<Game> getRecentlyViewed() { return new ArrayList<>(recentlyViewed); }

    @Override
    public String toString() {
        return username
                + " | Collections: " + collections.size()
                + " | Reviews: "     + reviews.size();
    }

    public Collection getCollectionByName(String name) {}
    public boolean addCollection(Collection collection) {}
    public boolean removeCollection(String name) {}

    public Review getReviewByGame(Game game) {}
    public boolean addReview(Review review) {}
    public boolean removeReview(Review review) {}

    public void addRecentlyViewed(Game game) {}
}