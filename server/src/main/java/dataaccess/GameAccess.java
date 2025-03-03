package dataaccess;

import java.util.Collection;

import chess.ChessGame;
import model.GameData;

public interface GameAccess {
    GameData create(GameData data) throws DataAccessException;
    GameData get(int gameID) throws DataAccessException;
    GameData addPlayer(int gameID, String playerColor) throws DataAccessException;
    GameData updatePosition(ChessGame game) throws DataAccessException;
    Collection<GameData> getAll() throws DataAccessException;
    void deleteAll() throws DataAccessException;
}
