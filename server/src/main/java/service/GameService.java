package service;

import chess.ChessGame;
import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import record.CreateGameRequest;
import record.CreateGameResponse;
import record.JoinGameRequest;
import record.ListGamesResponse;

import java.util.Collection;

public class GameService {
    private int nextID = 1;
    private final AuthAccess authAccess;
    private final GameAccess gameAccess;

    public GameService(AuthAccess authAccess, GameAccess gameAccess) {
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
    }

    public CreateGameResponse create(CreateGameRequest data, String authToken) throws ResponseException {
        authAccess.get(authToken);
        if (data.gameName() == null) {
            throw ResponseException.badRequest();
        }
        GameData game = gameAccess.create(new GameData(this.getNextID(), null, null, data.gameName(), new ChessGame()));
        return new CreateGameResponse(game.gameID());
    }

    public ListGamesResponse listGames(String authToken) throws ResponseException {
        authAccess.get(authToken);
        Collection<GameData> games = gameAccess.getAll();
        return new ListGamesResponse(games);
    }

    public void joinGame(JoinGameRequest data, String authToken) throws ResponseException {
        AuthData auth = authAccess.get(authToken);
        gameAccess.addPlayer(data.gameID(), data.playerColor(), auth.username());
    }

    private int getNextID() {
        return nextID++;
    }
}
