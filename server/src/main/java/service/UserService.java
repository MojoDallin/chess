package service;

import dataaccess.DataAccess;
import exceptions.ResponseException;
import requests.*;
import results.*;

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
}
