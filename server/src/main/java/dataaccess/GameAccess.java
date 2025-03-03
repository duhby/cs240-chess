package dataaccess;

import java.util.Collection;

import chess.ChessGame;
import model.GameData;

public interface GameAccess {
    GameData create(GameData data) throws DataAccessException;
    GameData get(int gameID) throws DataAccessException;
    Collection<GameData> getAll() throws DataAccessException;
    GameData addPlayer(int gameID, String color, String username) throws DataAccessException;
    GameData updatePosition(int gameID, ChessGame game) throws DataAccessException;
    void deleteAll() throws DataAccessException;
}
