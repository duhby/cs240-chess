package service;

import dataaccess.*;
import exception.ResponseException;

public class DatabaseService {
    private final AuthAccess authAccess;
    private final GameAccess gameAccess;
    private final UserAccess userAccess;

    public DatabaseService(AuthAccess authAccess, GameAccess gameAccess, UserAccess userAccess) {
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
        this.userAccess = userAccess;
    }

    public void clear() throws ResponseException {
        authAccess.deleteAll();
        gameAccess.deleteAll();
        userAccess.deleteAll();
    }
}
