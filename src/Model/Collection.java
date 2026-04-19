package Model;

import java.util.ArrayList;
import java.util.List;

public class Collection {

    private String name;
    private List<Game> games;

    public Collection(String name) {}

    public String getName() {}
    public void setName(String name) {}
    public int getSize() {}
    public List<Game> getGames() {}

    public boolean addGame(Game game) {}
    public boolean removeGame(Game game) {}
    public boolean containsGame(Game game) {}
    public Game getGameById(String gameID) {}

    @Override
    public String toString() {}
}