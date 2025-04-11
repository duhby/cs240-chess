package client;

import static ui.EscapeSequences.*;

import java.util.Scanner;

public class Repl {
    private final ChessClient client;

    public Repl (String serverUrl) {
        this.client = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.println("\uD83D\uDC36 Welcome to cli chess. Log in or register to start.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
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
}
