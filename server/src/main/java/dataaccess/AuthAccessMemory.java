package dataaccess;

import exception.DataAccess;
import model.AuthData;

import java.util.HashMap;

public class AuthAccessMemory implements AuthAccess {
    private HashMap<String, AuthData> rows = new HashMap<>();

    public AuthData create(AuthData data) {
        rows.put(data.authToken(), data);
        return data;
    }

    public AuthData get(String authToken) throws DataAccess {
        AuthData data = rows.get(authToken);
        if (data == null) {
            throw new DataAccess("Not found");
        }
        return data;
    }

    public void delete(String authToken) throws DataAccess {
        AuthData data = rows.remove(authToken);
        if (data == null) {
            throw new DataAccess("Not found");
        }
    }

    public void deleteAll() {
        rows = new HashMap<>();
    }
}
