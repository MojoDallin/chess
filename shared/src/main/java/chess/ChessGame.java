package chess;

import java.util.*;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame()
    {
        teamTurn = TeamColor.WHITE; // white always starts first
        board = new ChessBoard();
        board.resetBoard();
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
    public enum TeamColor
    {
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
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        if(startPosition == null)  { return possibleMoves; } // empty if no position
        ChessPiece piece = board.getPositionAt(startPosition.getRow(), startPosition.getColumn()).getCurrentPiece();
        if(piece == null)  { return possibleMoves;} // empty if no piece
        possibleMoves = piece.pieceMoves(board, startPosition);
        if(isInCheck(teamTurn))
        {
            if(piece.getPieceType() != ChessPiece.PieceType.KING)
            {
                Collection<ChessMove> newMoves = new ArrayList<>();
                Collection<ChessPosition> checkingPositions = getPieceCheckingPositions(teamTurn);
                for(ChessMove move : possibleMoves)
                {
                    if(checkingPositions.contains(move.getEndPosition()))
                    {
                        newMoves.add(move);
                    }
                }
                return newMoves;
            }
            else
            {
                for(ChessMove kingMove : possibleMoves)
                {
                    board.getPositionAt(kingMove.getEndPosition().getRow(), kingMove.getEndPosition().getColumn()).addPiece(piece);
                    board.getPositionAt(kingMove.getStartPosition().getRow(), kingMove.getStartPosition().getColumn()).removePiece();
                    if(isInCheck(teamTurn))
                    {
                        possibleMoves.remove(kingMove); // if the new move WOULD put the king in check, then cant move there
                    }
                    board.getPositionAt(kingMove.getStartPosition().getRow(), kingMove.getStartPosition().getColumn()).addPiece(piece);
                    board.getPositionAt(kingMove.getEndPosition().getRow(), kingMove.getEndPosition().getColumn()).removePiece();
                }
            }
        }

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
        if(oldPosition.getCurrentPiece() == null || oldPosition.getCurrentPiece().getTeamColor() != teamTurn) // if piece is null OR wrong piece color attempted to move
        {
            throw new InvalidMoveException();
        }
        Collection<ChessMove> possibleMoves = validMoves(move.getStartPosition());
        if(!possibleMoves.contains(move)) // move is not in possible moves
        {
            throw new InvalidMoveException(); // invalidmoveexception if possibleMoves doesnt contain move
        }
        ChessPosition newPosition = board.getPositionAt(move.getEndPosition().getRow(), move.getEndPosition().getColumn());
        if(move.getPromotionPiece() == null)
        {
            newPosition.addPiece(oldPosition.getCurrentPiece()); // "move" piece to end spot
        }
        else
        {
            ChessPiece newPiece = new ChessPiece(oldPosition.getCurrentPiece().getTeamColor(), move.getPromotionPiece());
            newPosition.addPiece(newPiece);
        }
        oldPosition.removePiece(); // remove piece as it moved
        setTeamTurn(teamTurn == TeamColor.BLACK ? TeamColor.WHITE : TeamColor.BLACK);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) { return !getPieceCheckingPositions(teamColor).isEmpty(); }

    /**
     * Checks for pieces which currently put the king in check.
     * @param teamColor The team color to check for a check.
     * @return A list of ChessPositions of pieces that currently put the king in check; an empty list of king is not in check.
     */
    private Collection<ChessPosition> getPieceCheckingPositions(TeamColor teamColor)
    {
        Collection<ChessPosition> checkingPositions = new ArrayList<>();
        ChessPosition kingPosition = getKingPosition(teamColor);
        if(kingPosition != null)
        {
            for (ChessPosition[] rows : board.getBoardPositions())
            {
                for (ChessPosition position : rows)
                {
                    if (position.getCurrentPiece() != null && position.getCurrentPiece().getTeamColor() != teamColor)
                    {
                        Collection<ChessMove> possibleMoves = position.getCurrentPiece().pieceMoves(board, position);
                        for (ChessMove move : possibleMoves)
                        {
                            ChessPosition end = move.getEndPosition();
                            if (end.getRow() == kingPosition.getRow() && end.getColumn() == kingPosition.getColumn())
                            {
                                checkingPositions.add(end);
                            }
                        }
                    }
                }
            }
        }
        return checkingPositions;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor)
    {
        return validMoves(getKingPosition(teamColor)).isEmpty();
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
    public ChessBoard getBoard() { return board; }

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
    public boolean equals(Object o)
    {
        if (o == null || getClass() != o.getClass())
        {
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
    public int hashCode() { return Objects.hash(teamTurn, board); }
}
