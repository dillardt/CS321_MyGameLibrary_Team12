package Controller;

import Model.User;
import Model.UserDatabase;

/**
 * Handles all authentication-related operations such as login,
 * logout, and account creation.
 */
public class AuthenticationController {

    private UserDatabase userDatabase;
    private User currentUser;

    /**
     * Constructs an AuthenticationController with a reference to the UserDatabase.
     *
     * @param userDatabase the database used for user authentication and storage
     */
    public AuthenticationController(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
        this.currentUser = null;
    }

    /**
     * Attempts to log in a user with the provided credentials.
     *
     * @param username the user's username
     * @param password the user's password
     * @return true if login is successful, false otherwise
     */
    public boolean login(String username, String password) {
        User user = userDatabase.authenticate(username, password);
        if (user != null) {
            currentUser = user;
            return true;
        }
        return false;
    }

    /**
     * Logs out the currently authenticated user.
     */
    public void logout() {
        if (currentUser != null) {
            currentUser.logout();
            currentUser = null;
        }
    }

    /**
     * Creates a new user account.
     *
     * @param username desired username
     * @param password desired password
     * @return true if account creation is successful, false otherwise
     */
    public boolean createAccount(String username, String password) {
        return userDatabase.createUser(username, password);
    }

    /**
     * Retrieves the currently logged-in user.
     *
     * @return the current User, or null if no user is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}