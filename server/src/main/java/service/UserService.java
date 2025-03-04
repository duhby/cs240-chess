package service;

import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import record.RegisterRequest;
import record.RegisterResult;

import java.util.UUID;

public class UserService {
    private final AuthAccess authAccess;
    private final UserAccess userAccess;

    public UserService(AuthAccess authAccess, UserAccess userAccess) {
        this.authAccess = authAccess;
        this.userAccess = userAccess;
    }

    public RegisterResult register(RegisterRequest data) throws ResponseException {
        UserData user = userAccess.create(new UserData(data.username(), data.password(), data.email()));
        AuthData auth = authAccess.create(new AuthData(UUID.randomUUID().toString(), user.username()));
        return new RegisterResult(auth.username(), auth.authToken());
    }
}
