package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import exception.ResponseException;
import record.LoginRequest;
import record.LoginResult;
import service.AuthService;
import service.DatabaseService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {
    private final AuthService authService;
//    private final GameService gameService;
//    private final UserService userService;
    private final DatabaseService databaseService;

    public Server() {
        AuthAccess authAccess = new AuthAccessMemory();
        GameAccess gameAccess = new GameAccessMemory();
        UserAccess userAccess = new UserAccessMemory();

        authService = new AuthService(authAccess, userAccess);
//        gameService = new GameService();
//        userService = new UserService();
        databaseService = new DatabaseService(authAccess, gameAccess, userAccess);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::deleteAll);
        Spark.post("/session", this::login);
        Spark.exception(ResponseException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object deleteAll(Request req, Response res) throws ResponseException {
        databaseService.clear();
        return "{}";
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
