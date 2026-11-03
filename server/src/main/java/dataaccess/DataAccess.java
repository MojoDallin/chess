package dataaccess;

import exceptions.ResponseException;
import model.AuthData;
import model.GameData;
import model.JoinData;
import model.UserData;
import results.*;
import requests.*;

import java.util.HashMap;
import java.util.HashSet;

public interface DataAccess {
    // create functions
    LoginResult registerUser(RegisterRequest registerRequest) throws ResponseException;
    int createNewGame(GameData data, String token) throws ResponseException;
    // read functions
    HashMap<Integer, GameData> getAllGames(String token) throws ResponseException;
    // update functions
    LoginResult loginUser(LoginRequest loginRequest) throws ResponseException;
    void joinGame(JoinRequest request, String token) throws ResponseException;
    // delete functions
    void logoutUser(LogoutRequest logoutRequest) throws ResponseException;
    void clearUsers() throws ResponseException;
    void clearGames() throws ResponseException;
    void clearAuthData() throws ResponseException;

    // other functions
    HashMap<Integer, UserData> getUsers();
    HashSet<AuthData> getAuthData();
    HashSet<String> getAuthTokens();
}
