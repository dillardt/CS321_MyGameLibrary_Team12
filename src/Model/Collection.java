package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a personal game list created by a user.
 * Games can be added or removed freely.
 */
public class Collection {

    private String name;
    private List<Game> games;

    /**
     * Constructs an empty Collection with the given name.
     *
     * @param name the collection name
     */
    public Collection(String name) {
        this.name = name;
        this.games = new ArrayList<>();
    }

    /**
     * Adds a game to this collection only if it is not already present.
     *
     * @param game the game to add
     */
    public void addGame(Game game) {
        // Check if game is already in the list by comparing IDs
        for (Game existingGame : games) {
            if (existingGame.getGameID().equals(game.getGameID())) {
                return; // Exit if already exists
            }
        }
        games.add(game);
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
}