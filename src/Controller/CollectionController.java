package Controller;

import Model.Collection;
import Model.Game;
import Model.User;
import Model.UserDatabase;

/**
 * Handles operations related to user collections,
 * including creation, deletion, and game management.
 */
public class CollectionController {

    private UserDatabase userDatabase;

    /**
     * Constructs a CollectionController with access to the UserDatabase.
     *
     * @param userDatabase the database managing users
     */
    public CollectionController(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
    }

    /**
     * Creates a new collection for the current user.
     *
     * @param currentUser the active user
     * @param name the name of the collection
     * @return the created Collection, or null if user is null
     */
    public Collection createCollection(User currentUser, String name) {
        if (currentUser != null) {
            return currentUser.createCollection(name);
        }
        return null;
    }

    /**
     * Deletes a collection belonging to the current user.
     *
     * @param currentUser the active user
     * @param collectionName the name of the collection
     * @return true if deletion is successful, false otherwise
     */
    public boolean deleteCollection(User currentUser, String collectionName) {
        if (currentUser != null) {
            currentUser.deleteCollection(collectionName);
            return true;
        }
        return false;
    }

    /**
     * Adds a game to a user's collection.
     *
     * @param currentUser the active user
     * @param game the game to add
     * @param collectionName the target collection
     */
    public void addGameToCollection(User currentUser, Game game, String collectionName) {
        if (currentUser != null) {
            currentUser.addGameToCollection(game, collectionName);
        }
    }

    /**
     * Removes a game from a user's collection.
     *
     * @param currentUser the active user
     * @param game the game to remove
     * @param collectionName the collection name
     */
    public void removeGameFromCollection(User currentUser, Game game, String collectionName) {
        if (currentUser != null) {
            currentUser.removeGameFromCollection(game, collectionName);
        }
    }
}