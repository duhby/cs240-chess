package client;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.NotificationHandler;
import client.websocket.WebsocketFacade;
import exception.ResponseException;
import model.GameData;
import server.ServerFacade;
import record.*;
import ui.ChessGame;

import java.util.*;

public class ChessClient {
    private final ServerFacade server;
    private String authToken = null;
    public GameData gameData = null;
    public String username = null;
    public boolean observing = false;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private WebsocketFacade websocketFacade;
    private final HashMap<Integer, Integer> gameIDs = new HashMap<>();

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        this.server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
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
            } else if (this.gameData == null) {
                return switch (cmd) {
                    case "logout" -> this.logout();
                    case "create" -> this.createGame(params);
                    case "list" -> this.listGames();
                    case "join" -> this.joinGame(params);
                    case "observe" -> this.spectate(params);
                    case "quit" -> "quit";
                    default -> this.help();
                };
            } else if (this.observing) {
                return switch (cmd) {
                    case "redraw" -> this.redraw();
                    case "leave" -> this.leaveObserving();
                    default -> this.help();
                };
            } else {
                // game mode
                String response = switch (cmd) {
                    case "redraw" -> this.redraw();
                    case "leave" -> this.leaveGame();
                    case "resign" -> this.resign();
                    default -> null;
                };
                if (response != null) {
                    return response;
                }
                return switch (cmd.length()) {
                    case 2 -> this.legalMoves(cmd);
                    case 4 -> this.makeMove(cmd);
                    case 6 -> this.makeMovePromotion(cmd);
                    default -> this.help();
                };
            }
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String resign() throws ResponseException {
        this.websocketFacade.resign(this.authToken, this.gameData.gameID());
        return "";
    }

    public String makeMove(String s) throws ResponseException {
        ChessMove move;
        try {
            move = new ChessMove(ChessPosition.fromString(s.substring(0, 2)), ChessPosition.fromString(s.substring(2, 4)), null);
        } catch (RuntimeException e) {
            return this.help();
        }
        this.websocketFacade.makeMove(this.authToken, this.gameData.gameID(), move);
        return "";
    }

    public String makeMovePromotion(String s) throws ResponseException {
        ChessMove move;
        try {
            if (s.charAt(4) != '=') {
                return this.help();
            }
            ChessPiece.PieceType promotionPiece = switch (s.charAt(5)) {
                case 'q' -> ChessPiece.PieceType.QUEEN;
                case 'b' -> ChessPiece.PieceType.BISHOP;
                case 'n' -> ChessPiece.PieceType.KNIGHT;
                case 'r' -> ChessPiece.PieceType.ROOK;
                default -> throw new RuntimeException("Invalid promotion piece: " + s.charAt(5));
            };
            move = new ChessMove(ChessPosition.fromString(s.substring(0, 2)), ChessPosition.fromString(s.substring(2, 4)), promotionPiece);
        } catch (RuntimeException e) {
            return this.help();
        }
        this.websocketFacade.makeMove(this.authToken, this.gameData.gameID(), move);
        return "";
    }

    public String legalMoves(String s) {
        ChessPosition position;
        try {
            position = ChessPosition.fromString(s);
        } catch (RuntimeException e) {
            return this.help();
        }
        ChessPiece piece = this.gameData.game().getBoard().getPiece(position);
        if (piece == null) {
            return "Empty square";
        }
        Collection<ChessMove> validMoves = this.gameData.game().validMoves(position);
        Collection<ChessPosition> validSquares = new ArrayList<>();
        for (ChessMove move : validMoves) {
            validSquares.add(move.getEndPosition());
        }
        return ChessGame.getBoardDisplay(gameData.game().getBoard(), !this.username.equals(gameData.blackUsername()), validSquares, position);
    }

    public String leaveObserving() throws ResponseException {
        this.websocketFacade.leaveGame(this.authToken, this.gameData.gameID());
        this.gameData = null;
        this.observing = false;
        return "You stopped observing the game.";
    }

    public String leaveGame() throws ResponseException {
        this.websocketFacade.leaveGame(this.authToken, this.gameData.gameID());
        this.gameData = null;
        return "You left the game.";
    }

    public String redraw() {
        // Observing will be white
        return ChessGame.getBoardDisplay(gameData.game().getBoard(), !this.username.equals(gameData.blackUsername()), null, null);
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
        return "Created game \"" + params[0] + '"';
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length != 2) {
            return this.help();
        }

        int gameID;
        try {
            gameID = this.gameIDs.get(Integer.parseInt(params[0]));
        } catch (Throwable e) {
            throw new ResponseException(400, "Invalid Game ID");
        }

        ListGamesResponse gameList = this.server.listGames(this.authToken);
        GameData joinedGame = null;
        for (GameData gameData : gameList.games()) {
            if (gameData.gameID() == gameID) {
                joinedGame = gameData;
                break;
            }
        }
        if (joinedGame == null) {
            throw new ResponseException(400, "Invalid Game ID");
        }
        String teamColor;
        if (params.length == 2) {
            teamColor = params[1];
            if (!teamColor.equals("white") && !teamColor.equals("black")) {
                return this.help();
            }
            teamColor = teamColor.toUpperCase();
        } else {
            if (joinedGame.whiteUsername() == null) {
                teamColor = "WHITE";
            } else if (joinedGame.blackUsername() == null) {
                teamColor = "BLACK";
            } else {
                throw ResponseException.alreadyTaken();
            }
        }

        JoinGameRequest req = new JoinGameRequest(teamColor, joinedGame.gameID());
        this.server.joinGame(req, this.authToken);

        gameList = this.server.listGames(this.authToken);
        for (GameData gameData : gameList.games()) {
            if (gameData.gameID() == gameID) {
                this.gameData = gameData;
                break;
            }
        }
        if (this.gameData == null) {
            throw new ResponseException(500, "Unknown error");
        }

        this.websocketFacade = new WebsocketFacade(this.serverUrl, this.notificationHandler);
        this.websocketFacade.joinGame(this.authToken, this.gameData.gameID());

        return "";
    }

    public String spectate(String... params) throws ResponseException {
        if (params.length != 1) {
            return this.help();
        }

        int gameID;
        try {
            gameID = this.gameIDs.get(Integer.parseInt(params[0]));
        } catch (Throwable e) {
            throw new ResponseException(400, "Invalid Game ID");
        }

        ListGamesResponse gameList = this.server.listGames(this.authToken);
        for (GameData gameData : gameList.games()) {
            if (gameData.gameID() == gameID) {
                this.gameData = gameData;
                break;
            }
        }
        if (this.gameData == null) {
            throw new ResponseException(400, "Invalid Game ID");
        }
        this.observing = true;

        this.websocketFacade = new WebsocketFacade(this.serverUrl, this.notificationHandler);
        this.websocketFacade.joinGame(this.authToken, this.gameData.gameID());

        return "";
    }

    public String listGames() throws ResponseException {
        ListGamesResponse result = this.server.listGames(this.authToken);
        StringBuilder gameList = new StringBuilder();

        int i = 1;
        for (GameData gameData : result.games()) {
            String whiteUsername = Objects.requireNonNullElse(gameData.whiteUsername(), "-");
            String blackUsername = Objects.requireNonNullElse(gameData.blackUsername(), "-");
            gameList.append("[").append(i).append("] Game \"").append(gameData.gameName());
            gameList.append("\" White: ").append(whiteUsername).append(" Black: ").append(blackUsername).append("\n");
            this.gameIDs.put(i, gameData.gameID());
            i++;
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
        } else if (this.gameData == null) {
            return """
                    - create <name>
                    - list
                    - join <id> <white|black>
                    - observe <id>
                    - logout
                    - quit
                    - help
                    """;
        } else if (this.observing) {
            return """
                    - redraw
                    - leave
                    - help
                    """;
        } else {
            return """
                    - redraw
                    - leave
                    - resign
                    - <move> (ex. e2e4, e7e8=Q)
                    - <position> (ex. e2)
                    - help
                    """;
        }
    }

    public String state() {
        if (gameData != null) {
            return "IN_GAME";
        }
        if (this.authToken == null) {
            return "LOGGED_OUT";
        }
        return "LOGGED_IN";
    }
}
