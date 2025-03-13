package dataaccess;

import exception.ResponseException;
import model.AuthData;

import java.sql.SQLException;

public class AuthAccessDB implements AuthAccess {
    public AuthData create(AuthData data) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES (?, ?)")) {
                ps.setString(1, data.authToken());
                ps.setString(2, data.username());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ResponseException(500, e.getMessage());
        }
        return data;
    }

    public AuthData get(String authToken) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT authToken, username FROM auth WHERE authToken=?")) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(authToken, rs.getString("username"));
                    }
                    throw ResponseException.unauthorized();
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void delete(String authToken) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("DELETE FROM auth WHERE authToken=?")) {
                ps.setString(1, authToken);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw ResponseException.unauthorized();
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void deleteAll() throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("TRUNCATE auth")) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
