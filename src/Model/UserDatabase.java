package Model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all system users.
 * Handles account creation, login validation, and persistent storage.
 */
public class UserDatabase {

    private List<User> users;
    private String fileLocation;

    /**
     * Constructs an empty UserDatabase.
     */
    public UserDatabase(String filePath) {
        users = new ArrayList<>();
        this.fileLocation = filePath;
    }



    /**
     * Loads users from the given CSV file (username,password per line).
     *
     * @param filePath path to the user data file
     */
    public void loadUsers(String filePath) {
        this.fileLocation = filePath;
        users.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    users.add(new User(parts[0].trim(), parts[1].trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load users: " + e.getMessage());
        }
    }

    /**
     * Saves all users back to their source CSV file.
     */
    public void saveUsers() {
        if (fileLocation.isEmpty()) return;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileLocation))) {
            for (User u : users) {
                writer.write(u.getUsername() + "," + u.getPassword());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to save users: " + e.getMessage());
        }
    }

    /**
     * Authenticates a user by username and password.
     *
     * @param username the login name
     * @param password the password attempt
     * @return the matching User if credentials are valid, null otherwise
     */
    public User authenticate(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username)
                    && u.authenticate(password)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Creates a new user account if the username is not already taken.
     *
     * @param username desired username
     * @param password desired password
     * @return true if account was created, false if username already exists
     */
    public boolean createUser(String username, String password) {
        if (getUser(username) != null) {
            return false;
        }
        users.add(new User(username, password));
        saveUsers();
        return true;
    }


    /**
     * Retrieves a user by username.
     *
     * @param username the username to look up
     * @return the User, or null if not found
     */
    public User getUser(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Removes a user account by username.
     *
     * @param username the username to delete
     */
    public void deleteUser(String username) {
        users.removeIf(u -> u.getUsername().equals(username));
        saveUsers();
    }
}
