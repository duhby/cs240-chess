package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import exception.ResponseException;
import record.*;
import service.AuthService;
import service.DatabaseService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {
    private final AuthService authService;
    private final GameService gameService;
    private final UserService userService;
    private final DatabaseService databaseService;

    public Server() {
        AuthAccess authAccess = new AuthAccessMemory();
        GameAccess gameAccess = new GameAccessMemory();
        UserAccess userAccess = new UserAccessMemory();

        authService = new AuthService(authAccess, userAccess);
        gameService = new GameService(authAccess, gameAccess);
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
        Spark.post("/game", this::createGame);
        Spark.get("/game", this::listGames);
        Spark.put("/game", this::joinGame);
        Spark.exception(ResponseException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object deleteAll(Request req, Response res) throws ResponseException {
        databaseService.clear();
        return "{}";
    }

    private Object register(Request req, Response res) throws ResponseException {
        RegisterRequest data = serialize(req.body(), RegisterRequest.class);
        if (data.username() == null || data.password() == null || data.email() == null) {
            throw ResponseException.badRequest();
        }
        RegisterResult registerResult = this.userService.register(data);
        return new Gson().toJson(registerResult);
    }

    private Object login(Request req, Response res) throws ResponseException {
        LoginRequest data = serialize(req.body(), LoginRequest.class);
        LoginResult loginResult = this.authService.login(data);
        return new Gson().toJson(loginResult);
    }

    private Object logout(Request req, Response res) throws ResponseException {
        String authToken = req.headers("authorization");
        this.authService.logout(authToken);
        return "{}";
    }

    private Object createGame(Request req, Response res) throws ResponseException {
        String authToken = req.headers("authorization");
        CreateGameRequest data = serialize(req.body(), CreateGameRequest.class);
        CreateGameResponse createGameResponse = this.gameService.create(data, authToken);
        return new Gson().toJson(createGameResponse);
    }

    private Object listGames(Request req, Response res) throws ResponseException {
        String authToken = req.headers("authorization");
        ListGamesResponse games = gameService.listGames(authToken);
        return new Gson().toJson(games);
    }

    private Object joinGame(Request req, Response res) throws ResponseException {
        String authToken = req.headers("authorization");
        JoinGameRequest data = serialize(req.body(), JoinGameRequest.class);
        if (data.playerColor() == null) {
            throw ResponseException.badRequest();
        }
        gameService.joinGame(data, authToken);
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
        res.status(ex.statusCode());
        res.body(ex.toJson());
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
