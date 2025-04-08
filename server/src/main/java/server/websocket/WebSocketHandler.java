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
            case LEAVE -> this.leave(username, game);
            case RESIGN -> this.resign(username, game);
        }
    }

    private void sendError(Session session, String message) throws IOException {
        Error error = new Error(message);
        session.getRemote().sendString(error.toString());
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
    }

//
//    private void exit(String visitorName) throws IOException {
//        connections.remove(visitorName);
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
}
