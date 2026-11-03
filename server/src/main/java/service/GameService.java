package service;

import dataaccess.DataAccess;
import exceptions.ResponseException;
import model.GameData;
import requests.JoinRequest;

import java.util.HashMap;

public class GameService
{
    private final DataAccess dataAccess;
    public GameService(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    public HashMap<Integer, GameData> getGames(String token) throws ResponseException
    {
        return dataAccess.getAllGames(token);
    }
    public int createGame(GameData data, String token) throws ResponseException
    {
        return dataAccess.createNewGame(data, token);
    }
    public void deleteGameData() throws ResponseException
    {
        dataAccess.clearGames();
    }
    public void joinGame(JoinRequest request, String token) throws ResponseException
    {
        dataAccess.joinGame(request, token);
    }
}
