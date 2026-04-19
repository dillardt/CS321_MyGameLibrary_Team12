package Model;

public class Game {

    private final String gameID;
    private final String title;
    private final String genre;
    private final int minPlayers;
    private final int maxPlayers;
    private final String description;
    private final String imagePath;
    private final double averageRating;

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
        } else if (averageRating > 10.0) {
            this.averageRating = 10.0;
        } else {
            this.averageRating = averageRating;
        }
    }

    public String getGameID() { return gameID; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public int getMinPlayers() { return minPlayers; }
    public int getMaxPlayers() { return maxPlayers; }
    public String getDescription() { return description; }
    public String getImagePath() { return imagePath; }
    public double getAverageRating() { return averageRating; }

    public String getDetails() {
        return "Title: " + title + "\n"
                + "Genre: " + genre + "\n"
                + "Players: " + minPlayers + " - " + maxPlayers + "\n"
                + "Rating: " + String.format("%.1f", averageRating) + "\n"
                + "Description: " + description;
    }

    public boolean matchesSearch(String criteria) { return false; }

    public String getSummary() {
        return title + " | " + genre + " | Rating: "
                + String.format("%.1f", averageRating);
    }
}