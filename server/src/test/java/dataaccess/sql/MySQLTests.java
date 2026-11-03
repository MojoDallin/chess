package dataaccess.sql;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.DataAccess;
import exceptions.ResponseException;
import model.GameData;
import model.JoinData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import requests.JoinRequest;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.LoginResult;
import service.GameService;
import service.UserService;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MySQLTests
{
    DataAccess sqlAccess = new MySqlDataAccess();
    final UserService userService = new UserService(sqlAccess);
    final GameService gameService = new GameService(sqlAccess);

    MySQLTests() throws ResponseException {}

    @BeforeEach
    void clear() throws ResponseException { userService.deleteUserData(); gameService.deleteGameData(); }

    @Test
    void registerUser() throws ResponseException
    {
        RegisterRequest request = new RegisterRequest("username", "password", "email");

        assertDoesNotThrow(() -> sqlAccess.registerUser(request));
    }

    @Test
    void listUsers() throws ResponseException
    {
        HashMap<Integer, UserData> expected = new HashMap<>();
        UserData data1 = new UserData("username", BCrypt.hashpw("password", BCrypt.gensalt()), "email");
        UserData data2 = new UserData("username2", BCrypt.hashpw("password2", BCrypt.gensalt()), "email2");
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        RegisterRequest request2 = new RegisterRequest("username2", "password2", "email2");
        userService.registerUser(request);
        userService.registerUser(request2);
        expected.put(1, data1);
        expected.put(2, data2);

        assertUserDataEqual(expected, userService.getUserData());
    }

    @Test
    void deleteUserData() throws ResponseException
    {
        UserData data1 = new UserData("username", BCrypt.hashpw("password", BCrypt.gensalt()), "email");
        UserData data2 = new UserData("username2", BCrypt.hashpw("password2", BCrypt.gensalt()), "email2");
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        RegisterRequest request2 = new RegisterRequest("username2", "password2", "email2");
        userService.registerUser(request);
        userService.registerUser(request2);
        userService.loginUser(new LoginRequest("username", "password"));
        userService.loginUser(new LoginRequest("username2", "password2"));

        assertEquals(2, userService.getAuthData().size());
        userService.deleteUserData();
        assertEquals(0, userService.getUserData().size());
        assertEquals(0, userService.getAuthData().size());
        assertEquals(0, userService.getAuthTokens().size());
    }

    @Test
    void logoutUser() throws ResponseException
    {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        userService.registerUser(request);

        LoginResult result = userService.loginUser(new LoginRequest("username", "password"));
        assertThrows(ResponseException.class, () -> userService.logoutUser(new LogoutRequest("invalid token")));
        userService.logoutUser(new LogoutRequest(result.authToken()));
        assertThrows(ResponseException.class, () -> userService.logoutUser(new LogoutRequest(result.authToken()))); // double logout
        userService.loginUser(new LoginRequest("username", "password"));
        assertThrows(ResponseException.class, () -> userService.logoutUser(new LogoutRequest("invalid token")));
    }

    @Test
    void createGame() throws ResponseException
    {
        userService.registerUser(new RegisterRequest("username", "password", "email"));
        LoginResult result = userService.loginUser(new LoginRequest("username", "password"));
        gameService.createGame(new GameData(99, "username", "username2", "testGame", new ChessGame()), result.authToken());
        HashMap<Integer, GameData> allGames = gameService.getGames(result.authToken());

        assertThrows(ResponseException.class, () -> gameService.getGames("invalid token"));
        assertEquals(1, allGames.size());
        assertEquals(new ChessGame(), allGames.get(1).game());

    }

    @Test
    void joinGame() throws ResponseException
    {
        userService.registerUser(new RegisterRequest("username", "password", "email"));
        LoginResult result = userService.loginUser(new LoginRequest("username", "password"));
        userService.registerUser(new RegisterRequest("blackUsername", "password", "email"));
        LoginResult result2 = userService.loginUser(new LoginRequest("blackUsername", "password"));
        gameService.createGame(new GameData(99, "username", null, "testGame", new ChessGame()), result.authToken());
        HashMap<Integer, GameData> allGames = gameService.getGames(result.authToken());
        assertNull(allGames.get(1).blackUsername()); // no black player (yet)
        JoinRequest request = new JoinRequest(new JoinData("BLACK", 1));
        assertThrows(ResponseException.class, () -> gameService.joinGame(request, "invalid_token"));
        gameService.joinGame(request, result2.authToken());
        allGames = gameService.getGames(result.authToken());

        assertEquals("blackUsername", gameService.getGames(result.authToken()).get(1).blackUsername());
    }

    private void assertUserDataEqual(HashMap<Integer, UserData> expected, HashMap<Integer, UserData> actual)
    {
        UserData[] expectedArr = expected.values().toArray(new UserData[0]);
        UserData[] actualArr = actual.values().toArray(new UserData[0]);
        for(int i = 0; i < actual.size(); i++)
        {
            assertEquals(expectedArr[i].username(), actualArr[i].username());
        }
    }
}
