package Model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/** Manages all registered users — handles file persistence, account creation, and login. */
public class UserDatabase {

    private List<User> users;
    private final String fileLocation;

    /**
     * Sets up the database pointing at the given file — call loadUsers() to actually read it.
     * @param fileLocation path to the CSV file
     */
    public UserDatabase(String fileLocation) {
        this.fileLocation = fileLocation;
        this.users        = new ArrayList<>();
    }

    /** Reads users from the CSV file — skips malformed lines, warns if file is missing. */
    public void loadUsers() {
        users.clear();
        File file = new File(fileLocation);

        if (!file.exists()) {
            System.out.println("WARNING: User database file not found: "
                    + fileLocation + ". Starting with empty user list.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;

                String[] parts = line.split(",");
                if (parts.length != 2) {
                    System.out.println("WARNING: Skipping malformed line "
                            + lineNumber + ": " + line);
                    continue;
                }

                String username     = parts[0].trim();
                String passwordHash = parts[1].trim();

                if (username.isBlank() || passwordHash.isBlank()) {
                    System.out.println("WARNING: Skipping line "
                            + lineNumber + " with blank fields.");
                    continue;
                }

                users.add(new User(username.toLowerCase(), passwordHash));
            }

        } catch (IOException e) {
            System.out.println("ERROR: Failed to read user database file: "
                    + e.getMessage());
        }
    }

    /** Overwrites the CSV file with all users currently in memory. */
    public void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileLocation, false))) {
            for (User user : users) {
                writer.write(user.getUsername() + "," + user.getPasswordHash());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("ERROR: Failed to save user database file: "
                    + e.getMessage());
        }
    }

    /**
     * Finds a user by username, case-insensitive.
     * @param username the username to look up
     * @return matching user, or null if not found
     */
    public User getUser(String username) {
        if (username == null || username.isBlank()) return null;
        String normalized = username.toLowerCase();
        for (User user : users) {
            if (user.getUsername().equals(normalized)) return user;
        }
        return null;
    }

    /** @return defensive copy of all registered users */
    public List<User> getAllUsers() { return new ArrayList<>(users); }

    /**
     * Checks if a username is already taken, case-insensitive.
     * @param username the username to check
     * @return true if taken, false if free or input is blank
     */
    public boolean userExists(String username) {
        if (username == null || username.isBlank()) return false;
        return getUser(username) != null;
    }

    /**
     * Creates a new account — rejects blank inputs and duplicate usernames.
     * @param username     desired username
     * @param passwordHash hashed password
     * @return true if created, false if invalid or duplicate
     */
    public boolean createUser(String username, String passwordHash) {
        if (username == null || username.isBlank()) return false;
        if (passwordHash == null || passwordHash.isBlank()) return false;
        if (userExists(username)) return false;
        users.add(new User(username.toLowerCase(), passwordHash));
        return true;
    }

    /**
     * Deletes an account by username, case-insensitive.
     * @param username the account to delete
     * @return true if deleted, false if not found or blank
     */
    public boolean deleteUser(String username) {
        if (username == null || username.isBlank()) return false;
        User target = getUser(username);
        if (target == null) return false;
        users.remove(target);
        return true;
    }

    /**
     * Verifies login credentials — delegates the password check to User.
     * @param username     the username attempting to log in
     * @param passwordHash the hash to verify
     * @return true if valid, false if user not found or password wrong
     */
    public boolean authenticate(String username, String passwordHash) {
        if (username == null || username.isBlank()) return false;
        if (passwordHash == null || passwordHash.isBlank()) return false;
        User user = getUser(username);
        if (user == null) return false;
        return user.authenticate(passwordHash);
    }
}