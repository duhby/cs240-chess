package client;

import exception.ResponseException;
import model.GameData;
import model.UserData;
import server.ServerFacade;
import record.*;
import ui.ChessGame;

import java.util.Arrays;
import java.util.Objects;

public class ChessClient {
    private final ServerFacade server;
    private String authToken = null;
    private GameData gameData = null;
    public String username = null;

    public ChessClient(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (this.authToken == null) {
                return switch (cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "quit" -> "quit";
                    default -> this.help();
                };
            } else {
                return switch (cmd) {
                    case "logout" -> this.logout();
                    case "create" -> this.createGame(params);
                    case "list" -> this.listGames();
                    case "quit" -> "quit";
                    // join, observe
                    default -> this.help();
                };
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
        this.authToken = result.authToken();
        this.username = result.username();
        return "Registered as " + result.username();
    }

    public String login(String... params) throws ResponseException {
        if (params.length != 2) {
            return this.help();
        }
        LoginResult result = this.server.login(new LoginRequest(params[0], params[1]));
        this.authToken = result.authToken();
        this.username = result.username();
        return "Logged in as " + result.username();
    }

    public String logout() throws ResponseException {
        this.server.logout(this.authToken);
        this.authToken = null;
        this.username = null;
        return "Logged out";
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length != 1) {
            return this.help();
        }
        this.server.createGame(new CreateGameRequest(params[0]), this.authToken);
//        ListGamesResponse gameList = this.server.listGames(this.authToken);
//        GameData game = null;
//        for (GameData gameData : gameList.games()) {
//            if (result.gameID() == gameData.gameID()) {
//                game = gameData;
//            }
//        }
//        if (game == null) {
//            throw new ResponseException(500, "Unknown error");
//        }
//        this.gameData = game;
//        return ChessGame.getBoardDisplay(this.gameData.game().getBoard(), this.gameData.whiteUsername().equals(this.username));
        return "Created game \"" + params[0] + '"';
    }

    public String listGames() throws ResponseException {
        ListGamesResponse result = this.server.listGames(this.authToken);
        StringBuilder gameList = new StringBuilder();

        int i = 1;
        for (GameData gameData : result.games()) {
            String whiteUsername = Objects.requireNonNullElse(gameData.whiteUsername(), "-");
            String blackUsername = Objects.requireNonNullElse(gameData.blackUsername(), "-");
            gameList.append("[").append(i).append("] Game \"").append(gameData.gameName()).append("\" White: ").append(whiteUsername).append(" Black: ").append(blackUsername).append("\n");
        }
        if (gameList.isEmpty()) {
            return "No games. Use `create` to create one.";
        }
        return gameList.toString();
    }

    public String help() {
        if (this.authToken == null) {
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

    public String state() {
        if (this.authToken == null) {
            return "LOGGED_OUT";
        }
        return "LOGGED_IN";
    }
}
