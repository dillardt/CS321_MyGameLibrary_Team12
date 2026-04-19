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

    public List<User> getAllUsers() { return new ArrayList<>(users); }

    public boolean userExists(String username) {
        if (username == null || username.isBlank()) return false;
        return getUser(username) != null;
    }

    public void loadUsers() {}
    public void saveUsers() {}
    public User getUser(String username) {}
    public boolean createUser(String username, String passwordHash) {}
    public boolean deleteUser(String username) {}
    public boolean authenticate(String username, String passwordHash) {}
}