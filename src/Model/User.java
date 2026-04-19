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

    public Collection getCollectionByName(String name) {
        if (name == null || name.isBlank()) return null;
        for (Collection c : collections) {
            if (c.getName().equalsIgnoreCase(name)) return c;
        }
        return null;
    }

    public boolean addCollection(Collection collection) {
        if (collection == null) return false;
        if (getCollectionByName(collection.getName()) != null) return false;
        collections.add(collection);
        return true;
    }

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

    public List<Review> getReviews() { return new ArrayList<>(reviews); }

    public Review getReviewByGame(Game game) {
        if (game == null) return null;
        for (Review r : reviews) {
            if (r.getGame() == game) return r;
        }
        return null;
    }

    public boolean addReview(Review review) {
        if (review == null) return false;
        if (getReviewByGame(review.getGame()) != null) return false;
        reviews.add(review);
        return true;
    }

    public boolean removeReview(Review review) {
        if (review == null) return false;
        return reviews.remove(review);
    }

    public List<Game> getRecentlyViewed() { return new ArrayList<>(recentlyViewed); }

    public void addRecentlyViewed(Game game) {
        if (game == null) return;
        recentlyViewed.remove(game);
        recentlyViewed.add(0, game);
        if (recentlyViewed.size() > MAX_RECENTLY_VIEWED) {
            recentlyViewed.remove(recentlyViewed.size() - 1);
        }
    }

    @Override
    public String toString() {
        return username
                + " | Collections: " + collections.size()
                + " | Reviews: "     + reviews.size();
    }
}