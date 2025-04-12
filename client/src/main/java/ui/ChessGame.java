package ui;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessGame.TeamColor;

import java.util.ArrayList;
import java.util.Collection;

public class ChessGame {
    public static String getBoardDisplay(
            ChessBoard board,
            boolean isWhite,
            Collection<ChessPosition> highlightedSquares,
            ChessPosition selectedSquare)
    {
        StringBuilder display = new StringBuilder();
        if (highlightedSquares == null) {
            highlightedSquares = new ArrayList<>();
        }

        int startRow = isWhite ? 8 : 1;
        int endRow = isWhite ? 0 : 9;
        int startCol = isWhite ? 1 : 8;
        int endCol = isWhite ? 9 : 0;
        int rowIncrement = isWhite ? -1 : 1;
        int colIncrement = isWhite ? 1 : -1;

        display.append(EscapeSequences.SET_TEXT_BOLD);

        addColDisplay(display, isWhite);

        for (int row = startRow; row != endRow; row += rowIncrement) {
            addRowNumber(display, row);

            for (int col = startCol; col != endCol; col += colIncrement) {
                // square color
                boolean isLightSquare = (row + col) % 2 != 0;
                String bgColor = isLightSquare ?
                        EscapeSequences.SET_BG_COLOR_WHITE :
                        EscapeSequences.SET_BG_COLOR_BLACK;

                if (selectedSquare != null && selectedSquare.getColumn() == col && selectedSquare.getRow() == row) {
                    bgColor = EscapeSequences.SET_BG_COLOR_YELLOW;
                }
                for (ChessPosition position : highlightedSquares) {
                    if (position.getColumn() == col && position.getRow() == row) {
                        bgColor = isLightSquare ?
                                EscapeSequences.SET_BG_COLOR_GREEN :
                                EscapeSequences.SET_BG_COLOR_DARK_GREEN;
                        break;
                    }
                }

                display.append(bgColor);

                // piece
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null) {
                    if (piece.getTeamColor() == TeamColor.WHITE) {
                        display.append(EscapeSequences.SET_TEXT_COLOR_RED);
                    } else {
                        display.append(EscapeSequences.SET_TEXT_COLOR_BLUE);
                    }
                    display.append(" ").append(piece).append(" ");
                } else {
                    display.append(EscapeSequences.EMPTY);
                }

                resetFormatting(display);
            }

            addRowNumber(display, row);

            display.append("\n");
        }

        addColDisplay(display, isWhite);

        display.append(EscapeSequences.RESET_TEXT_BOLD_FAINT);

        return display.toString();
    }

    private static void addRowNumber(StringBuilder display, int row) {
        display.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        display.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        display.append(" ").append(row).append(" ");
        resetFormatting(display);
    }

    private static void addColDisplay(StringBuilder display, boolean isWhite) {
        display.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        display.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        if (isWhite) {
            display.append("    a  b  c  d  e  f  g  h    ");
        } else {
            display.append("    h  g  f  e  d  c  b  a    ");
        }
        display.append(EscapeSequences.RESET_TEXT_COLOR);
        display.append(EscapeSequences.RESET_BG_COLOR);
        display.append("\n");
    }

    private static void resetFormatting(StringBuilder display) {
        display.append(EscapeSequences.RESET_TEXT_COLOR);
        display.append(EscapeSequences.RESET_BG_COLOR);
    }
}
