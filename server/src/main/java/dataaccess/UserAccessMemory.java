package dataaccess;

import java.util.HashMap;

import exception.ResponseException;
import model.UserData;

public class UserAccessMemory implements UserAccess {
    private HashMap<String, UserData> rows = new HashMap<>();

    public UserData create(UserData data) {
        rows.put(data.username(), data);
        return data;
    }

    public UserData get(String username) throws ResponseException {
        UserData data = rows.get(username);
        if (data == null) {
            throw ResponseException.badRequest();
        }
        return data;
    }

    public void deleteAll() {
        rows = new HashMap<>();
    }
}
