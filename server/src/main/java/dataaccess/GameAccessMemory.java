package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class GameAccessMemory implements GameAccess {
    private HashMap<Integer, GameData> rows = new HashMap<>();

    public GameData create(GameData data) {
        rows.put(data.gameID(), data);
        return data;
    }

    public GameData get(int gameID) throws ResponseException {
        GameData data = rows.get(gameID);
        if (data == null) {
            throw ResponseException.badRequest();
        }
        return data;
    }

    public Collection<GameData> getAll() throws ResponseException {
        if (rows.isEmpty()) {
            throw ResponseException.badRequest();
        }
        return rows.values();
    }

    public GameData addPlayer(int gameID, String color, String username) throws ResponseException {
        GameData data = this.get(gameID);
        String whiteUsername = data.whiteUsername();
        String blackUsername = data.blackUsername();
        switch (color) {
            case "WHITE" -> whiteUsername = username;
            case "BLACK" -> blackUsername = username;
            default -> throw ResponseException.badRequest();
        }
        GameData newData = new GameData(data.gameID(), whiteUsername, blackUsername, data.gameName(), data.game());
        newData = rows.replace(gameID, newData);
        return newData;
    }

    public GameData updatePosition(int gameID, ChessGame game) throws ResponseException {
        GameData data = this.get(gameID);
        GameData newData = new GameData(data.gameID(), data.whiteUsername(), data.blackUsername(), data.gameName(), game);
        newData = rows.replace(gameID, newData);
        return newData;
    }

    public void delete(int gameID) throws ResponseException {
        GameData data = rows.remove(gameID);
        if (data == null) {
            throw ResponseException.badRequest();
        }
    }

    public void deleteAll() {
        rows = new HashMap<>();
    }
}
