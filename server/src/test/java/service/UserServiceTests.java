package service;

import dataaccess.MemoryDataAccess;
import exceptions.ResponseException;
import model.UserData;
import org.eclipse.jetty.util.log.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
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
        assertEquals(1, userData.size());
        assertTrue(userData.containsValue(new UserData("username", "password", "email")));
    }

    @Test
    void registerBadUser() throws ResponseException
    {
        RegisterRequest request = new RegisterRequest(null, "password", "email");
        RegisterRequest request2 = new RegisterRequest("username", null, "email");
        RegisterRequest request3 = new RegisterRequest("username", "password", null);

        assertThrows(ResponseException.class, () -> userService.registerUser(request)); // bad username
        assertThrows(ResponseException.class, () -> userService.registerUser(request2)); // bad password
        assertThrows(ResponseException.class, () -> userService.registerUser(request3)); // bad email
    }

    @Test
    void multipleRegisters() throws ResponseException
    {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        RegisterRequest request2 = new RegisterRequest("username2", "password", "email");
        RegisterRequest request3 = new RegisterRequest("username", "difPassword", "difEmail");
        userService.registerUser(request);
        userService.registerUser(request2);

        HashMap<Integer, UserData> userData = userService.getUserData();
        assertEquals(2, userData.size());
        assertTrue(userData.containsValue(new UserData("username", "password", "email")));
        assertTrue(userData.containsValue(new UserData("username2", "password", "email")));
        assertThrows(ResponseException.class, () -> userService.registerUser(request3)); // duplicate username
    }

    @Test
    void loginUser() throws ResponseException
    {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        userService.registerUser(request);

        assertThrows(ResponseException.class, () -> userService.loginUser(new LoginRequest("username", "password_wrong")));
        assertThrows(ResponseException.class, () -> userService.loginUser(new LoginRequest("username_wrong", "password")));
        userService.loginUser(new LoginRequest("username", "password"));
        userService.loginUser(new LoginRequest("username", "password")); // login twice
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
