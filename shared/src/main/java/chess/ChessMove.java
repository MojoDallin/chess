package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition StartPosition;
    private final ChessPosition EndPosition;
    private final ChessPiece.PieceType PromotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece)
    {
        StartPosition = startPosition;
        EndPosition = endPosition;
        PromotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() { return StartPosition; }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() { return EndPosition; }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() { return PromotionPiece; }

    /**
     * Compares the chess move against another object to determine if they are equal.
     * @param o The reference object with which to compare.
     * @return True if the objects are the same, False if the objects are different.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(StartPosition, chessMove.StartPosition) && Objects.equals(EndPosition, chessMove.EndPosition) && PromotionPiece == chessMove.PromotionPiece;
    }

    /**
     * Generates a hashcode consisting of StartPosition, EndPosition, and Promotion Piece.
     * @return The generated hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(StartPosition, EndPosition, PromotionPiece);
    }
}
