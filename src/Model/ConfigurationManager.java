package Model;

public class ConfigurationManager {

    private static final String KEY_GAME_DB = "gameDBPath";
    private static final String KEY_USER_DB = "userDBPath";

    private String configFilePath;
    private String gameDBPath;
    private String userDBPath;

    public ConfigurationManager(String configFilePath) {
    }

    public void loadConfiguration() {
    }

    public void saveConfiguration() {
    }

    public String getGameDBPath() { return null; }

    public String getUserDBPath() { return null; }

    public String getConfigFilePath() { return null; }

    public boolean isValid() { return false; }

    @Override
    public String toString() { return null; }
}