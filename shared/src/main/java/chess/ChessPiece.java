package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor color;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch(this.type) {
            case KING -> kingMoves(board, myPosition);
            case QUEEN -> queenMoves(board, myPosition);
            case BISHOP -> bishopMoves(board, myPosition);
            case KNIGHT -> knightMoves(board, myPosition);
            case ROOK -> rookMoves(board, myPosition);
            case PAWN -> pawnMoves(board, myPosition);
        };
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = this.orthogonalMoves(board, myPosition);
        moves.addAll(this.diagonalMoves(board, myPosition));
        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        return this.diagonalMoves(board, myPosition);
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        return this.orthogonalMoves(board, myPosition);
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    private Collection<ChessMove> orthogonalMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // Right
        for (int i = 1; i <= 8 - col; i++) {
            ChessPosition newPosition = new ChessPosition(row, col + i);
            if (this.isBlocked(board, newPosition)) {
                break;
            }
            moves.add(new ChessMove(myPosition, newPosition, null));
            if (this.canCapture(board, newPosition)) {
                break;
            }
        }
        // Left
        for (int i = 1; i <= col - 1; i++) {
            ChessPosition newPosition = new ChessPosition(row, col - i);
            if (this.isBlocked(board, newPosition)) {
                break;
            }
            moves.add(new ChessMove(myPosition, newPosition, null));
            if (this.canCapture(board, newPosition)) {
                break;
            }
        }
        // Up
        for (int i = 1; i <= 8 - row; i++) {
            ChessPosition newPosition = new ChessPosition(row + i, col);
            if (this.isBlocked(board, newPosition)) {
                break;
            }
            moves.add(new ChessMove(myPosition, newPosition, null));
            if (this.canCapture(board, newPosition)) {
                break;
            }
        }
        // Down
        for (int i = 1; i <= row - 1; i++) {
            ChessPosition newPosition = new ChessPosition(row - i, col);
            if (this.isBlocked(board, newPosition)) {
                break;
            }
            moves.add(new ChessMove(myPosition, newPosition, null));
            if (this.canCapture(board, newPosition)) {
                break;
            }
        }

        return moves;
    }

    // TODO: repeat code less
    private Collection<ChessMove> diagonalMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // Top right
        for (int i = 1; i <= Math.min(8 - row, 8 - col); i++) {
            ChessPosition newPosition = new ChessPosition(row + i, col + i);
            if (this.isBlocked(board, newPosition)) {
                break;
            }
            moves.add(new ChessMove(myPosition, newPosition, null));
            if (this.canCapture(board, newPosition)) {
                break;
            }
        }
        // Bottom right
        for (int i = 1; i <= Math.min(row - 1, 8 - col); i++) {
            ChessPosition newPosition = new ChessPosition(row - i, col + i);
            if (this.isBlocked(board, newPosition)) {
                break;
            }
            moves.add(new ChessMove(myPosition, newPosition, null));
            if (this.canCapture(board, newPosition)) {
                break;
            }
        }
        // Top left
        for (int i = 1; i <= Math.min(8 - row, col - 1); i++) {
            ChessPosition newPosition = new ChessPosition(row + i, col - i);
            if (this.isBlocked(board, newPosition)) {
                break;
            }
            moves.add(new ChessMove(myPosition, newPosition, null));
            if (this.canCapture(board, newPosition)) {
                break;
            }
        }
        // Bottom left
        for (int i = 1; i <= Math.min(row - 1, col - 1); i++) {
            ChessPosition newPosition = new ChessPosition(row - i, col - i);
            if (this.isBlocked(board, newPosition)) {
                break;
            }
            moves.add(new ChessMove(myPosition, newPosition, null));
            if (this.canCapture(board, newPosition)) {
                break;
            }
        }

        return moves;
    }

    private boolean isBlocked(ChessBoard board, ChessPosition newPosition) {
        ChessPiece other = board.getPiece(newPosition);
        if (other == null) {
            return false;
        }
        return other.getTeamColor() == this.getTeamColor();
    }

    // Only implemented for non-pawn pieces, doesn't check if king will be in check
    private boolean canCapture(ChessBoard board, ChessPosition newPosition) {
        ChessPiece other = board.getPiece(newPosition);
        if (other == null) {
            return false;
        }
        return other.getTeamColor() != this.getTeamColor();
    }

    @Override
    public String toString() {
        return switch (this.type) {
            case KING -> "K";
            case QUEEN -> "Q";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case ROOK -> "R";
            // Pawn has no abbreviation unless it's capturing, but that logic needs to be implemented
            // somewhere else instead anyway.
            case PAWN -> "P";
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }
}
