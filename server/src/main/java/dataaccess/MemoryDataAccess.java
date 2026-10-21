package dataaccess;

import exceptions.ResponseException;
import model.AuthData;
import model.GameData;
import model.JoinData;
import model.UserData;
import requests.JoinRequest;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.LoginResult;

import java.util.*;

public class MemoryDataAccess implements DataAccess
{
    private int nextGameID = 0;
    private int nextUserID = 0;
    final private HashMap<Integer, UserData> users = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();
    final private HashSet<AuthData> authData = new HashSet<>();

    @Override
    public LoginResult registerUser(RegisterRequest registerRequest) throws ResponseException
    {
        if(users.containsValue(new UserData(registerRequest.username(), "", "")))
        {
            throw new ResponseException(403, "Error: already taken");
        }
        if(registerRequest.email() == null || registerRequest.username() == null || registerRequest.password() == null)
        {
            throw new ResponseException(400, "Error: bad request");
        }
        UserData newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        users.put(nextUserID++, newUser);
        return new LoginResult(registerRequest.username(), randomToken());
    }

    @Override
    public LoginResult loginUser(LoginRequest loginRequest) throws ResponseException
    {
        if(loginRequest.username() == null || loginRequest.password() == null)
        {
            throw new ResponseException(400, "Error: bad request");
        }
        UserData data = findUserDataByUsername(loginRequest.username());
        if(data == null)
        {
            throw new ResponseException(401, "Error: username not found");
        }
        if(!Objects.equals(data.password(), loginRequest.password()))
        {
            throw new ResponseException(401, "Error: password is not correct");
        }
        String token = randomToken();
        //authData.remove(new AuthData("", loginRequest.username())); // delete old auth token (if applicable)
        authData.add(new AuthData(token, loginRequest.username()));
        return new LoginResult(loginRequest.username(), token);
    }

    @Override
    public void logoutUser(LogoutRequest logoutRequest) throws ResponseException
    {
        AuthData data = findAuthDataByToken(logoutRequest.authToken());
        if(data == null)
        {
            throw new ResponseException(401, "Error: unauthorized");
        }
        authData.remove(data);
    }

    @Override
    public void clearUsers()
    {
        users.clear();
        nextUserID = 0;
    }

    @Override
    public void clearGames()
    {
        games.clear();
        nextGameID = 0;
    }

    @Override
    public void clearAuthData()
    {
        authData.clear();
    }

    @Override
    public HashMap<Integer, UserData> getUsers()
    {
        return users;
    }

    @Override
    public HashSet<AuthData> getAuthData() { return authData; }

    @Override
    public HashSet<String> getAuthTokens()
    {
        HashSet<String> tokens = new HashSet<>();
        for(AuthData data : authData)
        {
            tokens.add(data.authToken());
        }
        return tokens;
    }

    @Override
    public int createNewGame(GameData data) throws ResponseException
    {
        if(data.gameName() == null)
        {
            throw new ResponseException(400, "Error: bad request");
        }
        nextGameID++;
        data = new GameData(nextGameID, data.whiteUsername(), data.blackUsername(), data.gameName(), data.game());
        games.put(nextGameID, data);
        return nextGameID;
    }

    @Override
    public void joinGame(JoinRequest request) throws ResponseException
    {
        String color = request.data().playerColor();
        if(color == null)
        {
            throw new ResponseException(400, "Error: bad request");
        }
        boolean validColor = color.contains("WHITE") || color.contains("BLACK");
        if(!validColor || request.data().gameID() < 1)
        {
            throw new ResponseException(400, "Error: bad request");
        }
        GameData gameData = findGameDataByID(request.data().gameID());
        if(gameData == null)
        {
            throw new ResponseException(500, "Error: game ID " + request.data().gameID() + " not found");
        }
        boolean whiteTaken = gameData.whiteUsername() != null;
        boolean blackTaken = gameData.blackUsername() != null;
        if((whiteTaken && color.equals("WHITE")) || (blackTaken && color.equals("BLACK")))
        {
            throw new ResponseException(403, "Error: already taken");
        }
        games.remove(request.data().gameID());
        gameData = gameData.addPlayer(color, request.username());
        games.put(request.data().gameID(), gameData);
    }

    @Override
    public HashMap<Integer, GameData> getAllGames()
    {
        return games;
    }

    private String randomToken() { return UUID.randomUUID().toString(); };
    private UserData findUserDataByUsername(String username)
    {
        if(!users.containsValue(new UserData(username, "", "")))
        {
            return null;
        }
        for(Map.Entry<Integer, UserData> entry : users.entrySet())
        {
            if(Objects.equals(entry.getValue().username(), username))
            {
                return entry.getValue();
            }
        }
        return null;
    }
    private GameData findGameDataByID(int id)
    {
        if(!games.containsValue(new GameData(id, null, null, null, null)))
        {
            return null;
        }
        for(Map.Entry<Integer, GameData> entry : games.entrySet())
        {
            if(Objects.equals(entry.getValue().gameID(), id))
            {
                return entry.getValue();
            }
        }
        return null;
    }
    private AuthData findAuthDataByToken(String authToken)
    {
        for(AuthData data : authData)
        {
            if(Objects.equals(data.authToken(), authToken))
            {
                return data;
            }
        }
        return null;
    }
}
