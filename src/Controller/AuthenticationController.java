package Controller;

import Model.User;
import Model.UserDatabase;

public class AuthenticationController {

    private UserDatabase userDatabase;
    private User currentUser;

    public AuthenticationController(UserDatabase userDatabase) {

    }

    public boolean login(String username, String password) { return false; }
    public void logout() { }
    public boolean createAccount(String username, String password) { return false; }
    public User getCurrentUser() { return null; }
    public boolean isLoggedIn() { return false; }
}
