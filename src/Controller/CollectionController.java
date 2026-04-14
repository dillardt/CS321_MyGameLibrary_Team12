package Controller;

import Model.Collection;
import Model.Game;
import Model.User;
import Model.UserDatabase;

public class CollectionController {

    private UserDatabase userDatabase;

    public CollectionController(UserDatabase userDatabase) {

    }

    public Collection createCollection(User currentUser, String name) { return null; }
    public boolean deleteCollection(User currentUser, String collectionName) { return false; }
    public void addGameToCollection(User currentUser, Game game, String collectionName) { }
    public void removeGameFromCollection(User currentUser, Game game, String collectionName) { }
}
