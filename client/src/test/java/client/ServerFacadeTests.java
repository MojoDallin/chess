package client;

import chess.ChessGame;
import exceptions.ResponseException;
import model.GameData;
import org.junit.jupiter.api.*;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.LoginResult;
import server.Server;
import server.ServerFacade;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    void clear() throws ResponseException
    {
        facade.deleteAllData();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    void registerUser() throws ResponseException
    {
        LoginResult result = facade.registerUser(new RegisterRequest("username", "password", "email"));
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    void logoutUser() throws ResponseException
    {
        LoginResult result = facade.registerUser(new RegisterRequest("username", "password", "email"));
        boolean wasLoggedOut = facade.logoutUser(new LogoutRequest(result.authToken()));
        assertTrue(wasLoggedOut);
    }

    @Test
    void createGame() throws ResponseException
    {
        LoginResult result = facade.registerUser(new RegisterRequest("username", "password", "email"));
        int id = facade.createGame(new GameData(99, "whiteUsername", "blackUsername", "name", new ChessGame()), result.authToken());
        assertEquals(id, 1);
    }

    @Test
    void listGames() throws ResponseException
    {
        LoginResult result = facade.registerUser(new RegisterRequest("username", "password", "email"));
        HashMap<Integer, GameData> games = facade.listGames(result.authToken());
    }
}
