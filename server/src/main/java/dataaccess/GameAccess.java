package dataaccess;

import java.util.Collection;

import exception.ResponseException;
import model.GameData;

public interface GameAccess {
    GameData create(GameData data) throws ResponseException;
    GameData get(int gameID) throws ResponseException;
    Collection<GameData> getAll() throws ResponseException;
    void addPlayer(int gameID, String color, String username) throws ResponseException;
    void deleteAll() throws ResponseException;
}
