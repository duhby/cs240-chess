package service;

import dataaccess.*;
import org.junit.jupiter.api.*;
import record.*;
import exception.ResponseException;

record Services(AuthService auth, DatabaseService db, GameService game, UserService user) {}

public class ServiceTests {
    private Services getServices() {
        AuthAccess authAccess = new AuthAccessMemory();
        GameAccess gameAccess = new GameAccessMemory();
        UserAccess userAccess = new UserAccessMemory();
        AuthService authService = new AuthService(authAccess, userAccess);
        DatabaseService databaseService = new DatabaseService(authAccess, gameAccess, userAccess);
        GameService gameService = new GameService(authAccess, gameAccess);
        UserService userService = new UserService(authAccess, userAccess);
        return new Services(authService, databaseService, gameService, userService);
    }

    @Test
    public void clear() {
        Services services = this.getServices();

        try {
            String authToken = services.user().register(new RegisterRequest("username", "password", "asdf")).authToken();
            services.game().create(new CreateGameRequest("cool-game"), authToken);
            services.db().clear();
            authToken = services.user().register(new RegisterRequest("username", "password", "asdf")).authToken();
            if (!services.game().listGames(authToken).games().isEmpty()) {
                Assertions.fail("Games were not cleared.");
            }
        } catch (Exception e) {
            Assertions.fail("Unexpected exception was thrown");
        }
    }

    @Test
    public void testCreateGameSuccess() throws ResponseException {
        Services services = getServices();
        String authToken = services.user().register(new RegisterRequest("user", "pass", "email")).authToken();
        CreateGameResponse response = services.game().create(new CreateGameRequest("game1"), authToken);
        Assertions.assertNotNull(response);
    }

    @Test
    public void testCreateGameFailure() {
        Services services = getServices();
        Assertions.assertThrows(ResponseException.class, () ->
                services.game().create(new CreateGameRequest(null), "invalidToken"));
    }

    @Test
    public void testListGamesSuccess() throws ResponseException {
        Services services = getServices();
        String authToken = services.user().register(new RegisterRequest("user", "pass", "email")).authToken();
        services.game().create(new CreateGameRequest("game1"), authToken);
        ListGamesResponse response = services.game().listGames(authToken);
        Assertions.assertFalse(response.games().isEmpty());
    }

    @Test
    public void testListGamesFailure() {
        Services services = getServices();
        Assertions.assertThrows(ResponseException.class, () -> services.game().listGames("invalidToken"));
    }

    @Test
    public void testJoinGameSuccess() throws ResponseException {
        Services services = getServices();
        String authToken = services.user().register(new RegisterRequest("user", "pass", "email")).authToken();
        CreateGameResponse response = services.game().create(new CreateGameRequest("game1"), authToken);
        services.game().joinGame(new JoinGameRequest("WHITE", response.gameID()), authToken);
    }

    @Test
    public void testJoinGameFailure() {
        Services services = getServices();
        Assertions.assertThrows(ResponseException.class, () -> services.game().joinGame(new JoinGameRequest("asdf", 555), "invalidToken"));
    }

    @Test
    public void testRegisterSuccess() throws ResponseException {
        Services services = getServices();
        RegisterResult result = services.user().register(new RegisterRequest("user", "pass", "email"));
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void testRegisterFailure() {
        Services services = getServices();
        Assertions.assertThrows(ResponseException.class, () -> {
            services.user().register(new RegisterRequest("user", "pass", "email"));
            services.user().register(new RegisterRequest("user", "pass", "email"));
        });
    }

    @Test
    public void testLoginSuccess() throws ResponseException {
        Services services = getServices();
        services.user().register(new RegisterRequest("user", "pass", "email"));
        LoginResult result = services.auth().login(new LoginRequest("user", "pass"));
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void testLoginFailure() {
        Services services = getServices();
        Assertions.assertThrows(ResponseException.class, () -> services.auth().login(new LoginRequest("user", "wrongPass")));
    }

    @Test
    public void testLogoutSuccess() throws ResponseException {
        Services services = getServices();
        String authToken = services.user().register(new RegisterRequest("user", "pass", "email")).authToken();
        services.auth().logout(authToken);
    }

    @Test
    public void testLogoutFailure() {
        Services services = getServices();
        Assertions.assertThrows(ResponseException.class, () -> services.auth().logout("invalidToken"));
    }
}
