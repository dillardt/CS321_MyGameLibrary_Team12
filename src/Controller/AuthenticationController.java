package Controller;

import Model.User;
import Model.UserDatabase;

public class AuthenticationController {

    private final UserDatabase userDB;
    private User currentUser;

    public AuthenticationController(UserDatabase userDB) {
        this.userDB = userDB;
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return null;
    }

    public boolean isLoggedIn() {
        return false;
    }

    public boolean login(String username, String passwordHash) {
        return false;
    }

    public void logout() {
    }

    public boolean register(String username, String passwordHash) {
        return false;
    }

    public String toString() {
        return "AuthenticationController";
    }

}