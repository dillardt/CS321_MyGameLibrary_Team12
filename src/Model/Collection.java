package Model;

import java.util.ArrayList;
import java.util.List;

/** A user's named game collection. */
public class Collection {

    private String name;
    private List<Game> games;

    /**
     * Creates an empty collection with the given name.
     * @param name the name for this collection
     */
    public Collection(String name) {
        this.name  = name;
        this.games = new ArrayList<>();
    }

    /**
     * Returns the collection name.
     * @return collection name
     */
    public String getName() { return name; }

    /**
     * Renames the collection.
     * @param name the new name
     */
    public void setName(String name) { this.name = name; }

    /**
     * How many games are in this collection.
     * @return number of games
     */
    public int getSize() { return games.size(); }

    /**
     * Returns a copy of the games list so the original can't be mutated.
     * @return new list containing all games
     */
    public List<Game> getGames() { return new ArrayList<>(games); }

    /**
     * Adds a game — returns false if null or already in the collection.
     * @param game the game to add
     * @return true if added, false if null or duplicate
     */
    public boolean addGame(Game game) {
        if (game == null) return false;
        if (games.contains(game)) return false;
        games.add(game);
        return true;
    }

    /**
     * Removes a game — returns false if null or not found.
     * @param game the game to remove
     * @return true if removed, false if null or not found
     */
    public boolean removeGame(Game game) {
        if (game == null) return false;
        return games.remove(game);
    }

    /**
     * Checks if a game is already here, null-safe.
     * @param game the game to check
     * @return true if found, false otherwise
     */
    public boolean containsGame(Game game) {
        if (game == null) return false;
        return games.contains(game);
    }

    /**
     * Finds a game by ID, returns null if not found or ID is blank.
     * @param gameID the ID to search for
     * @return matching game, or null
     */
    public Game getGameById(String gameID) {
        if (gameID == null || gameID.isBlank()) return null;
        for (Game game : games) {
            if (game.getGameID().equals(gameID)) return game;
        }
        return null;
    }

    /**
     * e.g. "My RPGs (4 games)"
     * @return name and size as a string
     */
    @Override
    public String toString() {
        return name + " (" + games.size() + " games)";
    }
}