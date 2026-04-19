package Model;

import java.io.*;

/**
 * Holds file paths for the system config.
 * Reads and writes a simple key=value file.
 */
public class ConfigurationManager {

    private static final String KEY_GAME_DB = "gameDBPath";
    private static final String KEY_USER_DB = "userDBPath";

    private final String configFilePath;
    private String gameDBPath;
    private String userDBPath;

    /**
     * Create config manager with file path.
     * @param configFilePath path to config file
     */
    public ConfigurationManager(String configFilePath) {
        this.configFilePath = configFilePath;
        this.gameDBPath = null;
        this.userDBPath = null;
    }

    /**
     * Read config file and set paths.
     */
    public void loadConfiguration() {

        gameDBPath = null;
        userDBPath = null;

        File file = new File(configFilePath);

        if (!file.exists()) {
            System.out.println("WARNING: Config file not found: " + configFilePath);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.isBlank()) continue;
                if (line.trim().startsWith("#")) continue;

                if (!line.contains("=")) {
                    System.out.println("WARNING: Bad line " + lineNumber);
                    continue;
                }

                int index = line.indexOf("=");
                String key = line.substring(0, index).trim();
                String value = line.substring(index + 1).trim();

                if (key.isBlank()) continue;

                if (key.equals(KEY_GAME_DB)) {
                    gameDBPath = value.isEmpty() ? null : value;
                } else if (key.equals(KEY_USER_DB)) {
                    userDBPath = value.isEmpty() ? null : value;
                }
            }

        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * Write current paths to config file.
     */
    public void saveConfiguration() {

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(configFilePath, false))) {

            writer.write("# Config");
            writer.newLine();
            writer.newLine();

            if (gameDBPath != null && !gameDBPath.isBlank()) {
                writer.write(KEY_GAME_DB + "=" + gameDBPath);
                writer.newLine();
            }

            if (userDBPath != null && !userDBPath.isBlank()) {
                writer.write(KEY_USER_DB + "=" + userDBPath);
                writer.newLine();
            }

        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    /** @return game database path */
    public String getGameDBPath() {
        return gameDBPath;
    }

    /** @return user database path */
    public String getUserDBPath() {
        return userDBPath;
    }

    /** @return config file path */
    public String getConfigFilePath() {
        return configFilePath;
    }

    /** @return true if both paths are set */
    public boolean isValid() {
        return gameDBPath != null && !gameDBPath.isBlank()
                && userDBPath != null && !userDBPath.isBlank();
    }

    /** @return debug string */
    @Override
    public String toString() {
        return "ConfigurationManager | Config: " + configFilePath
                + " | GameDB: " + gameDBPath
                + " | UserDB: " + userDBPath
                + " | Valid: " + isValid();
    }
}