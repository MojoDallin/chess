package client;

import chess.*;
import exceptions.ResponseException;
import server.ServerFacade;

public class ClientMain {
    public static void main(String[] args)
    {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        try
        {
            int port = 8080;
            new ChessClient(new ServerFacade(port)).signedOutMenu();
        } catch (ResponseException ex)
        {
            System.out.println("Unable to start server: " + ex.getMessage());
        }
    }
}
