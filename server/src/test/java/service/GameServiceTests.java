package service;

import dataaccess.MemoryDataAccess;
import exceptions.ResponseException;
import model.GameData;
import model.JoinData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.JoinRequest;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTests
{
    static final GameService GAME_SERVICE = new GameService(new MemoryDataAccess());

    @BeforeEach
    void clear() { GAME_SERVICE.deleteGameData(); }

    @Test
    void createGames() throws ResponseException
    {
        GAME_SERVICE.createGame(new GameData(0, "a", "b", "name", null));
    }

    @Test
    void joinGame() throws ResponseException
    {
        GAME_SERVICE.createGame(new GameData(0, null, null, "name", null));

        GAME_SERVICE.joinGame(new JoinRequest(new JoinData("WHITE", 1), "user"));
        assertThrows(ResponseException.class, () -> GAME_SERVICE.joinGame(new JoinRequest(new JoinData("PURPLE", 1), "user4")));
        GAME_SERVICE.joinGame(new JoinRequest(new JoinData("BLACK", 1), "user2"));
        assertThrows(ResponseException.class, () -> GAME_SERVICE.joinGame(new JoinRequest(new JoinData("WHITE", 1), "user3")));
    }
}
