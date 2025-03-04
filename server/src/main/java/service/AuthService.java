package service;

import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import record.LoginRequest;
import record.LoginResult;

import java.util.Objects;
import java.util.UUID;

public class AuthService {
    private final AuthAccess authAccess;
    private final UserAccess userAccess;

    public AuthService(AuthAccess authAccess, UserAccess userAccess) {
        this.authAccess = authAccess;
        this.userAccess = userAccess;
    }

    public LoginResult login(LoginRequest data) throws ResponseException {
        UserData user;
        try {
            user = userAccess.get(data.username());
        } catch (ResponseException e) {
            throw ResponseException.unauthorized();
        }
        if (!Objects.equals(user.password(), data.password())) {
            throw ResponseException.unauthorized();
        }
        AuthData auth = authAccess.create(new AuthData(UUID.randomUUID().toString(), user.username()));
        return new LoginResult(user.username(), auth.authToken());
    }

    public void logout(String authToken) throws ResponseException {
        authAccess.delete(authToken);
    }
}
