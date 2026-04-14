package Controller;

import Model.ConfigurationManager;
import Model.GameDatabase;
import Model.UserDatabase;

public class SystemController {

    private ConfigurationManager configManager;
    private GameDatabase gameDatabase;
    private UserDatabase userDatabase;

    public SystemController(String configPath) {

    }

    public void initializeSystem() { }
    public void shutdownSystem() { }
    public void saveAllData() { }

    public GameDatabase getGameDatabase() { return null; }
    public UserDatabase getUserDatabase() { return null; }
}
