package Model;

import java.io.*;
import java.util.Properties;

/**
 * Handles loading and saving system startup configuration.
 * Stores file paths for the game and user databases.
 */
public class ConfigurationManager {

    private String configFilePath;
    private String gameDBPath;
    private String userDBPath;

    /**
     * Constructs a ConfigurationManager with the given config file path.
     *
     * @param configFilePath path to the configuration file
     */
    public ConfigurationManager(String configFilePath) {
        this.configFilePath = configFilePath;
        this.gameDBPath     = "";
        this.userDBPath     = "";
    }

    /**
     * Loads configuration from the config file.
     * Falls back to default paths if the file is missing or a key is absent.
     */
    public void loadConfiguration() {
        Properties props = new Properties();

        try (BufferedReader reader = new BufferedReader(new FileReader(configFilePath))) {
            props.load(reader);
            gameDBPath = props.getProperty("gameDB", "data/games.xml");
            userDBPath = props.getProperty("userDB", "data/users.csv");
        } catch (IOException e) {
            System.err.println("Config file not found, using defaults: " + e.getMessage());
            gameDBPath = "data/games.xml";
            userDBPath = "data/users.csv";
        }
    }

    /**
     * Saves current configuration back to the config file.
     */
    public void saveConfiguration() {
        Properties props = new Properties();
        props.setProperty("gameDB", gameDBPath);
        props.setProperty("userDB", userDBPath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFilePath))) {
            props.store(writer, "Board Game Browser Configuration");
        } catch (IOException e) {
            System.err.println("Failed to save configuration: " + e.getMessage());
        }
    }

    /**
     * Returns the path to the game database file.
     *
     * @return game database file path
     */
    public String getGameDBPath() { return gameDBPath; }

    /**
     * Returns the path to the user database file.
     *
     * @return user database file path
     */
    public String getUserDBPath() { return userDBPath; }
}