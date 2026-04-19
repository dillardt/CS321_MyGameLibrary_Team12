package Model;

/**
 * A single board game stored in the system.
 * This class holds only data. No business rules live here.
 */
public class Game {

    private final String gameID;
    private final String title;
    private final String genre;
    private final int minPlayers;
    private final int maxPlayers;
    private final String description;
    private final String imagePath;
    private final double averageRating;

    /**
     * Builds a game object with all required fields.
     * Rating stays within 0 to 10.
     *
     * @param gameID unique identifier for the game
     * @param title game title
     * @param genre game category
     * @param minPlayers minimum number of players
     * @param maxPlayers maximum number of players
     * @param description full description of the game
     * @param imagePath image file path or URL
     * @param averageRating starting average rating (0 to 10 range)
     */
    public Game(String gameID, String title, String genre,
                int minPlayers, int maxPlayers,
                String description, String imagePath,
                double averageRating) {

        this.gameID = gameID;
        this.title = title;
        this.genre = genre;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.description = description;
        this.imagePath = imagePath;

        if (averageRating < 0.0) {
            this.averageRating = 0.0;
        } else this.averageRating = Math.min(averageRating, 10.0);
    }

    /** @return game ID */
    public String getGameID() { return gameID; }

    /** @return title */
    public String getTitle() { return title; }

    /** @return genre */
    public String getGenre() { return genre; }

    /** @return minimum players */
    public int getMinPlayers() { return minPlayers; }

    /** @return maximum players */
    public int getMaxPlayers() { return maxPlayers; }

    /** @return description */
    public String getDescription() { return description; }

    /** @return image path */
    public String getImagePath() { return imagePath; }

    /** @return average rating */
    public double getAverageRating() { return averageRating; }

    /**
     * Full formatted details of the game.
     *
     * @return multi-line string with all key fields
     */
    public String getDetails() {
        return "Title: " + title + "\n"
                + "Genre: " + genre + "\n"
                + "Players: " + minPlayers + " - " + maxPlayers + "\n"
                + "Rating: " + String.format("%.1f", averageRating) + "\n"
                + "Description: " + description;
    }

    /**
     * Short summary for lists and search results.
     *
     * @return one-line game summary
     */
    public String getSummary() {
        return title + " | " + genre + " | Rating: "
                + String.format("%.1f", averageRating);
    }

    /**
     * Checks if this game matches a search query.
     *
     * @param criteria search text entered by user
     * @return true if match is found in title, genre, or description
     */
    public boolean matchesSearch(String criteria) {

        if (criteria == null || criteria.isBlank()) {
            return false;
        }

        String query = criteria.toLowerCase();

        String safeTitle = (title != null) ? title.toLowerCase() : "";
        String safeGenre = (genre != null) ? genre.toLowerCase() : "";
        String safeDescription = (description != null) ? description.toLowerCase() : "";

        return safeTitle.contains(query)
                || safeGenre.contains(query)
                || safeDescription.contains(query);
    }

    /**
     * String representation of the game.
     *
     * @return summary format
     */
    @Override
    public String toString() {
        return getSummary();
    }
}