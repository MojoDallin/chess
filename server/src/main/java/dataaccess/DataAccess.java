package dataaccess;

import exceptions.ResponseException;
import model.UserData;
import results.*;
import requests.*;

import java.util.HashMap;

public interface DataAccess {
    // create functions
    LoginResult registerUser(RegisterRequest registerRequest) throws ResponseException;
    // read functions

    // update functions
    LoginResult loginUser(LoginRequest loginRequest) throws ResponseException;
    // delete functions
    void clearUsers();
    void clearGames();
    void clearAuthData();

    // other functions
    HashMap<Integer, UserData> getUsers();
}
