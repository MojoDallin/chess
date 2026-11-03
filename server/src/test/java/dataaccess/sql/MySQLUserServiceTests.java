package dataaccess.sql;

import dataaccess.DataAccess;
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
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MySQLUserServiceTests
{
    DataAccess sqlAccess = new MySqlDataAccess();
    final UserService userService = new UserService(sqlAccess);

    MySQLUserServiceTests() throws ResponseException {}

    @BeforeEach
    void clear() throws ResponseException { userService.deleteUserData(); }

    @Test
    void registerUser() throws ResponseException
    {
        RegisterRequest request = new RegisterRequest("username", "password", "email");

        assertDoesNotThrow(() -> sqlAccess.registerUser(request));
    }

    @Test
    void listUsers() throws ResponseException
    {
        HashMap<Integer, UserData> expected = new HashMap<>();
        UserData data1 = new UserData("username", BCrypt.hashpw("password", BCrypt.gensalt()), "email");
        UserData data2 = new UserData("username2", BCrypt.hashpw("password2", BCrypt.gensalt()), "email2");
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        RegisterRequest request2 = new RegisterRequest("username2", "password2", "email2");
        userService.registerUser(request);
        userService.registerUser(request2);
        expected.put(1, data1);
        expected.put(2, data2);

        assertUserDataEqual(expected, userService.getUserData());
    }

    @Test
    void deleteUserData() throws ResponseException
    {
        UserData data1 = new UserData("username", BCrypt.hashpw("password", BCrypt.gensalt()), "email");
        UserData data2 = new UserData("username2", BCrypt.hashpw("password2", BCrypt.gensalt()), "email2");
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        RegisterRequest request2 = new RegisterRequest("username2", "password2", "email2");
        userService.registerUser(request);
        userService.registerUser(request2);
        userService.deleteUserData();

        assertEquals(0, userService.getUserData().size());
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

    private void assertUserDataEqual(HashMap<Integer, UserData> expected, HashMap<Integer, UserData> actual)
    {
        UserData[] expectedArr = expected.values().toArray(new UserData[0]);
        UserData[] actualArr = actual.values().toArray(new UserData[0]);
        for(int i = 0; i < actual.size(); i++)
        {
            assertEquals(expectedArr[i].username(), actualArr[i].username());
        }
    }
}
