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

    // delete functions
    void clearUsers();
    void clearGames();

    // other functions
    HashMap<Integer, UserData> getUsers();
}
