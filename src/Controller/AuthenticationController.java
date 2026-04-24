package Controller;

import Model.User;
import Model.UserDatabase;

/**
 * Handles all authentication-related operations such as login,
 * logout, and account creation.
 */
public class AuthenticationController {

    private final UserDatabase userDatabase;
    private User currentUser;

    public AuthenticationController(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
        this.currentUser = null;
    }

    public boolean login(String username, String password) {
        User user = userDatabase.authenticate(username, password);
        if (user != null) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean createAccount(String username, String password) {
        return userDatabase.createUser(username, password);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
