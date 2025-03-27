package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import record.*;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {
    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void setup() throws ResponseException {
        facade.clear();
    }

    @Test
    public void testRegisterSuccess() throws ResponseException {
        RegisterResult result = facade.register(new RegisterRequest("user", "pass", "email"));
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void testRegisterFailure() {
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.register(new RegisterRequest("user", "pass", "email"));
            facade.register(new RegisterRequest("user", "pass", "email"));
        });
    }

    @Test
    public void testLoginSuccess() throws ResponseException {
        facade.register(new RegisterRequest("user", "pass", "email"));
        LoginResult result = facade.login(new LoginRequest("user", "pass"));
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void testLoginFailure() {
        Assertions.assertThrows(ResponseException.class, () -> facade.login(new LoginRequest("user", "wrongPass")));
    }

    @Test
    public void testLogoutSuccess() throws ResponseException {
        String authToken = facade.register(new RegisterRequest("user", "pass", "email")).authToken();
        facade.logout(authToken);
    }

    @Test
    public void testLogoutFailure() {
        Assertions.assertThrows(ResponseException.class, () -> facade.logout("invalidToken"));
    }

    @Test
    public void testCreateGameSuccess() throws ResponseException {
        String authToken = facade.register(new RegisterRequest("user", "pass", "email")).authToken();
        CreateGameResponse response = facade.createGame(new CreateGameRequest("game1"), authToken);
        Assertions.assertNotNull(response);
    }

    @Test
    public void testCreateGameFailure() {
        Assertions.assertThrows(ResponseException.class, () ->
                facade.createGame(new CreateGameRequest(null), "invalidToken"));
    }

    @Test
    public void testListGamesSuccess() throws ResponseException {
        String authToken = facade.register(new RegisterRequest("user", "pass", "email")).authToken();
        facade.createGame(new CreateGameRequest("game1"), authToken);
        ListGamesResponse response = facade.listGames(authToken);
        Assertions.assertFalse(response.games().isEmpty());
    }

    @Test
    public void testListGamesFailure() {
        Assertions.assertThrows(ResponseException.class, () -> facade.listGames("invalidToken"));
    }

    @Test
    public void testJoinGameSuccess() throws ResponseException {
        String authToken = facade.register(new RegisterRequest("user", "pass", "email")).authToken();
        CreateGameResponse response = facade.createGame(new CreateGameRequest("game1"), authToken);
        facade.joinGame(new JoinGameRequest("WHITE", response.gameID()), authToken);
    }

    @Test
    public void testJoinGameFailure() {
        Assertions.assertThrows(ResponseException.class, () -> facade.joinGame(new JoinGameRequest("asdf", 555), "invalidToken"));
    }
}
