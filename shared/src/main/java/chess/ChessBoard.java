package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPosition[] BoardPositions = new ChessPosition[64];

    public ChessBoard()
    {
        for(int i = 0; i < 64; i++) // 8 rows x 8 columns = 64 total positions
            BoardPositions[i] = new ChessPosition(i/8 + 1, i%8 + 1); // i%8 gets remainder, i/8 gets floored value; + 1 to account for non-zero indexing
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece)
    {
        getPositionAt(position.getRow(), position.getColumn()).addPiece(piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) { return getPositionAt(position.getRow(), position.getColumn()).getCurrentPiece(); }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // clear
        for(ChessPosition position : BoardPositions)
            position.removePiece();
        for(int i = 0; i < 2; i++)
        {
            int offset = (i * 56); // 56 is offset of black from white; j will only be 0 OR 1
            ChessGame.TeamColor color = i == 0 ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

            ChessPiece rook = new ChessPiece(color, ChessPiece.PieceType.ROOK); // these pieces are used twice, so init them first
            ChessPiece knight = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
            ChessPiece bishop = new ChessPiece(color, ChessPiece.PieceType.BISHOP);

            BoardPositions[offset].addPiece(rook);
            BoardPositions[offset + 1].addPiece(knight);
            BoardPositions[offset + 2].addPiece(bishop);
            BoardPositions[offset + 3].addPiece(new ChessPiece(color, ChessPiece.PieceType.QUEEN)); // only 1 queen and king
            BoardPositions[offset + 4].addPiece(new ChessPiece(color, ChessPiece.PieceType.KING));
            BoardPositions[offset + 5].addPiece(bishop);
            BoardPositions[offset + 6].addPiece(knight);
            BoardPositions[offset + 7].addPiece(rook);

            ChessPiece pawn = new ChessPiece(color, ChessPiece.PieceType.PAWN); // create pawn only once per color
            int leftmostPawn = offset + (color == ChessGame.TeamColor.WHITE ? 8 : -8); // + or - 8 because pawns are closer to center
            for(int j = leftmostPawn; j < leftmostPawn + 8; j++) // 8 total pawns
            {
                BoardPositions[j].addPiece(pawn);
            }

        }
    }

    /**
     * Gets a ChessPosition based on row and column numbers. Zero indexed.
     * @param row The row of the position.
     * @param col The column of the position.
     * @return The position at row and column.
     */
    public ChessPosition getPositionAt(int row, int col) // is zero indexed
    {
        row--;
        col--; // to account for non-zero indexing
        int index = (row * 8) + col; // multiply row by 8 because there are 8 columns per row (ex. row 1 col 4: (8 * 1) + 4 = 12)
        return BoardPositions[index];
    }

    /**
     * Gets all the positions in the chess board.
     * @return An array of positions;
     */
    public ChessPosition[] getBoardPositions() { return BoardPositions; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(BoardPositions, that.BoardPositions);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(BoardPositions);
    }
}
