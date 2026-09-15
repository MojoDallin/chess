package chess;

import java.util.HashSet;

public class PieceMovesCalculator
{

    /**
     * Grabs the 8 closest positions around the piece, centered on itself. Intended for use with King and Queen movement.
     * @param board The chess board.
     * @param oldPosition The 'old', or current position of the piece to calculate moves for.
     * @return A list of ChessMove(s) centered on the current piece.
     */
    public static HashSet<ChessMove> calculateKingMoves(ChessBoard board, ChessPosition oldPosition)
    {
        HashSet<ChessMove> centeredMoves = new HashSet<>();

        for(int i = 1; i > -2; i--)
            for(int j = 1; j > -2; j--)
            {
                int newRow = oldPosition.getRow() - i;
                int newCol = oldPosition.getColumn() - j;
                if(newRow > 0 && newRow < 9 && newCol > 0 && newCol < 9) // both in bounds
                {
                    ChessPosition newPosition = board.getPositionAt(newRow, newCol);
                    int occupiedStatus = newPosition.isOccupied(board.getPositionAt(oldPosition.getRow(), oldPosition.getColumn()).getCurrentPiece());
                    if(!oldPosition.equals(newPosition) && occupiedStatus < 2) // do not add current square to possible moves and do not add pieces occupied by same team
                        centeredMoves.add(new ChessMove(oldPosition, newPosition, null));
                }
            }

        return centeredMoves;
    }
}
