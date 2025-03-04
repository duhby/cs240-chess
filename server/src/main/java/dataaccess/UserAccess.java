package dataaccess;

import exception.DataAccess;
import model.UserData;

public interface UserAccess {
    UserData create(UserData data) throws DataAccess;
    UserData get(String username) throws DataAccess;
    void deleteAll() throws DataAccess;
}
