package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece)
    {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() { return startPosition; }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() { return endPosition; }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() { return promotionPiece; }

    /**
     * Compares the chess move against another object to determine if they are equal.
     * @param o The reference object with which to compare.
     * @return True if the objects are the same, False if the objects are different.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        boolean positionsEqual = Objects.equals(startPosition, chessMove.startPosition) && Objects.equals(endPosition, chessMove.endPosition);
        return positionsEqual && promotionPiece == chessMove.promotionPiece;
    }

    /**
     * Generates a hashcode consisting of StartPosition, EndPosition, and Promotion Piece.
     * @return The generated hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }

    /**
     * Gives a human-readable format of the move.
     * @return A string consisting of [startPos, endPos].
     */
    @Override
    public String toString() {
        return String.format("%s%s", startPosition, endPosition);
    }
}
