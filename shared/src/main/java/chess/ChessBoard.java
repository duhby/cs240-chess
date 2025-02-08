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
    private ChessPiece[][] position = new ChessPiece[8][8];

    public ChessBoard() {}

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        this.position[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return this.position[position.getRow() - 1][position.getColumn() - 1];
    }

    public void makeMove(ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = this.getPiece(startPosition);
        this.position[startPosition.getRow() - 1][startPosition.getColumn() - 1] = null;
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
        if (promotionPiece != null) {
            piece = new ChessPiece(piece.getTeamColor(), promotionPiece);
        }
        this.addPiece(move.getEndPosition(), piece);
    }

    public ChessPosition findPiece(ChessPiece.PieceType type, ChessGame.TeamColor color) {
        ChessPosition currentPosition;
        ChessPiece currentPiece;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                currentPosition = new ChessPosition(i, j);
                currentPiece = this.getPiece(currentPosition);
                if (currentPiece == null) {
                    continue;
                }
                if (type == currentPiece.getPieceType() && color == currentPiece.getTeamColor()) {
                    return currentPosition;
                }
            }
        }
        return null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        this.position = new ChessPiece[8][8];

        int[] baseRows = {1, 8};
        int[] pawnRows = {2, 7};
        ChessPiece.PieceType[] order = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK,
        };

        for (int row : baseRows) {
            ChessGame.TeamColor color = row == 1 ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            for (int i = 0; i < 8; i++) {
                this.addPiece(new ChessPosition(row, i + 1), new ChessPiece(color, order[i]));
            }
        }
        for (int row : pawnRows) {
            ChessGame.TeamColor color = row == 2 ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            for (int i = 0; i < 8; i++) {
                this.addPiece(new ChessPosition(row, i + 1), new ChessPiece(color, ChessPiece.PieceType.PAWN));
            }
        }
    }

    public ChessBoard copy() {
        ChessBoard newBoard = new ChessBoard();
        ChessPosition currentPosition;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                currentPosition = new ChessPosition(i, j);
                newBoard.addPiece(currentPosition, this.getPiece(currentPosition));
            }
        }
        return newBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(position);
    }
}