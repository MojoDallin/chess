package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exceptions.ResponseException;
import io.javalin.http.Context;
import model.*;
import org.eclipse.jetty.server.QuietServletException;
import org.mindrot.jbcrypt.BCrypt;
import requests.*;
import results.LoginResult;

import java.sql.*;
import java.util.*;

import static java.sql.Types.NULL;

public class MySqlDataAccess implements DataAccess
{
    public MySqlDataAccess() throws ResponseException
    {
        setupDatabase();
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
        String hashedPassword = BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt());
        updateDatabase(statement, registerRequest.username(), hashedPassword, registerRequest.email());
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
        if(!BCrypt.checkpw(loginRequest.password(), data.password()))
        {
            throw new ResponseException(401, "Error: password is not correct");
        }
        String token = randomToken();
        String statement = "INSERT INTO authdata (authToken, username) VALUES (?, ?)";
        updateDatabase(statement, token, loginRequest.username());
        return new LoginResult(loginRequest.username(), token);
    }

    @Override
    public void logoutUser(LogoutRequest logoutRequest) throws ResponseException
    {
        if(isAuthorized(logoutRequest.authToken()))
        {
            String statement = "DELETE FROM authdata WHERE authToken=?";
            updateDatabase(statement, logoutRequest.authToken());
        }
    }

    @Override
    public int createNewGame(GameData data, String token) throws ResponseException
    {
        if(data.gameName() == null)
        {
            throw new ResponseException(400, "Error: bad request");
        }
        if(isAuthorized(token))
        {
            String statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, gameData) VALUES (?, ?, ?, ?)";
            int id = updateDatabase(statement, data.whiteUsername(), data.blackUsername(), data.gameName(), data.game());
            return id;
        }
        return -1;
    }

    @Override
    public HashMap<Integer, GameData> getAllGames(String token) throws ResponseException
    {
        if(isAuthorized(token))
        {
            HashMap<Integer, GameData> allGames = new HashMap<>();
            try (Connection connection = DatabaseManager.getConnection())
            {
                String statement = "SELECT * FROM games";
                try (PreparedStatement prepared = connection.prepareStatement(statement))
                {
                    try (ResultSet resultSet = prepared.executeQuery())
                    {
                        while (resultSet.next())
                        {
                            int id = resultSet.getInt("id");
                            String whiteUsername = resultSet.getString("whiteUsername");
                            String blackUsername = resultSet.getString("blackUsername");
                            String gameName = resultSet.getString("gameName");
                            ChessGame game = new Gson().fromJson(resultSet.getString("gameData"), ChessGame.class);
                            GameData data = new GameData(id, whiteUsername, blackUsername, gameName, game);
                            allGames.put(id, data);
                        }
                    }
                }
            } catch (Exception ex)
            {
                return null;
            }
            return allGames;
        }
        return null;
    }

    @Override
    public void joinGame(JoinRequest request, String token) throws ResponseException
    {
        String color = request.data().playerColor();
        if(color == null)
        {
            throw new ResponseException(400, "Error: bad request");
        }
        if(isAuthorized(token))
        {
            boolean validColor = color.contains("WHITE") || color.contains("BLACK");
            if (!validColor || request.data().gameID() < 1) {
                throw new ResponseException(400, "Error: bad request");
            }
            GameData gameData = findGameDataByID(request.data().gameID());
            if (gameData == null) {
                throw new ResponseException(500, "Error: game ID " + request.data().gameID() + " not found");
            }
            boolean whiteTaken = gameData.whiteUsername() != null;
            boolean blackTaken = gameData.blackUsername() != null;
            if ((whiteTaken && color.equals("WHITE")) || (blackTaken && color.equals("BLACK"))) {
                throw new ResponseException(403, "Error: already taken");
            }
            String username = findUsernameByAuthToken(token);
            gameData = gameData.addPlayer(color, username);
            String statement = "UPDATE games SET ";
            if(color.equals("BLACK"))
            {
                statement += "blackUsername = '" + username;
            }
            else
            {
                statement += "whiteUsername = '" + username;
            }
            statement += "' WHERE id=" + request.data().gameID();
            updateDatabase(statement);
        }
    }

    @Override
    public void clearUsers() throws ResponseException
    {
        updateDatabase("TRUNCATE users");
    }

    @Override
    public void clearGames() throws ResponseException
    {
        updateDatabase("TRUNCATE games");
    }

    @Override
    public void clearAuthData() throws ResponseException
    {
        updateDatabase("TRUNCATE authdata");
    }

    @Override
    public HashMap<Integer, UserData> getUsers()
    {
        HashMap<Integer, UserData> allUsers = new HashMap<>();
        try (Connection connection = DatabaseManager.getConnection())
        {
            String statement = "SELECT * FROM users";
            try (PreparedStatement prepared = connection.prepareStatement(statement))
            {
                try (ResultSet resultSet = prepared.executeQuery())
                {
                    while (resultSet.next())
                    {
                        int id = resultSet.getInt("id");
                        String username = resultSet.getString("username");
                        String password = resultSet.getString("password");
                        String email = resultSet.getString("email");
                        allUsers.put(id, new UserData(username, password, email));
                    }
                }
            }
        } catch (Exception ex)
        {
            return null;
        }
        return allUsers;
    }

    @Override
    public HashSet<AuthData> getAuthData()
    {
        HashSet<AuthData> allAuthData = new HashSet<>();
        try (Connection connection = DatabaseManager.getConnection())
        {
            String statement = "SELECT * FROM authdata";
            try (PreparedStatement prepared = connection.prepareStatement(statement))
            {
                try (ResultSet resultSet = prepared.executeQuery())
                {
                    while (resultSet.next())
                    {
                        int id = resultSet.getInt("id");
                        String authToken = resultSet.getString("authToken");
                        String username = resultSet.getString("username");
                        allAuthData.add(new AuthData(authToken, username));
                    }
                }
            }
        } catch (Exception ex)
        {
            return null;
        }
        return allAuthData;
    }

    @Override
    public HashSet<String> getAuthTokens()
    {
        HashSet<String> allAuthTokens = new HashSet<>();
        try (Connection connection = DatabaseManager.getConnection())
        {
            String statement = "SELECT authToken FROM authdata";
            try (PreparedStatement prepared = connection.prepareStatement(statement))
            {
                try (ResultSet resultSet = prepared.executeQuery())
                {
                    while (resultSet.next())
                    {
                        String token = resultSet.getString("authToken");
                        allAuthTokens.add(token);
                    }
                }
            }
        } catch (Exception ex)
        {
            return null;
        }
        return allAuthTokens;
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
            throw new ResponseException(500, "Error: unable to update database: " + statement + "; " + ex.getMessage());
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
                `id` int NOT NULL AUTO_INCREMENT,
                `authToken` varchar(256) NOT NULL,
                `username` varchar(256) NOT NULL,
                PRIMARY KEY (`id`),
                INDEX (`authToken`)
            )
            """
    };
    private final String[] setupGameTableStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
                `id` int NOT NULL AUTO_INCREMENT,
                `whiteUsername` varchar(256) NULL,
                `blackUsername` varchar(256) NULL,
                `gameName` varchar(256) NOT NULL,
                `gameData` TEXT DEFAULT NULL,
                PRIMARY KEY(`id`)
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
                String[][] statements = {setupUserTableStatements, setupAuthDataTableStatements, setupGameTableStatements};
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
            throw new ResponseException(500, "Error: Internal Server Error");
        }
        return null;
    }

    private GameData findGameDataByID(int id) throws ResponseException
    {
        try(Connection connection = DatabaseManager.getConnection())
        {
            String statement = "SELECT id, whiteUsername, blackUsername, gameName, gameData FROM games WHERE id=?";
            try(PreparedStatement prepared = connection.prepareStatement(statement))
            {
                prepared.setInt(1, id);
                try(ResultSet resultSet = prepared.executeQuery())
                {
                    if(resultSet.next())
                    {
                        String whiteUsername = resultSet.getString("whiteUsername");
                        String blackUsername = resultSet.getString("blackUsername");
                        String json = resultSet.getString("gameData");
                        ChessGame game = new Gson().fromJson(json, ChessGame.class);
                        return new GameData(resultSet.getInt("id"), whiteUsername, blackUsername, resultSet.getString("gameName"), game);
                    }
                }
            }
        } catch (Exception ex)
        {
            throw new ResponseException(500, "Error: Internal Server Error");
        }
        return null;
    }

    private String findUsernameByAuthToken(String token) throws ResponseException
    {
        try(Connection connection = DatabaseManager.getConnection())
        {
            String statement = "SELECT username FROM authdata WHERE authtoken=?";
            try(PreparedStatement prepared = connection.prepareStatement(statement))
            {
                prepared.setString(1, token);
                try(ResultSet resultSet = prepared.executeQuery())
                {
                    if(resultSet.next())
                    {
                        return resultSet.getString("username");
                    }
                }
            }
        } catch (Exception ex)
        {
            throw new ResponseException(500, "Error: Internal Server Error");
        }
        return null;
    }

    private boolean isAuthorized(String token) throws ResponseException
    {
        try(Connection connection = DatabaseManager.getConnection())
        {
            String statement = "SELECT authToken, username FROM authdata WHERE authToken=?";
            try(PreparedStatement prepared = connection.prepareStatement(statement))
            {
                prepared.setString(1, token);
                try (ResultSet resultSet = prepared.executeQuery())
                {
                    if (resultSet.next())
                    {
                        return true;
                    }
                }
            }
        } catch (Exception ex)
        {
            throw new ResponseException(500, "Error: Internal Server Error");
        }
        throw new ResponseException(401, "Error: unauthorized");
    }

    private int getLatestGameID() throws ResponseException {
        try(Connection connection = DatabaseManager.getConnection())
        {
            String statement = "SELECT LAST_INSERT_ID(`id`) FROM games";
            try(PreparedStatement prepared = connection.prepareStatement(statement))
            {
                try(ResultSet resultSet = prepared.executeQuery())
                {
                    if(resultSet.next())
                    {
                        return resultSet.getInt("id");
                    }
                }
            }
        } catch (Exception ex)
        {
            throw new ResponseException(500, "Error: Internal Server Error");
        }
        return -1;
    }

    private String randomToken() { return UUID.randomUUID().toString(); };
}
