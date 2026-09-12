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

    public ChessPosition(int row, int col)
    {
        Row = row;
        Column = col;
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
        return Row == that.Row && Column == that.Column;
    }

    /**
     * Generates a hashcode consisting of Row and Column.
     * @return The generated hashcode.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(Row, Column);
    }
}
