package dataaccess;

import exceptions.ResponseException;
import model.GameData;
import model.UserData;
import requests.RegisterRequest;
import results.LoginResult;

import java.util.HashMap;
import java.util.UUID;

public class MemoryDataAccess implements DataAccess
{
    private int nextGameID = 1;
    private int nextUserID = 1;
    final private HashMap<Integer, UserData> users = new HashMap<Integer, UserData>();
    final private HashMap<Integer, GameData> games = new HashMap<Integer, GameData>();

    @Override
    public LoginResult registerUser(RegisterRequest registerRequest) throws ResponseException
    {
        if(users.containsValue(new UserData(registerRequest.username(), "", "")))
        {
            throw new ResponseException(403, "Error: username already taken");
        }
        UserData newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        users.put(nextUserID++, newUser);
        return new LoginResult(registerRequest.username(), randomToken());
    }

    @Override
    public void clearUsers()
    {
        users.clear();
    }

    @Override
    public void clearGames()
    {
        games.clear();
    }

    @Override
    public HashMap<Integer, UserData> getUsers()
    {
        return users;
    }

    private String randomToken() { return UUID.randomUUID().toString(); };
}
