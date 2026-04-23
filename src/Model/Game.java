package Model;

public class Game {

    private String gameID;
    private String title;
    private int yearPublished;
    private String thumbnailUrl;

    /**
     * Constructs a Game with all required fields.
     *
     * @param gameID       unique game ID
     * @param title        game title
     * @param yearPublished year the game was published
     * @param thumbnailUrl  URL to the game's thumbnail image
     */
    public Game(String gameID, String title, int yearPublished, String thumbnailUrl) {
        this.gameID        = gameID;
        this.title         = title;
        this.yearPublished = yearPublished;
        this.thumbnailUrl  = thumbnailUrl;
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

    /** @return the unique game ID */
    public String getGameID()        { return gameID; }

    /** @return the game title */
    public String getTitle()         { return title; }

    /** @return the year the game was published */
    public int    getYearPublished() { return yearPublished; }

    /** @return the thumbnail image URL */
    public String getThumbnailUrl()  { return thumbnailUrl; }

    @Override
    public String toString() { return getSummary(); }
}