package Model;

import java.util.List;

public class UserDatabase {

    private List<User> users;
    private String fileLocation;

    public UserDatabase(String fileLocation) {

    }

    public void loadUsers() { }
    public void saveUsers() { }
    public User authenticate(String username, String password) { return null; }
    public User createUser(String username, String password) { return null; }
    public User getUser(String username) { return null; }
    public boolean deleteUser(String username) { return false; }
}
