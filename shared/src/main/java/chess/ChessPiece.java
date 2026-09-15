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
                break;
            }
            case ROOK:
            {
                possibleMoves.addAll(getHorizontalAndVerticalMoves(board, myPosition));
                break;
            }
            case KNIGHT:
            {
                for(int i = -1; i < 2; i++) // vertical
                {
                    for(int j = -1; j < 2; j += 2)
                    {
                        ChessPosition newPosition = board.getPositionAt(myPosition.getRow() + (i * 2), myPosition.getColumn() + j);
                        if(newPosition != null && newPosition.getCurrentPiece() == null)
                            possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
                for(int i = -1; i < 2; i += 2) // horizontal
                {
                    for(int j = -1; j < 2; j += 2)
                    {
                        ChessPosition newPosition = board.getPositionAt(myPosition.getRow() + j, myPosition.getColumn() + (i * 2));
                        if(newPosition != null && newPosition.getCurrentPiece() == null)
                            possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
                break;
            }
            case BISHOP:
            {
                possibleMoves.addAll(getDiagonalMoves(board, myPosition));
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
                possibleMoves.addAll(getHorizontalAndVerticalMoves(board, myPosition)); // rook moves
                possibleMoves.addAll(getDiagonalMoves(board, myPosition)); // bishop moves
                break;
            }
        }

        return possibleMoves;
    }

    /**
     * Grabs all the horizontal and vertical spaces a piece can move to. Intended for use with Rook and Queen movement.
     * @param board The chess board.
     * @param oldPosition The 'old', or current position of the piece to calculate moves for.
     * @return A list of ChessMove(s) centered on the current piece.
     */
    private HashSet<ChessMove> getHorizontalAndVerticalMoves(ChessBoard board, ChessPosition oldPosition) // for rook and queen
    {
        HashSet<ChessMove> verticalHorizontalMoves = new HashSet<>();
        int currentCol = oldPosition.getColumn();
        int currentRow = oldPosition.getRow();

        for(int i = -1; i < 2; i += 2) // vertical
        {
            int nextPos = oldPosition.getRow() + i;
            ChessPosition newPosition = board.getPositionAt(nextPos, currentCol);
            while (newPosition != null) // next position is in bounds and not occupied
            {
                ChessPiece curPiece = newPosition.getCurrentPiece();
                ChessMove newMove = new ChessMove(oldPosition, newPosition, null);
                if(curPiece != null && curPiece.getTeamColor() != TeamColor) // occupied by enemy piece
                {
                    verticalHorizontalMoves.add(newMove);
                    break;
                }
                else if(curPiece != null) // occupied by team piece
                    break;
                verticalHorizontalMoves.add(newMove); // unoccupied
                nextPos += i;
                newPosition = board.getPositionAt(nextPos, currentCol);
            }
        }

        for(int i = -1; i < 2; i += 2) // horizontal
        {
            int nextPos = oldPosition.getColumn() + i;
            ChessPosition newPosition = board.getPositionAt(currentRow, nextPos);
            while(newPosition != null && newPosition.getCurrentPiece() == null)
            {
                ChessPiece curPiece = newPosition.getCurrentPiece();
                ChessMove newMove = new ChessMove(oldPosition, newPosition, null);
                if(curPiece != null && curPiece.getTeamColor() != TeamColor)
                {
                    verticalHorizontalMoves.add(new ChessMove(oldPosition, newPosition, null));
                    break;
                }
                else if(curPiece != null)
                    break;
                verticalHorizontalMoves.add(newMove);
                nextPos += i;
                newPosition = board.getPositionAt(currentRow, nextPos);
            }
        }

        return verticalHorizontalMoves;
    }

    private HashSet<ChessMove> getDiagonalMoves(ChessBoard board, ChessPosition oldPosition)
    {
        HashSet<ChessMove> diagonalMoves = new HashSet<>();

        for(int i = -1; i < 2; i += 2) // right
        {
            for(int j = -1; j < 2; j+= 2)
            {
                ChessPosition newPosition = board.getPositionAt(oldPosition.getRow() + j, oldPosition.getColumn() + i);
                while (newPosition != null && newPosition.getCurrentPiece() == null)
                {
                    diagonalMoves.add(new ChessMove(oldPosition, newPosition, null));
                    newPosition = board.getPositionAt(newPosition.getRow() + j, newPosition.getColumn() + i);
                }
            }
        }

        return diagonalMoves;
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
