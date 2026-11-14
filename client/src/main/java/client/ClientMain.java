package client;

import chess.*;
import exceptions.ResponseException;
import server.Server;
import server.ServerFacade;

public class ClientMain {
    public static void main(String[] args)
    {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        try
        {
            Server server = new Server();
            int port = server.run(0);
            new ChessClient(new ServerFacade(port)).signedOutMenu();
        } catch (ResponseException ex)
        {
            System.out.println("Unable to start server: " + ex.getMessage());
        }
    }
}
