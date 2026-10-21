package service;

import dataaccess.MemoryDataAccess;
import exceptions.ResponseException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameServiceTests
{
    static final GameService gameService = new GameService(new MemoryDataAccess());

    @BeforeEach
    void clear() { gameService.deleteGameData(); }

    @Test
    void createGames() throws ResponseException
    {
        gameService.createGame(new GameData(0, "a", "b", "name", null));
    }
}
