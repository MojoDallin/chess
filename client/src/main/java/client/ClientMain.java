package client;

import chess.*;

public class ClientMain {
    public static void main(String[] args)
    {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        try
        {
            new ChessClient(8080).signedOutMenu();
        } catch (Exception ex)
        {
            System.out.println("Unable to start server: " + ex.getMessage());
        }
    }
}
