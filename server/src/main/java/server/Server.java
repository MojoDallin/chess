package server;

import dataaccess.MemoryDataAccess;
import exceptions.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
import com.google.gson.Gson;
import model.UserData;
import requests.RegisterRequest;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final UserService userService;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register);

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
        results.LoginResult result = userService.registerUser(new RegisterRequest(newUser.username(), newUser.password(), newUser.email()));
    }

    private void handleException(ResponseException exception, Context ctx)
    {
        ctx.status(exception.id());
        ctx.result(exception.toJson());
    }
}
