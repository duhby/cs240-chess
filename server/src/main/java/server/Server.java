package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import exception.ResponseException;
import record.LoginRequest;
import record.LoginResult;
import service.AuthService;
import spark.*;

public class Server {
    private final AuthAccess authAccess;
    private final GameAccess gameAccess;
    private final UserAccess userAccess;
    private final AuthService authService;
    //    private final GameService gameService;
    //    private final UserService userService;

    public Server() {
        authAccess = new AuthAccessMemory();
        gameAccess = new GameAccessMemory();
        userAccess = new UserAccessMemory();
        authService = new AuthService(authAccess, userAccess);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.post("/session", this::login);
        Spark.exception(ResponseException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object login(Request req, Response res) throws ResponseException {
        LoginRequest loginRequest;
        try {
            loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        } catch (JsonSyntaxException e) {
            throw ResponseException.badRequest();
        }
        LoginResult loginResult = this.authService.login(loginRequest);
        return new Gson().toJson(loginResult);
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
