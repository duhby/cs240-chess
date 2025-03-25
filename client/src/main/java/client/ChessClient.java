package client;

import exception.ResponseException;
import server.ServerFacade;
import record.*;

import java.util.Arrays;

public class ChessClient {
    public enum State {
        SIGNED_IN,
        SIGNED_OUT,
    }

    private final ServerFacade server;
    private String authToken = null;
    public State state = State.SIGNED_OUT;

    public ChessClient(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
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
                        case "logout" -> this.logout();
                        case "quit" -> "quit";
                        default -> this.help();
                    };
                }
                // Satisfies the compiler because all objects can be null :/
                default -> throw new ResponseException(500, "Unknown error");
            }
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length != 3) {
            return this.help();
        }
        RegisterResult result = this.server.register(new RegisterRequest(params[0], params[1], params[2]));
        this.state = State.SIGNED_IN;
        this.authToken = result.authToken();
        return "Registered as " + result.username();
    }

    public String login(String... params) throws ResponseException {
        if (params.length != 2) {
            return this.help();
        }
        LoginResult result = this.server.login(new LoginRequest(params[0], params[1]));
        this.state = State.SIGNED_IN;
        this.authToken = result.authToken();
        return "Logged in as " + result.username();
    }

    public String logout() throws ResponseException {
        this.server.logout(this.authToken);
        this.state = State.SIGNED_OUT;
        this.authToken = null;
        return "Logged out";
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
