package service;

import dataaccess.DataAccess;
import model.GameData;

import java.util.HashMap;

public class GameService
{
    private final DataAccess dataAccess;
    public GameService(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    public HashMap<Integer, GameData> getGames()
    {
        return dataAccess.getAllGames();
    }
}
