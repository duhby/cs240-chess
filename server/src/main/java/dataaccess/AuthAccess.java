package dataaccess;

import exception.ResponseException;
import model.AuthData;

public interface AuthAccess {
    AuthData create(AuthData data) throws ResponseException;
    AuthData get(String authToken) throws ResponseException;
    void delete(String authToken) throws ResponseException;
    void deleteAll() throws ResponseException;
}
