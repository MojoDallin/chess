package dataaccess.sql;

import dataaccess.MySqlDataAccess;
import exceptions.ResponseException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.LoginResult;
import service.UserService;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MySQLUserServiceTests
{
    final UserService userService = new UserService(new MySqlDataAccess());

    MySQLUserServiceTests() throws ResponseException {}

    @BeforeEach
    void clear() throws ResponseException { userService.deleteUserData(); }

    @Test
    void registerUser() throws ResponseException
    {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        userService.registerUser(request);

        HashMap<Integer, UserData> userData = userService.getUserData();
        assertEquals(1, userData.size());
        assertTrue(userData.containsValue(new UserData("username", BCrypt.hashpw("password", BCrypt.gensalt()), "email")));
    }

    @Test
    void registerBadUser()
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
        assertTrue(userData.containsValue(new UserData("username", BCrypt.hashpw("password", BCrypt.gensalt()), "email")));
        assertTrue(userData.containsValue(new UserData("username2", BCrypt.hashpw("password", BCrypt.gensalt()), "email")));
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
    void logoutUser() throws ResponseException
    {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        userService.registerUser(request);

        LoginResult result = userService.loginUser(new LoginRequest("username", "password"));
        userService.logoutUser(new LogoutRequest(result.authToken()));
        assertThrows(ResponseException.class, () -> userService.logoutUser(new LogoutRequest(result.authToken()))); // double logout
        userService.loginUser(new LoginRequest("username", "password"));
        assertThrows(ResponseException.class, () -> userService.logoutUser(new LogoutRequest("invalid token")));
    }

    @Test
    void clearUsers() throws ResponseException
    {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        userService.registerUser(request);

        userService.deleteUserData();
        assertEquals(0, userService.getUserData().size()); // should have nothing inside it
    }

    @Test
    void dataCounts() throws ResponseException
    {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        RegisterRequest request2 = new RegisterRequest("username2", "password", "email");
        userService.registerUser(request);
        userService.registerUser(request2);
        LoginRequest login = new LoginRequest(request.username(), request.password());
        userService.loginUser(login);
        assertEquals(1, userService.getAuthData().size());
        assertEquals(2, userService.getUserData().size());
        userService.loginUser(login);
        assertEquals(2, userService.getUserData().size());
        userService.loginUser(new LoginRequest(request2.username(), request2.password()));
        assertEquals(3, userService.getAuthData().size());
        assertEquals(2, userService.getUserData().size());

        assertEquals(3, userService.getAuthTokens().size());
    }
}
