package dataaccess;

import exception.ResponseException;
import model.UserData;

public interface UserAccess {
    UserData create(UserData data) throws ResponseException;
    UserData get(String username) throws ResponseException;
    void deleteAll() throws ResponseException;
}
