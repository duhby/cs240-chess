package dataaccess;

import java.util.HashMap;

import exception.ResponseException;
import model.UserData;

public class UserAccessMemory implements UserAccess {
    private HashMap<String, UserData> rows = new HashMap<>();

    public UserData create(UserData data) throws ResponseException {
        for (UserData user : rows.values()) {
            if (user.email().equals(data.email()) || user.username().equals(data.username())) {
                throw ResponseException.alreadyTaken();
            }
        }

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
