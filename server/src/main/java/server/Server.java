package server;

import dataaccess.MemoryDataAccess;
import exceptions.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
import com.google.gson.Gson;
import model.UserData;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.LoginResult;
import service.UserService;

import java.util.HashSet;
import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserService userService;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/db", this::clearData);
        javalin.delete("/session", this::logout);

        javalin.exception(ResponseException.class, this::handleException);


        this.userService = new UserService(new MemoryDataAccess());
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
        if(isAuthorized(ctx))
        {
            userService.logoutUser(new LogoutRequest(ctx.header("authorization")));
            ctx.status(200);
        }
    }

    private void clearData(Context ctx)
    {
        userService.deleteUserData();
        ctx.status(200);
    }

    private void handleException(ResponseException exception, Context ctx)
    {
        ctx.status(exception.id());
        ctx.result(exception.toJson());
    }

    private boolean isAuthorized(Context ctx)
    {
        String token = ctx.header("authorization");
        HashSet<String> tokens = userService.getAuthTokens();
        if(tokens.contains(token))
        {
            return true;
        }
        ctx.status(401);
        ctx.result(new Gson().toJson(Map.of("message", "Error: unauthorized")));
        return false;
    }
}
