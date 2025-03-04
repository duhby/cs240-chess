package service;

import dataaccess.*;
import exception.DataAccess;
import exception.Unauthorized;
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

    public LoginResult login(LoginRequest data) throws DataAccess, Unauthorized {
        UserData user = userAccess.get(data.username());
        if (!Objects.equals(user.password(), data.password())) {
            throw new Unauthorized();
        }
        AuthData auth = authAccess.create(new AuthData(UUID.randomUUID().toString(), user.username()));
        return new LoginResult(user.username(), auth.authToken());
    }
}
