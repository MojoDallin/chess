package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int column;
    private ChessPiece currentPiece = null;

    public ChessPosition(int row, int col)
    {
        this.row = row;
        column = col;
    }

    public ChessPosition(int row, int col, ChessPiece piece)
    {
        this.row = row - 1; // - 1 to account for non-zero indexing
        column = col - 1;
        currentPiece = piece;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow()
    {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn()
    {
        return column;
    }

    /**
     * Gets the current chess piece on the position, if there is any.
     * @return The ChessPiece object that is currently on the object; if there is no piece, then returns null.
     */
    public ChessPiece getCurrentPiece() { return currentPiece; }

    /**
     * Adds a chess piece to the position. Does NOT check if piece is currently occupied.
     * @param newPiece The piece to add.
     */
    public void addPiece(ChessPiece newPiece) { currentPiece = newPiece; }

    /**
     * Removes the current chess piece from the position.
     */
    public void removePiece() { currentPiece = null; }

    public int isOccupied(ChessPiece piece)
    {
        if(currentPiece == null)
        {
            return 0; // 0 if unoccupied
        }
        if(piece.getTeamColor() != currentPiece.getTeamColor())
        {
            return 1; // 1 if occupied by DIFFERENT team color
        }
        return 2; // 2 if occupied by SAME team color
    }

    /**
     * Compares the chess position against another object to determine if they are equal.
     * @param o The reference object with which to compare.
     * @return True if the objects are the same, False if the objects are different.
     */
    @Override
    public boolean equals(Object o)
    {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && column == that.column && Objects.equals(currentPiece, that.currentPiece);
    }

    /**
     * Generates a hashcode consisting of Row, column, and CurrentPiece.
     * @return The generated hashcode.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(row, column, currentPiece);
    }

    /**
     * Gives a human-readable format of the position.
     * @return A string consisting of [row, col].
     */
    @Override
    public String toString() { return String.format("[%d,%d]", row, column); }
}
