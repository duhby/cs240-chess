package dataaccess;

import exception.DataAccessException;
import model.AuthData;

public interface AuthAccess {
    AuthData create(AuthData data) throws DataAccessException;
    AuthData get(String authToken) throws DataAccessException;
    void delete(String authToken) throws DataAccessException;
    void deleteAll() throws DataAccessException;
}
