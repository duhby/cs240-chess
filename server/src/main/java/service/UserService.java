package service;

import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
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
        String hashedPassword = BCrypt.hashpw(data.password(), BCrypt.gensalt());
        UserData user = userAccess.create(new UserData(data.username(), hashedPassword, data.email()));
        AuthData auth = authAccess.create(new AuthData(UUID.randomUUID().toString(), user.username()));
        return new RegisterResult(auth.username(), auth.authToken());
    }
}
