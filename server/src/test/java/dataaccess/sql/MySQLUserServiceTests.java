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
    final UserService USER_SERVICE = new UserService(new MySqlDataAccess());

    MySQLUserServiceTests() throws ResponseException {}

    @BeforeEach
    void clear() throws ResponseException { USER_SERVICE.deleteUserData(); }

    @Test
    void registerUser() throws ResponseException
    {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        USER_SERVICE.registerUser(request);

        HashMap<Integer, UserData> userData = USER_SERVICE.getUserData();
        assertEquals(1, userData.size());
        assertTrue(userData.containsValue(new UserData("username", BCrypt.hashpw("password", BCrypt.gensalt()), "email")));
    }

    @Test
    void registerBadUser()
    {
        RegisterRequest request = new RegisterRequest(null, "password", "email");
        RegisterRequest request2 = new RegisterRequest("username", null, "email");
        RegisterRequest request3 = new RegisterRequest("username", "password", null);

        assertThrows(ResponseException.class, () -> USER_SERVICE.registerUser(request)); // bad username
        assertThrows(ResponseException.class, () -> USER_SERVICE.registerUser(request2)); // bad password
        assertThrows(ResponseException.class, () -> USER_SERVICE.registerUser(request3)); // bad email
    }

    @Test
    void multipleRegisters() throws ResponseException
    {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        RegisterRequest request2 = new RegisterRequest("username2", "password", "email");
        RegisterRequest request3 = new RegisterRequest("username", "difPassword", "difEmail");
        USER_SERVICE.registerUser(request);
        USER_SERVICE.registerUser(request2);

        HashMap<Integer, UserData> userData = USER_SERVICE.getUserData();
        assertEquals(2, userData.size());
        assertTrue(userData.containsValue(new UserData("username", BCrypt.hashpw("password", BCrypt.gensalt()), "email")));
        assertTrue(userData.containsValue(new UserData("username2", BCrypt.hashpw("password", BCrypt.gensalt()), "email")));
        assertThrows(ResponseException.class, () -> USER_SERVICE.registerUser(request3)); // duplicate username
    }

    @Test
    void loginUser() throws ResponseException
    {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        USER_SERVICE.registerUser(request);

        assertThrows(ResponseException.class, () -> USER_SERVICE.loginUser(new LoginRequest("username", "password_wrong")));
        assertThrows(ResponseException.class, () -> USER_SERVICE.loginUser(new LoginRequest("username_wrong", "password")));
        USER_SERVICE.loginUser(new LoginRequest("username", "password"));
        USER_SERVICE.loginUser(new LoginRequest("username", "password")); // login twice
    }

    @Test
    void logoutUser() throws ResponseException
    {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        USER_SERVICE.registerUser(request);

        LoginResult result = USER_SERVICE.loginUser(new LoginRequest("username", "password"));
        USER_SERVICE.logoutUser(new LogoutRequest(result.authToken()));
        assertThrows(ResponseException.class, () -> USER_SERVICE.logoutUser(new LogoutRequest(result.authToken()))); // double logout
        USER_SERVICE.loginUser(new LoginRequest("username", "password"));
        assertThrows(ResponseException.class, () -> USER_SERVICE.logoutUser(new LogoutRequest("invalid token")));
    }

    @Test
    void clearUsers() throws ResponseException
    {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        USER_SERVICE.registerUser(request);

        USER_SERVICE.deleteUserData();
        assertEquals(0, USER_SERVICE.getUserData().size()); // should have nothing inside it
    }

    @Test
    void dataCounts() throws ResponseException
    {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        RegisterRequest request2 = new RegisterRequest("username2", "password", "email");
        USER_SERVICE.registerUser(request);
        USER_SERVICE.registerUser(request2);
        LoginRequest login = new LoginRequest(request.username(), request.password());
        USER_SERVICE.loginUser(login);
        assertEquals(1, USER_SERVICE.getAuthData().size());
        assertEquals(2, USER_SERVICE.getUserData().size());
        USER_SERVICE.loginUser(login);
        assertEquals(2, USER_SERVICE.getUserData().size());
        USER_SERVICE.loginUser(new LoginRequest(request2.username(), request2.password()));
        assertEquals(3, USER_SERVICE.getAuthData().size());
        assertEquals(2, USER_SERVICE.getUserData().size());

        assertEquals(3, USER_SERVICE.getAuthTokens().size());
    }
}
