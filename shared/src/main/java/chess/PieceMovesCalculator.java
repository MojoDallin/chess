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
        ChessPiece piece = board.getPositionAt(oldPosition.getRow(), oldPosition.getColumn()).getCurrentPiece();
        for(int i = 1; i > -2; i--)
        {
            for (int j = 1; j > -2; j--)
            {
                int newRow = oldPosition.getRow() - i;
                int newCol = oldPosition.getColumn() - j;
                if (newRow > 0 && newRow < 9 && newCol > 0 && newCol < 9) // both in bounds
                {
                    ChessPosition newPosition = board.getPositionAt(newRow, newCol);
                    int occupiedStatus = newPosition.isOccupied(piece);
                    if (!oldPosition.equals(newPosition) && occupiedStatus < 2) // do not add current square
                    {
                        centeredMoves.add(new ChessMove(oldPosition, newPosition, null));
                    }
                }
            }
        }
        return centeredMoves;
    }

    /**
     * Grabs all the horizontal and vertical spaces a piece can move to. Intended for use with Rook and Queen movement.
     * @param board The chess board.
     * @param oldPosition The 'old', or current position of the piece to calculate moves for.
     * @return A list of ChessMove(s) centered on the current piece.
     */
    public static HashSet<ChessMove> calculateRookMoves(ChessBoard board, ChessPosition oldPosition) // for rook and queen
    {
        HashSet<ChessMove> verticalHorizontalMoves = new HashSet<>();
        int currentCol = oldPosition.getColumn();
        int currentRow = oldPosition.getRow();

        for(int i = 0; i < 4; i++) // 0,1: vertical; 2,3: horizontal
        {
            ChessPosition nextPosition;
            int offset = i % 2 == 0 ? 1 : -1;
            if(i < 2)
            {
                nextPosition = board.getPositionAt(currentRow + offset, currentCol);
            }
            else
            {
                nextPosition = board.getPositionAt(currentRow, currentCol + offset);
            }
            while(nextPosition != null)
            {
                int isOccupied = nextPosition.isOccupied(board.getPositionAt(oldPosition.getRow(), oldPosition.getColumn()).getCurrentPiece());
                if (isOccupied < 2)
                {
                    verticalHorizontalMoves.add(new ChessMove(oldPosition, nextPosition, null));
                    if (isOccupied == 1)
                    {
                        break;
                    }
                }
                else
                {
                    break;
                }
                if(i < 2)
                {
                    nextPosition = board.getPositionAt(nextPosition.getRow() + offset, currentCol);
                }
                else
                {
                    nextPosition = board.getPositionAt(currentRow, nextPosition.getColumn() + offset);
                }
            }
        }

        return verticalHorizontalMoves;
    }

    /**
     * Grabs all the diagonal spaces a piece can move to. Intended for use with Bishop and Queen movement.
     * @param board The chess board.
     * @param oldPosition The 'old', or current position of the piece to calculate moves for.
     * @return A list of ChessMove(s) centered on the current piece.
     */
    public static HashSet<ChessMove> calculateBishopMoves(ChessBoard board, ChessPosition oldPosition)
    {
        HashSet<ChessMove> diagonalMoves = new HashSet<>();

        for(int i = 0; i < 4; i++)
        {
            int offsetRow = i < 2 ? 1 : -1;
            int offsetCol = i % 2 == 0 ? 1 : -1;
            ChessPosition nextPosition = board.getPositionAt(oldPosition.getRow() + offsetRow, oldPosition.getColumn() + offsetCol);
            while(nextPosition != null)
            {
                int isOccupied = nextPosition.isOccupied(board.getPositionAt(oldPosition.getRow(), oldPosition.getColumn()).getCurrentPiece());
                if(isOccupied < 2)
                {
                    diagonalMoves.add(new ChessMove(oldPosition, nextPosition, null));
                    if(isOccupied == 1)
                    {
                        break;
                    }
                }
                else
                {
                    break;
                }
                nextPosition = board.getPositionAt(nextPosition.getRow() + offsetRow, nextPosition.getColumn() + offsetCol);
            }
        }

        return diagonalMoves;
    }

    /**
     * Grabs all the moves a pawn can move to.
     * @param board The chess board.
     * @param oldPosition The 'old', or current position of the piece to calculate moves for.
     * @return A list of ChessMove(s) centered on the current piece.
     */
    public static HashSet<ChessMove> calculatePawnMoves(ChessBoard board, ChessPosition oldPosition)
    {
        HashSet<ChessMove> pawnMoves = new HashSet<>();
        int currentRow = oldPosition.getRow();
        int currentCol = oldPosition.getColumn();
        ChessPiece piece = board.getPositionAt(currentRow, currentCol).getCurrentPiece();
        int direction = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;
        boolean pawnHasMoved = direction == 1 ? currentRow != 2 : currentRow != 7;
        ChessPosition nextPosition = board.getPositionAt(currentRow + direction, currentCol);
        if(nextPosition.isOccupied(piece) == 0) // unoccupied by anything
        {
            pawnMoves.add(new ChessMove(oldPosition, new ChessPosition(currentRow + direction, currentCol), null));
        }
        if(!pawnHasMoved && pawnMoves.size() == 1) // add another movement if pawn has not moved AND square directly in front is not blocked
        {
            nextPosition = board.getPositionAt(nextPosition.getRow() + direction, nextPosition.getColumn());
            if(nextPosition.isOccupied(piece) == 0)
            {
                pawnMoves.add(new ChessMove(oldPosition, new ChessPosition(currentRow + (direction * 2), currentCol), null));
            }
        }
        // diagonal moves
        for(int i = -1; i < 2; i += 2)
        {
            ChessPosition diagonalPosition = board.getPositionAt(currentRow + direction, currentCol + i);
            if(diagonalPosition != null && diagonalPosition.isOccupied(piece) == 1) // occupied by enemy
            {
                pawnMoves.add(new ChessMove(oldPosition, diagonalPosition, null));
            }
        }
        // promotions
        if((direction == 1 && nextPosition.getRow() == 8) || (direction == -1 && nextPosition.getRow() == 1))
        {
            HashSet<ChessMove> promotionMoves = new HashSet<>();
            for(ChessMove move : pawnMoves)
            {
                for(ChessPiece.PieceType newPieceType : chess.ChessPiece.PieceType.values())
                {
                    if (newPieceType != ChessPiece.PieceType.KING && newPieceType != ChessPiece.PieceType.PAWN) // cant promote to either
                    {
                        promotionMoves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), newPieceType));
                    }
                }
            }
            return promotionMoves; // only return promotion moves
        }
        return pawnMoves;
    }

    /**
     * Grabs all the moves the knight can move to.
     * @param board The chess board.
     * @param oldPosition The 'old', or current position of the piece to calculate moves for.
     * @return A list of ChessMove(s) centered on the current piece.
     */
    public static HashSet<ChessMove> calculateKnightMoves(ChessBoard board, ChessPosition oldPosition)
    {
        HashSet<ChessMove> knightMoves = new HashSet<>();
        ChessPiece piece = board.getPositionAt(oldPosition.getRow(), oldPosition.getColumn()).getCurrentPiece();
        for(int i = -1; i < 2; i += 2) // vertical
        {
            for(int j = -1; j < 2; j += 2)
            {
                ChessPosition nextPosition = board.getPositionAt(oldPosition.getRow() + (i * 2), oldPosition.getColumn() + j);
                if(nextPosition != null && nextPosition.isOccupied(piece) < 2)
                {
                    knightMoves.add(new ChessMove(oldPosition, nextPosition, null));
                }
            }
        }
        for(int i = -1; i < 2; i += 2) // horizontal
        {
            for(int j = -1; j < 2; j += 2)
            {
                ChessPosition nextPosition = board.getPositionAt(oldPosition.getRow() + j, oldPosition.getColumn() + (i * 2));
                if(nextPosition != null && nextPosition.isOccupied(piece) < 2)
                {
                    knightMoves.add(new ChessMove(oldPosition, nextPosition, null));
                }
            }
        }
        return knightMoves;
    }

}
