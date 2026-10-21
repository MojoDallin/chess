package service;

import dataaccess.MemoryDataAccess;
import exceptions.ResponseException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.RegisterRequest;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTests
{
    static final UserService userService = new UserService(new MemoryDataAccess());

    @BeforeEach
    void clear() { userService.deleteUserData(); }

    @Test
    void registerUser() throws ResponseException
    {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        userService.registerUser(request);

        HashMap<Integer, UserData> userData = userService.getUserData();
        assertEquals(1, userData.size()); // only 1 should be present
        assertTrue(userData.containsValue(new UserData("username", "password", "email"))); // check if the one is correct
    }

    @Test
    void clearUsers() throws ResponseException
    {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        userService.registerUser(request);

        userService.deleteUserData();
        assertEquals(0, userService.getUserData().size()); // should have nothing inside it
    }
}
