package dataaccess;

import exception.DataAccess;
import model.AuthData;

public interface AuthAccess {
    AuthData create(AuthData data) throws DataAccess;
    AuthData get(String authToken) throws DataAccess;
    void delete(String authToken) throws DataAccess;
    void deleteAll() throws DataAccess;
}
