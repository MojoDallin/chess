package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int Row;
    private final int Column;
    private ChessPiece CurrentPiece = null;

    public ChessPosition(int row, int col)
    {
        Row = row;
        Column = col;
    }

    public ChessPosition(int row, int col, ChessPiece piece)
    {
        Row = row - 1; // - 1 to account for non-zero indexing
        Column = col - 1;
        CurrentPiece = piece;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow()
    {
        return Row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn()
    {
        return Column;
    }

    /**
     * Gets the current chess piece on the position, if there is any.
     * @return The ChessPiece object that is currently on the object; if there is no piece, then returns null.
     */
    public ChessPiece getCurrentPiece() { return CurrentPiece; }

    /**
     * Adds a chess piece to the position. Does NOT check if piece is currently occupied.
     * @param newPiece The piece to add.
     */
    public void addPiece(ChessPiece newPiece) { CurrentPiece = newPiece; }

    /**
     * Removes the current chess piece from the position.
     */
    public void removePiece() { CurrentPiece = null; }

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
        return Row == that.Row && Column == that.Column && Objects.equals(CurrentPiece, that.CurrentPiece);
    }

    /**
     * Generates a hashcode consisting of Row, Column, and CurrentPiece.
     * @return The generated hashcode.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(Row, Column, CurrentPiece);
    }
}
