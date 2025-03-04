package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import exception.ResponseException;
import record.LoginRequest;
import record.LoginResult;
import record.RegisterRequest;
import record.RegisterResult;
import service.AuthService;
import service.DatabaseService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {
    private final AuthService authService;
//    private final GameService gameService;
    private final UserService userService;
    private final DatabaseService databaseService;

    public Server() {
        AuthAccess authAccess = new AuthAccessMemory();
        GameAccess gameAccess = new GameAccessMemory();
        UserAccess userAccess = new UserAccessMemory();

        authService = new AuthService(authAccess, userAccess);
//        gameService = new GameService();
        userService = new UserService(authAccess, userAccess);
        databaseService = new DatabaseService(authAccess, gameAccess, userAccess);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::deleteAll);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.exception(ResponseException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object deleteAll(Request req, Response res) throws ResponseException {
        databaseService.clear();
        return "{}";
    }

    private Object register(Request req, Response res) throws ResponseException {
        RegisterRequest registerRequest = serialize(req.body(), RegisterRequest.class);
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            throw ResponseException.badRequest();
        }
        RegisterResult registerResult = this.userService.register(registerRequest);
        return new Gson().toJson(registerResult);
    }

    private Object login(Request req, Response res) throws ResponseException {
        LoginRequest loginRequest = serialize(req.body(), LoginRequest.class);;
        LoginResult loginResult = this.authService.login(loginRequest);
        return new Gson().toJson(loginResult);
    }

    private Object logout(Request req, Response res) throws ResponseException {
        String authToken = req.headers("authorization");
        this.authService.logout(authToken);
        return "{}";
    }

    // Generics moment
    public static <T> T serialize(String body, Class<T> obj) throws ResponseException {
        try {
            T serialized = new Gson().fromJson(body, obj);
            if (serialized == null) {
                throw ResponseException.badRequest();
            }
            return serialized;
        } catch (JsonSyntaxException e) {
            throw ResponseException.badRequest();
        }
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
