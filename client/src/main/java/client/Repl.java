package client;

import client.websocket.NotificationHandler;
import model.GameData;
import ui.ChessGame;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;

import java.util.Scanner;

public class Repl implements NotificationHandler {
    private final ChessClient client;

    public Repl (String serverUrl) {
        this.client = new ChessClient(serverUrl, this);
    }

    public void run() {
        System.out.println("\uD83D\uDC36 Welcome to cli chess. Log in or register to start.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        printPrompt();
        while (!result.equals("quit")) {
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
                if (result.isEmpty()) {
                    continue;
                }
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
            printPrompt();
        }
        System.out.println();
    }

    private void printPrompt() {
        String bracketText = "LOGGED_OUT";
        if (this.client.state().equals("LOGGED_IN")) {
            bracketText = "LOGGED_IN: " + this.client.username;
        }
        if (this.client.state().equals("IN_GAME")) {
            String teamColor;
            if (this.client.gameData.whiteUsername().equals(this.client.username)) {
                teamColor = "WHITE";
            } else if (this.client.gameData.blackUsername().equals(this.client.username)) {
                teamColor = "BLACK";
            } else {
                teamColor = "OBSERVING";
            }
            bracketText = "GAME: " + teamColor;
        }
        System.out.print("\n" + RESET_BG_COLOR + RESET_TEXT_COLOR + "[" + bracketText + "] " + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    public void notify(ServerMessage message) {
        System.out.print(RESET_BG_COLOR);
        switch (message) {
            case Notification notification -> System.out.println(SET_TEXT_COLOR_WHITE + notification.getMessage());
            case Error error -> System.out.println(SET_TEXT_COLOR_RED + error.getErrorMessage());
            case LoadGame loadGame -> {
                GameData gameData = loadGame.getGameData();
                this.client.gameData = gameData;
                // Observers should view it from the white perspective
                System.out.println();
                System.out.print(ChessGame.getBoardDisplay(gameData.game().getBoard(), !this.client.username.equals(gameData.blackUsername()), null, null));
            }
            default -> System.out.println(SET_TEXT_COLOR_RED + "Unknown message received");
        }
        printPrompt();
    }

}
