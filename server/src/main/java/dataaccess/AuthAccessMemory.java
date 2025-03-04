package dataaccess;

import exception.ResponseException;
import model.AuthData;

import java.util.HashMap;

public class AuthAccessMemory implements AuthAccess {
    private HashMap<String, AuthData> rows = new HashMap<>();

    public AuthData create(AuthData data) {
        rows.put(data.authToken(), data);
        return data;
    }

    public AuthData get(String authToken) throws ResponseException {
        AuthData data = rows.get(authToken);
        if (data == null) {
            throw ResponseException.unauthorized();
        }
        return data;
    }

    public void delete(String authToken) throws ResponseException {
        AuthData data = rows.remove(authToken);
        if (data == null) {
            throw ResponseException.unauthorized();
        }
    }

    public void deleteAll() {
        rows = new HashMap<>();
    }
}
