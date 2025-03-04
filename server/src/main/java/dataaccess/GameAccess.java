package dataaccess;

import java.util.Collection;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;

public interface GameAccess {
    GameData create(GameData data) throws ResponseException;
    GameData get(int gameID) throws ResponseException;
    Collection<GameData> getAll() throws ResponseException;
    GameData addPlayer(int gameID, String color, String username) throws ResponseException;
    GameData updatePosition(int gameID, ChessGame game) throws ResponseException;
    void deleteAll() throws ResponseException;
}
