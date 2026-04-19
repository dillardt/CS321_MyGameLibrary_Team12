package Model;

import java.io.*;

public class ConfigurationManager {

    private static final String KEY_GAME_DB = "gameDBPath";
    private static final String KEY_USER_DB = "userDBPath";

    private final String configFilePath;
    private String gameDBPath;
    private String userDBPath;

    public ConfigurationManager(String configFilePath) {
        this.configFilePath = configFilePath;
        this.gameDBPath = null;
        this.userDBPath = null;
    }

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

    public String getGameDBPath() {
        return gameDBPath;
    }

    public String getUserDBPath() {
        return userDBPath;
    }

    public String getConfigFilePath() {
        return configFilePath;
    }

    public boolean isValid() {
        return gameDBPath != null && !gameDBPath.isBlank()
                && userDBPath != null && !userDBPath.isBlank();
    }

    @Override
    public String toString() {
        return "ConfigurationManager | Config: " + configFilePath
                + " | GameDB: " + gameDBPath
                + " | UserDB: " + userDBPath
                + " | Valid: " + isValid();
    }
}