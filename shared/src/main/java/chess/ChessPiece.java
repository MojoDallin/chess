package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
        List<ChessMove> possibleMoves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();

        switch(PieceType)
        {
            case PAWN:
            {
                int nextRow = TeamColor == ChessGame.TeamColor.WHITE ? currentRow - 1 : currentRow + 1; // -1 = white, +1 = black

                if(board.getPositionAt(nextRow, currentCol).getCurrentPiece() == null) // can move forward if space in front is not occupied
                {
                    possibleMoves.add(new ChessMove(myPosition, new ChessPosition(nextRow, currentCol), null));
                    if(!HasMoved) // if it's the first move, then add an extra space
                    {
                        int nextNextRow = TeamColor == ChessGame.TeamColor.WHITE ? nextRow + 1 : nextRow - 1;
                        if(board.getPositionAt(nextNextRow, currentCol).getCurrentPiece() == null)
                            possibleMoves.add(new ChessMove(myPosition, new ChessPosition(nextNextRow, currentCol), null));
                    }
                }
                for(int i = -1; i < 2; i += 2) // diagonal moves
                {
                    int diagonalCol = currentCol + i;
                    if(diagonalCol > 0 && diagonalCol < 9) // checks for bounds
                        if (board.getPositionAt(nextRow, diagonalCol).getCurrentPiece() != null) // can move diagonally if either (or both) spaces ARE occupied
                            possibleMoves.add(new ChessMove(myPosition, new ChessPosition(nextRow, currentCol + i), null));
                }
            }
            case ROOK:
            {
                for(int i = -1; i < 2; i += 2) // vertical
                {
                    int nextPos = currentRow + i;
                    ChessPosition newPosition = board.getPositionAt(nextPos, currentCol);
                    while (newPosition != null && newPosition.getCurrentPiece() == null) // next position is in bounds and not occupied
                    {
                        possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                        nextPos += i;
                        newPosition = board.getPositionAt(nextPos, currentCol);
                    }
                }
                for(int i = -1; i < 2; i += 2) // horizontal
                {
                    int nextPos = currentCol + i;
                    ChessPosition newPosition = board.getPositionAt(nextPos, currentCol);
                    while(newPosition != null && newPosition.getCurrentPiece() != null)
                    {
                        possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                        nextPos += i;
                        newPosition = board.getPositionAt(nextPos, currentCol);
                    }
                }
            }
        }
        return possibleMoves;
    }

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
