package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor turn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.setTeamTurn(TeamColor.WHITE);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = this.board.getPiece(startPosition);
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        if (piece == null) {
            return validMoves;
        }

        for (ChessMove move : piece.pieceMoves(this.board, startPosition)) {
            ChessBoard originalBoard = this.board.copy();
            this.board.makeMove(move);
            if (this.isInCheck(piece.getTeamColor())) {
                this.board = originalBoard;
                continue;
            }
            this.board = originalBoard;
            validMoves.add(move);
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = this.board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException();
        }
        if (piece.getTeamColor() != this.turn) {
            throw new InvalidMoveException();
        }
        if (!this.validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException();
        }
        this.board.makeMove(move);
        this.setTeamTurn(this.turn == TeamColor.BLACK ? TeamColor.WHITE : TeamColor.BLACK);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = this.board.findPiece(ChessPiece.PieceType.KING, teamColor);
        if (kingPosition == null) {
            throw new RuntimeException("King not found.");
        }

        // Test all opponent pieces
        ChessPosition currentPosition;
        ChessPiece currentPiece;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                currentPosition = new ChessPosition(i, j);
                currentPiece = this.board.getPiece(currentPosition);
                if (currentPiece == null) {
                    continue;
                }
                if (currentPiece.getTeamColor() == teamColor) {
                    continue;
                }
                for (ChessMove move : this.board.getPiece(currentPosition).pieceMoves(this.board, currentPosition)) {
                    if (move.getEndPosition().equals(kingPosition)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return this.isInCheck(teamColor) && this.cannotMove(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return this.cannotMove(teamColor) && !this.isInCheck(teamColor);
    }

    private boolean cannotMove(TeamColor teamColor) {
        ChessPosition currentPosition;
        ChessPiece currentPiece;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                currentPosition = new ChessPosition(i, j);
                currentPiece = this.board.getPiece(currentPosition);
                if (currentPiece == null) {
                    continue;
                }
                if (currentPiece.getTeamColor() != teamColor) {
                    continue;
                }
                if (!this.validMoves(currentPosition).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, turn);
    }
}
