package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class GameAccessDB implements GameAccess {
    public GameData create(GameData data) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("INSERT INTO game (id, gameName, game) VALUES (?, ?, ?)")) {
                ps.setInt(1, data.gameID());
                ps.setString(2, data.gameName());
                ps.setString(3, new Gson().toJson(data.game()));
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ResponseException(500, e.getMessage());
        }
        return data;
    }

    public GameData get(int gameID) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT id, whiteUsername, blackUsername, gameName, game FROM game WHERE id=?")) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var json = rs.getString("game");
                        var game = new Gson().fromJson(json, ChessGame.class);
                        return new GameData(
                                rs.getInt("id"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                game
                        );
                    }
                    throw ResponseException.badRequest();
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    // Does not validate moves or turns etc
    public void edit(GameData data) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("UPDATE game SET game=? WHERE id=?")) {
                ps.setString(1, new Gson().toJson(data.game()));
                ps.setInt(2, data.gameID());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public Collection<GameData> getAll() throws ResponseException {
        var games = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT id, whiteUsername, blackUsername, gameName, game FROM game")) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        var json = rs.getString("game");
                        var game = new Gson().fromJson(json, ChessGame.class);
                        games.add(new GameData(
                                rs.getInt("id"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                game
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(500, e.getMessage());
        }
        return games;
    }

    public void addPlayer(int gameID, String color, String username) throws ResponseException {
        GameData data = this.get(gameID);
        String whiteUsername = data.whiteUsername();
        String blackUsername = data.blackUsername();
        String statement;
        switch (color) {
            case "WHITE" -> {
                if (whiteUsername != null) {
                    throw ResponseException.alreadyTaken();
                }
                statement = "UPDATE game SET whiteUsername=? WHERE id=?";
            }
            case "BLACK" -> {
                if (blackUsername != null) {
                    throw ResponseException.alreadyTaken();
                }
                statement = "UPDATE game SET blackUsername=? WHERE id=?";
            }
            default -> throw ResponseException.badRequest();
        }
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ps.setInt(2, data.gameID());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void removePlayer(int gameID, String username) throws ResponseException {
        GameData data = this.get(gameID);
        String statement;
        if (username.equals(data.whiteUsername())) {
            statement = "UPDATE game SET whiteUsername=NULL WHERE id=?";
        } else if (username.equals(data.blackUsername())) {
            statement = "UPDATE game SET blackUsername=NULL WHERE id=?";
        } else {
            throw ResponseException.badRequest();
        }
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, data.gameID());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void deleteAll() throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("TRUNCATE game")) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
