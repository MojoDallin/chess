package dataaccess;

import exceptions.ResponseException;
import model.*;
import requests.*;
import results.LoginResult;

import java.util.HashMap;
import java.util.HashSet;

public class MySqlDataAccess implements DataAccess
{

    @Override
    public LoginResult registerUser(RegisterRequest registerRequest) throws ResponseException {
        return null;
    }

    @Override
    public int createNewGame(GameData data) throws ResponseException {
        return 0;
    }

    @Override
    public HashMap<Integer, GameData> getAllGames() {
        return null;
    }

    @Override
    public LoginResult loginUser(LoginRequest loginRequest) throws ResponseException {
        return null;
    }

    @Override
    public void joinGame(JoinRequest request) throws ResponseException {

    }

    @Override
    public void logoutUser(LogoutRequest logoutRequest) throws ResponseException {

    }

    @Override
    public void clearUsers() {

    }

    @Override
    public void clearGames() {

    }

    @Override
    public void clearAuthData() {

    }

    @Override
    public HashMap<Integer, UserData> getUsers() {
        return null;
    }

    @Override
    public HashSet<AuthData> getAuthData() {
        return null;
    }

    @Override
    public HashSet<String> getAuthTokens() {
        return null;
    }
}
