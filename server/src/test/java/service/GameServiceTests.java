package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import exceptions.ResponseException;
import model.GameData;
import model.JoinData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.JoinRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.LoginResult;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTests
{
    static DataAccess dataAccess = new MemoryDataAccess();
    static final GameService GAME_SERVICE = new GameService(dataAccess);
    static final UserService USER_SERVICE = new UserService(dataAccess);

    @BeforeEach
    void clear() throws ResponseException { GAME_SERVICE.deleteGameData(); }

    @Test
    void createGames() throws ResponseException
    {
        GAME_SERVICE.createGame(new GameData(0, "a", "b", "name", null), "abc");
    }

    @Test
    void joinGame() throws ResponseException
    {
        USER_SERVICE.registerUser(new RegisterRequest("user1", "password", "email"));
        USER_SERVICE.registerUser(new RegisterRequest("user2", "password", "email"));
        LoginResult result1 = USER_SERVICE.loginUser(new LoginRequest("user1", "password"));
        LoginResult result2 = USER_SERVICE.loginUser(new LoginRequest("user2", "password"));
        GAME_SERVICE.createGame(new GameData(0, null, null, "name", null), result1.authToken());

        GAME_SERVICE.joinGame(new JoinRequest(new JoinData("WHITE", 1)), result1.authToken());
        assertThrows(ResponseException.class, () -> GAME_SERVICE.joinGame(new JoinRequest(new JoinData("PURPLE", 1)), result1.authToken()));
        GAME_SERVICE.joinGame(new JoinRequest(new JoinData("BLACK", 1)), result2.authToken());
        assertThrows(ResponseException.class, () -> GAME_SERVICE.joinGame(new JoinRequest(new JoinData("WHITE", 1)), result2.authToken()));
    }

    @Test
    void gameAmounts() throws ResponseException
    {
        GAME_SERVICE.createGame(new GameData(0, null, null, "name", null), "abc");
        GAME_SERVICE.createGame(new GameData(1, null, null, "name1", null), "abc");
        assertEquals(2, GAME_SERVICE.getGames("abc").size());
        GAME_SERVICE.deleteGameData();
        assertEquals(0, GAME_SERVICE.getGames("abc").size());
    }
}
