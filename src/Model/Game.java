package Model;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private String gameID;
    private String title;
    private int yearPublished;
    private String thumbnailUrl;
    private String genre;
    private int minPlayers;
    private int maxPlayers;
    private double averageRating;
    private String description;
    private String imagePath;
    private List<Review> reviews = new ArrayList<>();

    /**
     * Constructs a Game object with core metadata.
     * Default values are used for extended fields such as players,
     * rating, description, and image path.
     *
     * @param gameID unique identifier for the game
     * @param title name of the game
     * @param yearPublished year the game was published
     * @param thumbnailUrl URL or path to thumbnail image
     * @param genre genre/category of the game
     */
    public Game(String gameID, String title, int yearPublished,
                String thumbnailUrl, String genre) {

        this.gameID = gameID;
        this.title = title;
        this.yearPublished = yearPublished;
        this.thumbnailUrl = thumbnailUrl;
        this.genre = genre;

        // defaults so nothing breaks
        this.minPlayers = 0;
        this.maxPlayers = 0;
        this.averageRating = 0.0;
        this.description = "";
        this.imagePath = thumbnailUrl;
    }

    public Game(String title) {
        this.gameID = "";
        this.title = title;
        this.yearPublished = 0;
        this.thumbnailUrl = "";
        this.genre = "";
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void addReview(Review r) {
        reviews.add(r);
    }


    /**
     * Checks if this game's title matches the search term (case-insensitive).
     *
     * @param criteria the search term
     * @return true if the title contains the criteria
     */
    public boolean matchesSearch(String criteria) {
        return title.toLowerCase().contains(criteria.toLowerCase());
    }

    /**
     * Returns a short display summary of this game.
     *
     * @return title and year as a formatted string
     */
    public String getSummary() {
        return title + " (" + yearPublished + ")";
    }

    /**
     * @return the unique game ID
     */
    public String getGameID()        { return gameID; }

    /** @return the game title */
    public String getTitle()         { return title; }

    /** @return the year the game was published */
    public int    getYearPublished() { return yearPublished; }

    /** @return the thumbnail image URL */
    public String getThumbnailUrl()  { return thumbnailUrl; }

    /** @return game genre/category */
    public String getGenre()        { return genre; }

    /** @return minimum number of players */
    public int getMinPlayers() {
        return minPlayers;
    }

    /** @return maximum number of players */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /** @return average rating of the game */
    public double getAverageRating() {
        return averageRating;
    }

    /** @return game description */
    public String getDescription() {
        return description;
    }

    /** @return file path or URL of game image */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Updates the game description.
     *
     * @param description new description text
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Updates the average rating of the game.
     *
     * @param averageRating new rating value
     */
    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    /**
     * Sets the minimum number of players.
     *
     * @param minPlayers minimum players supported
     */
    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    /**
     * Sets the year the game was published.
     *
     * @param yearPublished the publication year
     */
    public void setYearPublished(int yearPublished) {
        this.yearPublished = yearPublished;
    }


    /**
     * Sets the genre/category of the game.
     *
     * @param genre the genre text
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }


    /**
     * Sets the maximum number of players.
     *
     * @param maxPlayers maximum players supported
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    /**
     * Sets the image path for the game.
     *
     * @param imagePath path or URL to image
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() { return getSummary(); }
}