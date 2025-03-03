package dataaccess;

import java.util.HashMap;

import model.UserData;

public class UserAccessMemory implements UserAccess {
    private HashMap<String, UserData> rows = new HashMap<>();

    public UserData create(UserData data) {
        rows.put(data.username(), data);
        return data;
    }

    public UserData get(String username) throws DataAccessException {
        UserData data = rows.get(username);
        if (data == null) {
            throw new DataAccessException("Not found");
        }
        return data;
    }

    public void deleteAll() {
        rows = new HashMap<>();
    }
}
