package service;

import dataaccess.DataAccess;
import exceptions.ResponseException;
import model.UserData;
import requests.*;
import results.*;

import java.util.HashMap;

public class UserService
{
    private final DataAccess dataAccess;
    public UserService(DataAccess dataAccess)
    {
        this.dataAccess = dataAccess;
    }

    public LoginResult registerUser(RegisterRequest registerRequest) throws ResponseException
    {

        return dataAccess.registerUser(registerRequest);
    }
    public LoginResult loginUser(LoginRequest loginRequest) { return new LoginResult("", ""); }
    public void logoutUser(LogoutRequest logoutRequest) {}

    public void deleteUserData()
    {
        dataAccess.clearUsers();
    }

    public HashMap<Integer, UserData> getUserData()
    {
        return dataAccess.getUsers();
    }
}
