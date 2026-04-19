package Model;

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
    }

    public void saveConfiguration() {
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