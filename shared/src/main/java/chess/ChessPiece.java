package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor TeamColor;
    private final PieceType PieceType;
    private boolean HasMoved = false;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type)
    {
        TeamColor = pieceColor;
        PieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor()
    {
        return TeamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType()
    {
        return PieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition)
    {
        HashSet<ChessMove> possibleMoves = new HashSet<>(); // HashSet because we don't want duplicate positions, and they do not need to be ordered

        switch(PieceType)
        {
            case PAWN:
            {
                possibleMoves.addAll(PieceMovesCalculator.calculatePawnMoves(board, myPosition));
                break;
            }
            case ROOK:
            {
                possibleMoves.addAll(PieceMovesCalculator.calculateRookMoves(board, myPosition));
                break;
            }
            case KNIGHT:
            {
                possibleMoves.addAll(PieceMovesCalculator.calculateKnightMoves(board, myPosition));
                break;
            }
            case BISHOP:
            {
                possibleMoves.addAll(PieceMovesCalculator.calculateBishopMoves(board, myPosition));
                break;
            }
            case KING:
            {
                possibleMoves.addAll(PieceMovesCalculator.calculateKingMoves(board, myPosition));
                break;
            }
            case QUEEN:
            {
                possibleMoves.addAll(PieceMovesCalculator.calculateKingMoves(board, myPosition)); // king moves
                possibleMoves.addAll(PieceMovesCalculator.calculateRookMoves(board, myPosition)); // rook moves
                possibleMoves.addAll(PieceMovesCalculator.calculateBishopMoves(board, myPosition)); // bishop moves
                break;
            }
        }
        HasMoved = true;
        for(ChessMove move : possibleMoves)
            move.getEndPosition().addPiece(null);
        return possibleMoves;
    }

    /**
     * Gets the HasMoved field from ChessPiece.
     * @return The boolean HasMoved field, determining if the piece has moved (mainly for castling).
     */
    public boolean hasMoved() { return HasMoved; }

    /**
     * Compares the chess piece against another object to determine if they are equal.
     * @param o The reference object with which to compare.
     * @return True if the objects are the same, False if the objects are different.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return TeamColor == that.TeamColor && PieceType == that.PieceType;
    }

    /**
     * Generates a hashcode consisting of TeamColor and PieceType.
     * @return The generated hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(TeamColor, PieceType);
    }

    /**
     * Gets the string representation of the piece.
     * @return The piece, as a string. Lowercase means black, uppercase means white; each letter stands for the respective piece which starts with it, save for K and N: K represents King, N represents Knight.
     */
    @Override
    public String toString() {
        char returnChar = ' '; // empty if no piece
        switch (PieceType)
        {
            case ROOK -> returnChar = 'r';
            case KNIGHT -> returnChar = 'n';
            case BISHOP -> returnChar = 'b';
            case KING -> returnChar = 'k';
            case QUEEN -> returnChar = 'q';
            case PAWN -> returnChar = 'p';
        }
        return TeamColor == ChessGame.TeamColor.BLACK ? String.valueOf(returnChar) : String.valueOf(Character.toUpperCase(returnChar)); // convert to string to return
    }
}
