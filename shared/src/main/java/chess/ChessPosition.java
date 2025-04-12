package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        if (row > 8 || row < 1 || col > 8 || col < 1) {
            throw new RuntimeException("Invalid position.");
        }
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return this.col;
    }

    @Override
    public String toString() {
        char col = (char)('a' + this.getColumn() - 1);
        return String.format("%c%d", col, this.getRow());
    }

    public static ChessPosition fromString(String s) {
        char file = s.charAt(0);
        char rank = s.charAt(1);
        int col = file - 'a' + 1;
        int row = Character.getNumericValue(rank);
        return new ChessPosition(row, col);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
