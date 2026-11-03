package dataaccess.sql;

import dataaccess.MySqlDataAccess;
import exceptions.ResponseException;
import model.GameData;
import model.JoinData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.JoinRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.LoginResult;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MySQLGameServiceTests
{
    MySqlDataAccess dataAccess = new MySqlDataAccess();
    final GameService GAME_SERVICE = new GameService(dataAccess);
    final UserService USER_SERVICE = new UserService(dataAccess);

    MySQLGameServiceTests() throws ResponseException {}

    @BeforeEach
    void clear() throws ResponseException { GAME_SERVICE.deleteGameData(); USER_SERVICE.deleteUserData(); }

    @Test
    void createGames() throws ResponseException
    {
        USER_SERVICE.registerUser(new RegisterRequest("username", "password", "email"));
        LoginResult result = USER_SERVICE.loginUser(new LoginRequest("username", "password"));
        GAME_SERVICE.createGame(new GameData(0, "a", "b", "name", null), result.authToken());
    }

    @Test
    void joinGame() throws ResponseException
    {
        USER_SERVICE.registerUser(new RegisterRequest("username", "password", "email"));
        LoginResult result = USER_SERVICE.loginUser(new LoginRequest("username", "password"));
        String token = result.authToken();

        GAME_SERVICE.createGame(new GameData(0, null, null, "name", null), token);
        GAME_SERVICE.joinGame(new JoinRequest(new JoinData("WHITE", 1), "user"), token);
        assertThrows(ResponseException.class, () -> GAME_SERVICE.joinGame(new JoinRequest(new JoinData("PURPLE", 1), "user4"), token));
        GAME_SERVICE.joinGame(new JoinRequest(new JoinData("BLACK", 1), "user2"), token);
        assertThrows(ResponseException.class, () -> GAME_SERVICE.joinGame(new JoinRequest(new JoinData("WHITE", 1), "user3"), token));
    }

    @Test
    void gameAmounts() throws ResponseException
    {
        USER_SERVICE.registerUser(new RegisterRequest("username", "password", "email"));
        LoginResult result = USER_SERVICE.loginUser(new LoginRequest("username", "password"));
        String token = result.authToken();

        GAME_SERVICE.createGame(new GameData(0, null, null, "name", null), token);
        GAME_SERVICE.createGame(new GameData(1, null, null, "name1", null), token);
        assertEquals(2, GAME_SERVICE.getGames(token).size());
        GAME_SERVICE.deleteGameData();
        assertEquals(0, GAME_SERVICE.getGames(token).size());
    }
}
