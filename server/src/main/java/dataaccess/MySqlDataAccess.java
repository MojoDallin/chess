package dataaccess;

import exceptions.ResponseException;
import model.*;
import requests.*;
import results.LoginResult;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

import static java.sql.Types.NULL;

public class MySqlDataAccess implements DataAccess
{
    public MySqlDataAccess()
    {
        try
        {
            setupDatabase();
        } catch (ResponseException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LoginResult registerUser(RegisterRequest registerRequest) throws ResponseException
    {
        UserData data = findUserDataByUsername(registerRequest.username());
        if(data != null)
        {
            throw new ResponseException(403, "Error: already taken");
        }
        if(registerRequest.email() == null || registerRequest.username() == null || registerRequest.password() == null)
        {
            throw new ResponseException(400, "Error: bad request");
        }
        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        updateDatabase(statement, registerRequest.username(), registerRequest.password(), registerRequest.email());
        return new LoginResult(registerRequest.username(), randomToken());
    }

    @Override
    public LoginResult loginUser(LoginRequest loginRequest) throws ResponseException
    {
        if(loginRequest.username() == null || loginRequest.password() == null)
        {
            throw new ResponseException(400, "Error: bad request");
        }
        UserData data = findUserDataByUsername(loginRequest.username());
        if(data == null)
        {
            throw new ResponseException(401, "Error: username not found");
        }
        if(!Objects.equals(data.password(), loginRequest.password()))
        {
            throw new ResponseException(401, "Error: password is not correct");
        }
        String token = randomToken();
        String statement = "INSERT INTO authdata (token, username) VALUES (?, ?)";
        updateDatabase(statement, token, loginRequest.username());
        return new LoginResult(loginRequest.username(), randomToken());
    }

    @Override
    public int createNewGame(GameData data) throws ResponseException {
        return 0;
    }

    @Override
    public HashMap<Integer, GameData> getAllGames() {
        return null;
    }

    @Override
    public void joinGame(JoinRequest request) throws ResponseException {

    }

    @Override
    public void logoutUser(LogoutRequest logoutRequest) throws ResponseException {

    }

    @Override
    public void clearUsers()
    {
        try {
            updateDatabase("TRUNCATE users");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearGames() {

    }

    @Override
    public void clearAuthData() {

    }

    @Override
    public HashMap<Integer, UserData> getUsers() {
        return null;
    }

    @Override
    public HashSet<AuthData> getAuthData() {
        return null;
    }

    @Override
    public HashSet<String> getAuthTokens() {
        return null;
    }

    private int updateDatabase(String statement, Object...params) throws ResponseException
    {
        try(Connection connection = DatabaseManager.getConnection())
        {
            try(PreparedStatement prepared = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS))
            {
                for(int i = 0; i < params.length; i++)
                {
                    Object param = params[i];
                    switch (param)
                    {
                        case String s -> prepared.setString(i + 1, s);
                        case Integer in -> prepared.setInt(i + 1, in);
                        case null -> prepared.setNull(i + 1, NULL);
                        default -> { }
                    }
                }

                prepared.executeUpdate();
                ResultSet resultSet = prepared.getGeneratedKeys();
                if(resultSet.next())
                {
                    return resultSet.getInt(1);
                }
                return 0;
            }
        } catch (Exception ex)
        {
            throw new ResponseException(500, "Unable to update database: " + statement + "; " + ex.getMessage());
        }
    }

    private final String[] setupUserTableStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
                `id` int NOT NULL AUTO_INCREMENT,
                `username` varchar(256) NOT NULL,
                `password` varchar(256) NOT NULL,
                `email` varchar(256) NOT NULL,
                PRIMARY KEY (`id`),
                INDEX (`username`)
            )
            """
    };
    private final String[] setupAuthDataTableStatements = {
            """
            CREATE TABLE IF NOT EXISTS authdata (
                `token` varchar(256) NOT NULL,
                `username` varchar(256) NOT NULL,
                PRIMARY KEY (`token`),
                INDEX (`username`)
            )
            """
    };

    private void setupDatabase() throws ResponseException
    {
        try
        {
            DatabaseManager.createDatabase();
            try(Connection connection = DatabaseManager.getConnection())
            {
                String[][] statements = {setupUserTableStatements, setupAuthDataTableStatements};
                for(String[] setupStatements : statements)
                {
                    for(String statement : setupStatements)
                    {
                        try (PreparedStatement prepared = connection.prepareStatement(statement))
                        {
                            prepared.executeUpdate();
                        }
                    }
                }
            }
        } catch (Exception ex)
        {
            throw new ResponseException(500, "Unable to set up database: " + ex.getMessage());
        }
    }

    private UserData findUserDataByUsername(String username) throws ResponseException
    {
        try(Connection connection = DatabaseManager.getConnection())
        {
            String statement = "SELECT username, password, email FROM users WHERE username=?";
            try(PreparedStatement prepared = connection.prepareStatement(statement))
            {
                prepared.setString(1, username);
                try (ResultSet resultSet = prepared.executeQuery())
                {
                    if (resultSet.next())
                    {
                        return new UserData(resultSet.getString("username"), resultSet.getString("password"), resultSet.getString("email"));
                    }
                }
            }
        } catch (Exception ex)
        {
            throw new ResponseException(0, "");
        }
        return null;
    }

    private String randomToken() { return UUID.randomUUID().toString(); };
}
