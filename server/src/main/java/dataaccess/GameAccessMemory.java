package dataaccess;

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

    public Collection<GameData> getAll() {
        return rows.values();
    }

    public void addPlayer(int gameID, String color, String username) throws ResponseException {
        GameData data = this.get(gameID);
        String whiteUsername = data.whiteUsername();
        String blackUsername = data.blackUsername();
        switch (color) {
            case "WHITE" -> {
                if (whiteUsername != null) {
                    throw ResponseException.alreadyTaken();
                }
                whiteUsername = username;
            }
            case "BLACK" -> {
                if (blackUsername != null) {
                    throw ResponseException.alreadyTaken();
                }
                blackUsername = username;
            }
            default -> throw ResponseException.badRequest();
        }
        GameData newData = new GameData(data.gameID(), whiteUsername, blackUsername, data.gameName(), data.game());
        rows.replace(gameID, newData);
    }

    public void deleteAll() {
        rows = new HashMap<>();
    }
}
