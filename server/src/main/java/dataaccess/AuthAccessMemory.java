package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class AuthAccessMemory implements AuthAccess {
    private HashMap<String, AuthData> rows = new HashMap<>();

    public AuthData create(AuthData data) {
        rows.put(data.authToken(), data);
        return data;
    }
    public AuthData get(String authToken) throws DataAccessException {
        AuthData data = rows.get(authToken);
        if (data == null) {
            throw new DataAccessException("Not found");
        }
        return data;
    }
    public void delete(String authToken) throws DataAccessException {
        AuthData data = rows.remove(authToken);
        if (data == null) {
            throw new DataAccessException("Not found");
        }
    }
    public void deleteAll() {
        rows = new HashMap<>();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
