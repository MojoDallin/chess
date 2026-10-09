package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        teamTurn = TeamColor.WHITE; // white always starts first
        board = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) { teamTurn = team; }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if(startPosition == null)
        {
            return null;
        }
        ChessPiece piece = startPosition.getCurrentPiece();
        if(piece == null)
        {
            return null;
        }
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition); // WIP


        return possibleMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition oldPosition = board.getPositionAt(move.getStartPosition().getRow(), move.getStartPosition().getColumn());
        if(oldPosition.getCurrentPiece() == null)
        {
            return;
        }
        Collection<ChessMove> possibleMoves = oldPosition.getCurrentPiece().pieceMoves(board, move.getStartPosition());
        if(!possibleMoves.contains(move))
        {
            throw new InvalidMoveException(); // invalidmoveexception if possibleMoves doesnt contain move
        }
        board.getPositionAt(move.getEndPosition().getRow(), move.getEndPosition().getColumn()).addPiece(oldPosition.getCurrentPiece()); // "move" piece to end spot
        oldPosition.removePiece(); // remove piece as it moved
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor)
    {
        return validMoves(getKingPosition(teamColor)).isEmpty() && !isInCheck(teamColor);
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) { this.board = board; }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    /**
     * Gets the chess position of a team color.
     * @param teamColor The king color to search for.
     * @return The chess position of the king. Returns null if the king couldn't be found.
     */
    private ChessPosition getKingPosition(TeamColor teamColor)
    {
        ChessPiece colorKing = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
        for(ChessPosition[] row : board.getBoardPositions())
        {
            for(ChessPosition position : row)
            {
                if (position.getCurrentPiece() != null && position.getCurrentPiece().equals(colorKing))
                {
                    return position;
                }
            }
        }
        return null;

    }

    /**
     * Compares the current chess game against another object.
     * @param o   the reference object with which to compare.
     * @return true if the objects are the same, false if they are different; if the other object is NOT a chess game, it always returns false.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    /**
     * Generates a hashcode consisting of teamTurn and the board.
     * @return The generated hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }
}
