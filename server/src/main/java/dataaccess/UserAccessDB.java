package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;
import model.UserData;

import java.sql.SQLException;

public class UserAccessDB implements UserAccess {
    public UserData create(UserData data) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("INSERT INTO player (username, password, email) VALUES (?, ?, ?)")) {
                ps.setString(1, data.username());
                ps.setString(2, data.password());
                ps.setString(3, data.email());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw ResponseException.alreadyTaken();
            }
            throw new ResponseException(500, e.getMessage());
        }
        return data;
    }

    public UserData get(String username) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT username, password, email FROM player WHERE username=?")) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email")
                        );
                    }
                    throw ResponseException.badRequest();
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void deleteAll() throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("TRUNCATE player")) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
