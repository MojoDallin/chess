package service;

import dataaccess.DataAccess;
import exceptions.ResponseException;
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
    public int createGame(GameData data) throws ResponseException
    {
        return dataAccess.createNewGame(data);
    }
    public void deleteGameData()
    {
        dataAccess.clearGames();
    }
}
