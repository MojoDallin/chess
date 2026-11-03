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
    final GameService gameService = new GameService(dataAccess);
    final UserService userService = new UserService(dataAccess);

    MySQLGameServiceTests() throws ResponseException {}

    @BeforeEach
    void clear() throws ResponseException { gameService.deleteGameData(); userService.deleteUserData(); }

    @Test
    void createGames() throws ResponseException
    {
        userService.registerUser(new RegisterRequest("username", "password", "email"));
        LoginResult result = userService.loginUser(new LoginRequest("username", "password"));
        gameService.createGame(new GameData(0, "a", "b", "name", null), result.authToken());
    }

    @Test
    void joinGame() throws ResponseException
    {
        userService.registerUser(new RegisterRequest("username", "password", "email"));
        LoginResult result = userService.loginUser(new LoginRequest("username", "password"));
        String token = result.authToken();

        gameService.createGame(new GameData(0, null, null, "name", null), token);
        gameService.joinGame(new JoinRequest(new JoinData("WHITE", 1), "user"), token);
        assertThrows(ResponseException.class, () -> gameService.joinGame(new JoinRequest(new JoinData("PURPLE", 1), "user4"), token));
        gameService.joinGame(new JoinRequest(new JoinData("BLACK", 1), "user2"), token);
        assertThrows(ResponseException.class, () -> gameService.joinGame(new JoinRequest(new JoinData("WHITE", 1), "user3"), token));
    }

    @Test
    void gameAmounts() throws ResponseException
    {
        userService.registerUser(new RegisterRequest("username", "password", "email"));
        LoginResult result = userService.loginUser(new LoginRequest("username", "password"));
        String token = result.authToken();

        gameService.createGame(new GameData(0, null, null, "name", null), token);
        gameService.createGame(new GameData(1, null, null, "name1", null), token);
        assertEquals(2, gameService.getGames(token).size());
        gameService.deleteGameData();
        assertEquals(0, gameService.getGames(token).size());
    }
}
