package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthAccess;
import dataaccess.GameAccess;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final AuthAccess authAccess;
    private final GameAccess gameAccess;

    public WebSocketHandler(AuthAccess authAccess, GameAccess gameAccess) {
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        ChessMove chessMove = null;
        if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            MakeMoveCommand moveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
            chessMove = moveCommand.getMove();
        }
        int gameID = command.getGameID();

        // auth
        AuthData authData;
        try {
            authData = this.authAccess.get(command.getAuthToken());
        } catch (ResponseException e) {
            sendError(session, e.getMessage());
            return;
        }
        String username = authData.username();

        GameData game;
        try {
            game = gameAccess.get(gameID);
        } catch (ResponseException e) {
            sendError(session, e.getMessage());
            return;
        }

        switch (command.getCommandType()) {
            case CONNECT -> this.connect(username, game, session);
            case MAKE_MOVE -> this.makeMove(username, game, chessMove, session);
            case LEAVE -> this.leave(username, game, session);
            case RESIGN -> this.resign(username, game, session);
        }
    }

    private void sendError(Session session, String message) throws IOException {
        Error error = new Error(message);
        session.getRemote().sendString(error.toString());
    }

    private void leave(String username, GameData game, Session session) throws IOException {
        try {
            gameAccess.removePlayer(game.gameID(), username);
        } catch (ResponseException e) {
            sendError(session, e.getMessage());
        }
        connections.remove(username);
        Notification notification = new Notification(String.format("%s left the game", username));
        connections.broadcast(username, game.gameID(), notification);
    }

    private void connect(String username, GameData game, Session session) throws IOException {
        connections.add(username, game.gameID(), session);
        LoadGame loadGame = new LoadGame(game);
        session.getRemote().sendString(loadGame.toString());

        String end;
        if (username.equals(game.whiteUsername())) {
            end = "white";
        } else if (username.equals(game.blackUsername())) {
            end = "black";
        } else {
            end = "an observer";
        }
        String message = String.format("%s connected to the game as %s", username, end);
        Notification notification = new Notification(message);
        connections.broadcast(username, game.gameID(), notification);
    }

    private void makeMove(String username, GameData game, ChessMove move, Session session) throws IOException {
        if (move == null) {
            this.sendError(session, "Missing chess move");
            return;
        }
        try {
            game.game().makeMove(move);
        } catch (InvalidMoveException e) {
            this.sendError(session, e.getMessage());
        }
        try {
            this.gameAccess.edit(game);
        } catch (ResponseException e) {
            this.sendError(session, e.getMessage());
        }
        connections.broadcast(null, game.gameID(), new LoadGame(game));

        Notification moveNotification = new Notification(String.format("%s made the move %s", username, move));
        connections.broadcast(username, game.gameID(), moveNotification);

        // Check etc. notifications
        ChessGame.TeamColor color = game.game().getBoard().getPiece(move.getStartPosition()).getTeamColor();
        ChessGame.TeamColor otherColor;
        String otherUsername;
        if (color == ChessGame.TeamColor.BLACK) {
            otherColor = ChessGame.TeamColor.WHITE;
            otherUsername = game.whiteUsername();
        } else {
            otherColor = ChessGame.TeamColor.BLACK;
            otherUsername = game.blackUsername();
        }
        String message = null;
        if (game.game().isInCheckmate(otherColor)) {
            message = "is in checkmate";
        } else if (game.game().isInStalemate(otherColor)) {
            message = "is in stalemate";
        } else if (game.game().isInCheck(otherColor)) {
            message = "is in check";
        }
        if (message != null) {
            Notification status = new Notification(String.format("%s %s", otherUsername, message));
            connections.broadcast(null, game.gameID(), status);
        }
    }
}