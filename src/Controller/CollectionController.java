package Controller;

import Model.User;
import Model.Game;

public class CollectionController {

    public CollectionController() {}

    /** Creates a new collection for the user */
    public boolean createCollection(User user, String name) {
        if (user == null || name == null || name.isBlank()) return false;
        user.createCollection(name);
        return true;
    }

    /** Deletes a collection */
    public boolean deleteCollection(User user, String name) {
        if (user == null || name == null) return false;
        user.deleteCollection(name);
        return true;
    }

    /** Adds a game to a collection */
    public boolean addGameToCollection(User user, Game game, String collectionName) {
        if (user == null || game == null || collectionName == null) return false;
        user.addGameToCollection(game, collectionName);
        return true;
    }

    /** Removes a game from a collection */
    public boolean removeGameFromCollection(User user, Game game, String collectionName) {
        if (user == null || game == null || collectionName == null) return false;
        user.removeGameFromCollection(game, collectionName);
        return true;
    }
}
