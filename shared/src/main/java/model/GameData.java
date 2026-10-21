package model;

import chess.ChessGame;

import java.util.Objects;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game)
{
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameData gameData = (GameData) o;
        return gameID == gameData.gameID;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(gameID);
    }

    public GameData addPlayer(String color, String username)
    {
        if(color.equals("WHITE"))
        {
            return new GameData(gameID, username, blackUsername, gameName, game);
        }
        return new GameData(gameID, whiteUsername, username, gameName, game);
    }
}
