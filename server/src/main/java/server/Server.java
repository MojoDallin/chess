package server;

import dataaccess.DataAccess;
import dataaccess.DatabaseManager;
import dataaccess.MemoryDataAccess;
import dataaccess.MySqlDataAccess;
import exceptions.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.JoinData;
import model.UserData;
import requests.JoinRequest;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.LoginResult;
import service.GameService;
import service.UserService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final GameService gameService;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/db", this::clearData);
        javalin.delete("/session", this::logout);
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);

        javalin.exception(ResponseException.class, this::handleException);

        DataAccess access = new MemoryDataAccess();
        try {
            access = new MySqlDataAccess();
        } catch (ResponseException ex)
        {
            System.out.println("Unable to start MySQL server, using memory server instead: " + ex.getMessage());
        }
        this.userService = new UserService(access);
        this.gameService = new GameService(access);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context ctx) throws ResponseException
    {
        UserData newUser = new Gson().fromJson(ctx.body(), UserData.class);
        LoginResult result = userService.registerUser(new RegisterRequest(newUser.username(), newUser.password(), newUser.email()));
        login(ctx); // login automatically after registering
    }

    private void login(Context ctx) throws ResponseException
    {
        UserData user = new Gson().fromJson(ctx.body(), UserData.class);
        LoginResult result = userService.loginUser(new LoginRequest(user.username(), user.password()));
        ctx.status(200);
        ctx.result(new Gson().toJson(Map.of("authToken", result.authToken(), "username", result.username())));
    }

    private void logout(Context ctx) throws ResponseException
    {
        String token = ctx.header("authorization");
        userService.logoutUser(new LogoutRequest(ctx.header("authorization")));
        ctx.status(200);
    }

    private void clearData(Context ctx) throws ResponseException
    {
        userService.deleteUserData();
        gameService.deleteGameData();
        ctx.status(200);
    }

    private void createGame(Context ctx) throws ResponseException
    {
        GameData gameData = new Gson().fromJson(ctx.body(), GameData.class);
        String token = ctx.header("authorization");
        int id = gameService.createGame(gameData, token);
        ctx.status(200);
        ctx.result(new Gson().toJson(Map.of("gameID", id)));
    }

    private void joinGame(Context ctx) throws ResponseException
    {
        JoinData joinData = new Gson().fromJson(ctx.body(), JoinData.class);
        String token = ctx.header("authorization");
        gameService.joinGame(new JoinRequest(joinData, ""), token);
    }

    private void listGames(Context ctx) throws ResponseException
    {
        String token = ctx.header("authorization");
        HashMap<Integer, GameData> games = gameService.getGames(token);
        ctx.status(200);
        ctx.result(new Gson().toJson(Map.of("games", games.values())));
    }

    private void handleException(ResponseException exception, Context ctx)
    {
        ctx.status(exception.id());
        ctx.result(exception.toJson());
    }
}
