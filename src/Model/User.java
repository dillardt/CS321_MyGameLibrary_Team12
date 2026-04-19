package Model;

import java.util.ArrayList;
import java.util.List;

public class User {

    private static final int MAX_RECENTLY_VIEWED = 10;

    private String username;
    private String passwordHash;
    private List<Collection> collections;
    private List<Review> reviews;
    private List<Game> recentlyViewed;

    public User(String username, String passwordHash) {}

    public String getUsername() {}
    public String getPasswordHash() {}

    public boolean authenticate(String inputHash) {}

    public List<Collection> getCollections() {}
    public Collection getCollectionByName(String name) {}
    public boolean addCollection(Collection collection) {}
    public boolean removeCollection(String name) {}

    public List<Review> getReviews() {}
    public Review getReviewByGame(Game game) {}
    public boolean addReview(Review review) {}
    public boolean removeReview(Review review) {}

    public List<Game> getRecentlyViewed() {}
    public void addRecentlyViewed(Game game) {}

    @Override
    public String toString() {}
}