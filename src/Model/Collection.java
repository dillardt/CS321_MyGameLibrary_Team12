package Model;

import java.util.*;

/**
 * Represents a personal game list created by a user.
 * Games can be added or removed freely.
 */
public class Collection {

    private String name;
    private List<Game> games;

    // Holds game IDs loaded from XML before GameDatabase is available
    // to resolve them into full Game objects. Cleared after resolution.
    private List<String> pendingGameIds;

    /**
     * Constructs an empty Collection with the given name.
     *
     * @param name the collection name
     */
    public Collection(String name) {
        this.name           = name;
        this.games          = new ArrayList<>();
        this.pendingGameIds = new ArrayList<>();
    }

    /**
     * Adds a game to this collection only if it is not already present.
     *
     * @param game the game to add
     */
    public void addGame(Game game) {
        for (Game existingGame : games) {
            if (existingGame.getGameID().equals(game.getGameID())) {
                return; // already in collection
            }
        }
        games.add(game);
    }

    /**
     * Stores a game ID to be resolved later.
     * Called during XML load before GameDatabase is available (R6).
     *
     * @param gameId the BGG game ID to store
     */
    public void addGamePlaceholder(String gameId) {
        pendingGameIds.add(gameId);
    }

    /**
     * Returns the list of pending game IDs waiting to be resolved.
     * Used by AppCoordinator after both databases have loaded.
     *
     * @return list of unresolved game IDs
     */
    public List<String> getPendingGameIds() {
        return pendingGameIds;
    }

    /**
     * Clears the pending IDs list after resolution is complete.
     */
    public void clearPendingGameIds() {
        pendingGameIds.clear();
    }

    /**
     * Removes a game from this collection.
     *
     * @param game the game to remove
     */
    public void removeGame(Game game) {
        games.remove(game);
    }

    /**
     * Returns all games in this collection.
     *
     * @return list of games
     */
    public List<Game> getGames() {
        return games;
    }

    /**
     * Returns the name of this collection.
     *
     * @return collection name
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}