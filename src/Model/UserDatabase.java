package Model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserDatabase {

    private List<User> users;
    private final String fileLocation;

    public UserDatabase(String fileLocation) {
        this.fileLocation = fileLocation;
        this.users        = new ArrayList<>();
    }

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

    public User getUser(String username) {
        if (username == null || username.isBlank()) return null;
        String normalized = username.toLowerCase();
        for (User user : users) {
            if (user.getUsername().equals(normalized)) return user;
        }
        return null;
    }

    public List<User> getAllUsers() { return new ArrayList<>(users); }

    public boolean userExists(String username) {
        if (username == null || username.isBlank()) return false;
        return getUser(username) != null;
    }

    public boolean createUser(String username, String passwordHash) {
        if (username == null || username.isBlank()) return false;
        if (passwordHash == null || passwordHash.isBlank()) return false;
        if (userExists(username)) return false;
        users.add(new User(username.toLowerCase(), passwordHash));
        return true;
    }

    public boolean deleteUser(String username) {
        if (username == null || username.isBlank()) return false;
        User target = getUser(username);
        if (target == null) return false;
        users.remove(target);
        return true;
    }

    public boolean authenticate(String username, String passwordHash) {
        if (username == null || username.isBlank()) return false;
        if (passwordHash == null || passwordHash.isBlank()) return false;
        User user = getUser(username);
        if (user == null) return false;
        return user.authenticate(passwordHash);
    }
}