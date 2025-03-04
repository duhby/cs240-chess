package dataaccess;

import java.util.Collection;

import chess.ChessGame;
import exception.DataAccess;
import model.GameData;

public interface GameAccess {
    GameData create(GameData data) throws DataAccess;
    GameData get(int gameID) throws DataAccess;
    Collection<GameData> getAll() throws DataAccess;
    GameData addPlayer(int gameID, String color, String username) throws DataAccess;
    GameData updatePosition(int gameID, ChessGame game) throws DataAccess;
    void deleteAll() throws DataAccess;
}
