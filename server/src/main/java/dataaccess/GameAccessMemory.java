package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class GameAccessMemory implements GameAccess {
    private HashMap<Integer, GameData> rows = new HashMap<>();

    public GameData create(GameData data) {
        rows.put(data.gameID(), data);
        return data;
    }

    public GameData get(int gameID) throws DataAccessException {
        GameData data = rows.get(gameID);
        if (data == null) {
            throw new DataAccessException("Not found");
        }
        return data;
    }

    public Collection<GameData> getAll() throws DataAccessException {
        if (rows.isEmpty()) {
            throw new DataAccessException("Not found");
        }
        return rows.values();
    }

    public GameData addPlayer(int gameID, String color, String username) throws DataAccessException {
        GameData data = this.get(gameID);
        String whiteUsername = data.whiteUsername();
        String blackUsername = data.blackUsername();
        switch (color) {
            case "WHITE" -> whiteUsername = username;
            case "BLACK" -> blackUsername = username;
            default -> throw new DataAccessException("Invalid color");
        }
        GameData newData = new GameData(data.gameID(), whiteUsername, blackUsername, data.gameName(), data.game());
        newData = rows.replace(gameID, newData);
        return newData;
    }

    public GameData updatePosition(int gameID, ChessGame game) throws DataAccessException {
        GameData data = this.get(gameID);
        GameData newData = new GameData(data.gameID(), data.whiteUsername(), data.blackUsername(), data.gameName(), game);
        newData = rows.replace(gameID, newData);
        return newData;
    }

    public void delete(int gameID) throws DataAccessException {
        GameData data = rows.remove(gameID);
        if (data == null) {
            throw new DataAccessException("Not found");
        }
    }

    public void deleteAll() {
        rows = new HashMap<>();
    }
}
