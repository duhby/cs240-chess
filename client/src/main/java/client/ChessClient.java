package client;

import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

public class ChessClient {
    public enum State {
        SIGNED_IN,
        SIGNED_OUT,
    }

    private final ServerFacade server;
    public State state;

    public ChessClient(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
        this.state = State.SIGNED_OUT;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            switch (this.state) {
                case State.SIGNED_OUT -> {
                    return switch (cmd) {
                        case "register" -> register(params);
                        case "login" -> login(params);
                        case "quit" -> "quit";
                        default -> this.help();
                    };
                }
                case State.SIGNED_IN -> {
                    return switch (cmd) {
                        case "quit" -> "quit";
                        default -> this.help();
                    };
                }
            }
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        String response = this.server.register()
    }

    public String login(String... params) throws ResponseException {

    }

    public String help() {
        if (this.state == State.SIGNED_OUT) {
            return """
                    - register <username> <password> <email>
                    - login <username> <password>
                    - quit
                    - help
                    """;
        }
        return """
                - create <name>
                - list
                - join <id> [white|black]
                - observe <id>
                - logout
                - quit
                - help
                """;
    }
}
