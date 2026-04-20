package Controller;

import Model.*;
import java.util.ArrayList;
import java.util.List;

public class SystemController {

    private final String configFilePath;
    private ConfigurationManager config;
    private GameDatabase gameDB;
    private UserDatabase userDB;
    private User currentUser;

    public SystemController(String configFilePath) {
        this.configFilePath = configFilePath;
        this.config = null;
        this.gameDB = null;
        this.userDB = null;
        this.currentUser = null;
    }

    public boolean initialize() {
        return false;
    }

    public void shutdown() {
    }

    public boolean login(String username, String passwordHash) {
        return false;
    }

    public void logout() {
    }

    public boolean register(String username, String passwordHash) {
        return false;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<Game> searchGames(String criteria) {
        return new ArrayList<>();
    }

    public List<Game> filterGames(String genre, Integer playerCount, Double minRating) {
        return new ArrayList<>();
    }

    public Game getGameByID(String id) {
        return null;
    }

    public List<Game> getAllGames() {
        return new ArrayList<>();
    }

    public boolean viewGame(Game game) {
        return false;
    }

    public boolean createCollection(String name) {
        return false;
    }

    public boolean deleteCollection(String name) {
        return false;
    }

    public boolean addGameToCollection(String collectionName, Game game) {
        return false;
    }

    public boolean removeGameFromCollection(String collectionName, Game game) {
        return false;
    }

    public boolean addReview(Game game, int rating, String comment) {
        return false;
    }

    public boolean editReview(Game game, String newComment, int newRating) {
        return false;
    }

    public boolean deleteReview(Game game) {
        return false;
    }

    public String toString() {
        return "SystemController";
    }
}