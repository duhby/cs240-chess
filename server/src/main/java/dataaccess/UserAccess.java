package dataaccess;

import exception.DataAccessException;
import model.UserData;

public interface UserAccess {
    UserData create(UserData data) throws DataAccessException;
    UserData get(String username) throws DataAccessException;
    void deleteAll() throws DataAccessException;
}
