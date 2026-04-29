package Controller;

import Model.*;

/**
 * Handles system startup and shutdown.
 * Initializes ConfigurationManager, GameDatabase, and UserDatabase in the
 * correct order and saves all data on exit (R1, R2, R6).
 *
 * Design pattern: Facade — one simple interface over the startup sequence.
 */
public class SystemController {

    private final ConfigurationManager configManager;
    private final GameDatabase gameDatabase;
    private final UserDatabase userDatabase;

    /**
     * Builds the system from the given config file path and loads all data.
     *
     * @param configPath path to config.txt
     */
    public SystemController(String configPath) {
        configManager = new ConfigurationManager(configPath);
        configManager.loadConfiguration();
        gameDatabase  = new GameDatabase(configManager.getGameDBPath());
        userDatabase  = new UserDatabase(configManager.getUserDBPath());
    }

    /**
     * Saves all data and config on shutdown (R2, R6).
     */
    public void shutdownSystem() {
        saveAllData();
    }

    /**
     * Persists games, users, and configuration to their respective files.
     */
    public void saveAllData() {
        gameDatabase.saveGames();
        userDatabase.saveUsers();
        configManager.saveConfiguration();
    }

    /**
     * @return the loaded game database
     */
    public GameDatabase getGameDatabase() { return gameDatabase; }

    /**
     * @return the loaded user database
     */
    public UserDatabase getUserDatabase() { return userDatabase; }
}