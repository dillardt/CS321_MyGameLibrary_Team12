package Model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserDatabase {

    private List<User> users;
    private String fileLocation;

    public UserDatabase(String fileLocation) {}

    public void loadUsers() {}
    public void saveUsers() {}

    public User getUser(String username) {}
    public List<User> getAllUsers() {}

    public boolean userExists(String username) {}
    public boolean createUser(String username, String passwordHash) {}
    public boolean deleteUser(String username) {}

    public boolean authenticate(String username, String passwordHash) {}
}