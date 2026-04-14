package Model;

public class Game {

    private String gameID;
    private String title;
    private String genre;
    private int minPlayers;
    private int maxPlayers;
    private String description;
    private String imagePath;
    private double averageRating;

    public Game(String gameID, String title, String genre,
                int minPlayers, int maxPlayers,
                String description, String imagePath, double averageRating) {

    }

    public String getGameID() { return null; }
    public String getTitle() { return null; }
    public String getGenre() { return null; }
    public int getMinPlayers() { return 0; }
    public int getMaxPlayers() { return 0; }
    public String getDescription() { return null; }
    public String getImagePath() { return null; }
    public double getAverageRating() { return 0; }
    public void setAverageRating(double averageRating) { }

    public String getDetails() { return null; }
    public boolean matchesSearch(String criteria) { return false; }
    public String getSummary() { return null; }
}