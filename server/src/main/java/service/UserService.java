package service;

import requests.*;
import results.*;

public class UserService
{
    public LoginResult registerUser(RegisterRequest registerRequest) { return new LoginResult("", ""); }
    public LoginResult loginUser(LoginRequest loginRequest) { return new LoginResult("", ""); }
    public void logoutUser(LogoutRequest logoutRequest) {}
}
