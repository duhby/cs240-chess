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
        Collection<ChessMove> moves = this.orthogonalMoves(board, myPosition, 1);
        moves.addAll(this.diagonalMoves(board, myPosition, 1));
        return moves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = this.orthogonalMoves(board, myPosition, 0);
        moves.addAll(this.diagonalMoves(board, myPosition, 0));
        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        return this.diagonalMoves(board, myPosition, 0);
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int[][] diffs = {{1, 2}, {2, 1}, {-1, 2}, {-2, 1}, {1, -2}, {2, -1}, {-1, -2}, {-2, -1}};

        for (int[] diff : diffs) {
            int newRow = row + diff[0];
            int newCol = col + diff[1];
            try {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                if (this.isBlocked(board, newPosition)) {
                    continue;
                }
                moves.add(new ChessMove(myPosition, newPosition, null));
            } catch (RuntimeException e) {}
        }

        return moves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        return this.orthogonalMoves(board, myPosition, 0);
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int baseRow = 0;
        int lastRow = 0;
        int dir = 0;
        switch (this.getTeamColor()) {
            case ChessGame.TeamColor.WHITE:
                baseRow = 2;
                lastRow = 7;
                dir = 1;
                break;
            case ChessGame.TeamColor.BLACK:
                baseRow = 7;
                lastRow = 2;
                dir = -1;
                break;
        }

        int[][] diffs = {{dir, -1}, {dir, 0}, {dir, 1}};

        ArrayList<ChessPosition> positions = new ArrayList<>();
        for (int[] diff : diffs) {
            int newRow = row + diff[0];
            int newCol = col + diff[1];
            // Pawn promotion is mandatory (a pawn can't be on the back rank)
            ChessPosition newPosition;
            try {
                newPosition = new ChessPosition(newRow, newCol);
            } catch (RuntimeException e) {
                continue;
            }
            // Straight
            if (diff[1] == 0) {
                // isBlocked handles pawns correctly
                if (this.isBlocked(board, newPosition)) {
                    continue;
                }
                positions.add(newPosition);
                if (row == baseRow) {
                    newPosition = new ChessPosition(row + (2 * dir), col);
                    if (!this.isBlocked(board, newPosition)) {
                        positions.add(newPosition);
                    }
                }
            // Capture
            } else if (this.canCapture(board, newPosition)) {
                positions.add(newPosition);
            }
        }

        for (ChessPosition position : positions) {
            if (row == lastRow) {
                moves.add(new ChessMove(myPosition, position, PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, position, PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, position, PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, position, PieceType.ROOK));
            } else {
                moves.add(new ChessMove(myPosition, position, null));
            }
        }

        return moves;
    }

    // When limit is 0 it's considered as no limit
    // Limit is limit per direction.
    private Collection<ChessMove> orthogonalMoves(ChessBoard board, ChessPosition myPosition, int limit) {
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
            if (i == limit) {
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
            if (i == limit) {
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
            if (i == limit) {
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
            if (i == limit) {
                break;
            }
        }

        return moves;
    }

    // When limit is 0 it's considered as no limit
    // Limit is limit per direction
    private Collection<ChessMove> diagonalMoves(ChessBoard board, ChessPosition myPosition, int limit) {
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
            if (i == limit) {
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
            if (i == limit) {
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
            if (i == limit) {
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
            if (i == limit) {
                break;
            }
        }

        return moves;
    }

    // Only considers being blocked by other pieces
    private boolean isBlocked(ChessBoard board, ChessPosition newPosition) {
        ChessPiece other = board.getPiece(newPosition);
        if (other == null) {
            return false;
        }
        if (this.getPieceType() == PieceType.PAWN) {
            return true;
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
