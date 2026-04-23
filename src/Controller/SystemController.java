package Controller;

import Model.ConfigurationManager;
import Model.GameDatabase;
import Model.UserDatabase;

/**
 * Controls system-level operations such as initialization,
 * shutdown, and data persistence.
 */
public class SystemController {

    private ConfigurationManager configManager;
    private GameDatabase gameDatabase;
    private UserDatabase userDatabase;

    /**
     * Constructs the SystemController using a configuration file path.
     *
     * @param configPath the path to the configuration file
     */
    public SystemController(String configPath) {
        configManager = new ConfigurationManager(configPath);
        gameDatabase = new GameDatabase();
        userDatabase = new UserDatabase();
    }

    /**
     * Initializes the system by loading configuration and databases.
     */
    public void initializeSystem() {
        configManager.loadConfiguration();

        gameDatabase.loadGames(configManager.getGameDBPath());
        userDatabase.loadUsers(configManager.getUserDBPath());
    }

    /**
     * Handles system shutdown and ensures all data is saved.
     */
    public void shutdownSystem() {
        saveAllData();
    }

    /**
     * Saves all system data including games, users, and configuration.
     */
    public void saveAllData() {
        gameDatabase.saveGames();
        userDatabase.saveUsers();
        configManager.saveConfiguration();
    }

    /**
     * Provides access to the GameDatabase.
     *
     * @return the game database
     */
    public GameDatabase getGameDatabase() {
        return gameDatabase;
    }

    /**
     * Provides access to the UserDatabase.
     *
     * @return the user database
     */
    public UserDatabase getUserDatabase() {
        return userDatabase;
    }
}