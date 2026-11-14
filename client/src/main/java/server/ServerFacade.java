package server;

import com.google.gson.Gson;
import exceptions.ResponseException;
import model.*;
import requests.*;
import results.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerFacade
{
    private final HttpClient client = HttpClient.newHttpClient();
    private final String url;

    public ServerFacade(int port) { url = "http://localhost:" + port; }

    public LoginResult registerUser(RegisterRequest registerRequest) throws ResponseException
    {
        HttpRequest httpRequest = createRequest("POST", "/user", registerRequest, null);
        HttpResponse<String> response = sendRequest(httpRequest);
        LoginResult loginResult = handleResponse(response, LoginResult.class);
        return loginUser(new LoginRequest(loginResult.username(), registerRequest.password()));
    }

    public LoginResult loginUser(LoginRequest loginRequest) throws ResponseException
    {
        HttpRequest httpRequest = createRequest("POST", "/session", loginRequest, null);
        HttpResponse<String> response = sendRequest(httpRequest);
        return handleResponse(response, LoginResult.class);
    }

    public void deleteAllData() throws ResponseException
    {
        HttpRequest httpRequest = createRequest("DELETE", "/db", null, null);
        sendRequest(httpRequest);
    }

    public boolean logoutUser(LogoutRequest logoutRequest) throws ResponseException
    {
        HttpRequest httpRequest = createRequest("DELETE", "/session", logoutRequest, logoutRequest.authToken());
        HttpResponse<String> response = sendRequest(httpRequest);
        if(wasSuccessful(response.statusCode()))
        {
            return true;
        }
        throw new ResponseException(response.statusCode(), getBodyMessage(response.body()));
    }

    public ArrayList<GameData> listGames(String authToken) throws ResponseException
    {
        HttpRequest httpRequest = createRequest("GET", "/game", null, authToken);
        HttpResponse<String> response = sendRequest(httpRequest);
        if(wasSuccessful(response.statusCode()))
        {
            //Object allGames = new Gson().fromJson(response.body(), GameListResult.class).get("games"); // only accepted type is Object
            GameListResult allGames = handleResponse(response, GameListResult.class);
            return (ArrayList<GameData>) allGames.games(); // cast for proper return time
        }
        throw new ResponseException(response.statusCode(), getBodyMessage(response.body()));
    }

    public int createGame(GameData gameData, String authToken) throws ResponseException
    {
        HttpRequest httpRequest = createRequest("POST", "/game", gameData, authToken);
        HttpResponse<String> response = sendRequest(httpRequest);
        if(wasSuccessful(response.statusCode()))
        {
            GameData data = handleResponse(response, GameData.class);
            return data.gameID();
        }
        throw new ResponseException(response.statusCode(), getBodyMessage(response.body()));
    }

    public void joinGame(JoinData joinData, String authToken) throws ResponseException
    {
        HttpRequest httpRequest = createRequest("PUT", "/game", joinData, authToken);
        HttpResponse<String> response = sendRequest(httpRequest);
        if(!wasSuccessful(response.statusCode()))
        {
            throw new ResponseException(response.statusCode(), getBodyMessage(response.body()));
        }
    }

    private HttpRequest createRequest(String method, String path, Object body, String token)
    {
        HttpRequest.Builder req = HttpRequest.newBuilder()
                .uri(URI.create(url + path))
                .method(method, convertBody(body));
        if(token != null)
        {
            req.header("authorization", token);
        }
        if(body != null)
        {
            req.setHeader("Content-Type", "application/json");
        }
        return req.build();
    }

    private HttpRequest.BodyPublisher convertBody(Object request)
    {
        if(request == null)
        {
            return HttpRequest.BodyPublishers.noBody();
        }
        return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException
    {
        try
        {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex)
        {
            throw new ResponseException(400, "Error: " + ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseType) throws ResponseException
    {
        if(wasSuccessful(response.statusCode()))
        {
            if(responseType == null)
            {
                return null;
            }
            return new Gson().fromJson(response.body(), responseType);
        }
        String body = response.body();
        if(body == null)
        {
            throw new ResponseException(500, "Error: " + response.statusCode());
        }
        throw new ResponseException(500, getBodyMessage(body));
    }

    private boolean wasSuccessful(int status)
    {
        return status / 100 == 2;
    }
    private String getBodyMessage(String body) { return new Gson().fromJson(body, HashMap.class).get("message").toString(); }
}
