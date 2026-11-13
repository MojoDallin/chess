package server;

import com.google.gson.Gson;
import exceptions.ResponseException;
import requests.*;
import results.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade
{
    private final HttpClient client = HttpClient.newHttpClient();
    private final String url;

    public ServerFacade(int port) { url = "http://localhost:" + port; }

    public LoginResult registerUser(RegisterRequest registerRequest) throws ResponseException
    {
        HttpRequest httpRequest = createRequest("POST", "/user", registerRequest);
        HttpResponse<String> response = sendRequest(httpRequest);
        LoginResult loginResult = handleResponse(response, LoginResult.class);
        return loginResult;
        //return loginUser(new LoginRequest(loginResult.username(), registerRequest.password()));
    }

//    public LoginResult loginUser(LoginRequest loginRequest) throws ResponseException
//    {
//
//    }

    private HttpRequest createRequest(String method, String path, Object body)
    {
        HttpRequest.Builder req = HttpRequest.newBuilder()
                .uri(URI.create(url + path))
                .method(method, convertBody(body));
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
        throw new ResponseException(999, "not implemented yet");
    }

    private boolean wasSuccessful(int status)
    {
        return status / 100 == 2;
    }
}
