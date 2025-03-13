package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.DatabaseService;

import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

record Accesses(AuthAccess auth, GameAccess game, UserAccess user) {}

public class DataAccessTests {
    private Accesses getServices() throws ResponseException {
        DatabaseManager.initializeDatabase();
        AuthAccess authAccess = new AuthAccessDB();
        GameAccess gameAccess = new GameAccessDB();
        UserAccess userAccess = new UserAccessDB();
        DatabaseService databaseService = new DatabaseService(authAccess, gameAccess, userAccess);

        databaseService.clear();
        return new Accesses(authAccess, gameAccess, userAccess);
    }

    @Test
    public void createAuthPositive() throws ResponseException {
        Accesses accesses = getServices();
        String username = "testUser";
        String authToken = UUID.randomUUID().toString();

        AuthData authData = new AuthData(authToken, username);
        AuthData result = accesses.auth().create(authData);

        assertEquals(authToken, result.authToken());
        assertEquals(username, result.username());
    }

    @Test
    public void createAuthNegative() throws ResponseException {
        Accesses accesses = getServices();
        String username = "testUser";
        String authToken = UUID.randomUUID().toString();

        AuthData authData = new AuthData(authToken, username);
        accesses.auth().create(authData);

        AuthData duplicateAuth = new AuthData(authToken, "anotherUser");

        assertThrows(ResponseException.class, () -> {
            accesses.auth().create(duplicateAuth);
        });
    }

    @Test
    public void getAuthPositive() throws ResponseException {
        Accesses accesses = getServices();
        String username = "testUser";
        String authToken = UUID.randomUUID().toString();

        AuthData authData = new AuthData(authToken, username);
        accesses.auth().create(authData);

        AuthData result = accesses.auth().get(authToken);

        assertEquals(authToken, result.authToken());
        assertEquals(username, result.username());
    }

    @Test
    public void getAuthNegative() throws ResponseException {
        Accesses accesses = getServices();
        String nonExistentToken = UUID.randomUUID().toString();

        assertThrows(ResponseException.class, () -> {
            accesses.auth().get(nonExistentToken);
        });
    }

    @Test
    public void deleteAuthPositive() throws ResponseException {
        Accesses accesses = getServices();
        String username = "testUser";
        String authToken = UUID.randomUUID().toString();

        AuthData authData = new AuthData(authToken, username);
        accesses.auth().create(authData);

        accesses.auth().delete(authToken);

        assertThrows(ResponseException.class, () -> {
            accesses.auth().get(authToken);
        });
    }

    @Test
    public void deleteAuthNegative() throws ResponseException {
        Accesses accesses = getServices();
        String nonExistentToken = UUID.randomUUID().toString();

        assertThrows(ResponseException.class, () -> {
            accesses.auth().delete(nonExistentToken);
        });
    }

    @Test
    public void deleteAllAuthPositive() throws ResponseException {
        Accesses accesses = getServices();

        AuthData auth1 = new AuthData(UUID.randomUUID().toString(), "user1");
        AuthData auth2 = new AuthData(UUID.randomUUID().toString(), "user2");
        accesses.auth().create(auth1);
        accesses.auth().create(auth2);

        accesses.auth().deleteAll();

        assertThrows(ResponseException.class, () -> {
            accesses.auth().get(auth1.authToken());
        });

        assertThrows(ResponseException.class, () -> {
            accesses.auth().get(auth2.authToken());
        });
    }

    @Test
    public void createUserPositive() throws ResponseException {
        Accesses accesses = getServices();
        UserData userData = new UserData("testUser", "password", "test@example.com");

        UserData result = accesses.user().create(userData);

        assertEquals("testUser", result.username());
        assertEquals("password", result.password());
        assertEquals("test@example.com", result.email());
    }

    @Test
    public void createUserNegative() throws ResponseException {
        Accesses accesses = getServices();
        UserData userData = new UserData("testUser", "password", "test@example.com");
        accesses.user().create(userData);

        UserData duplicateUser = new UserData("testUser", "anotherPassword", "another@example.com");

        assertThrows(ResponseException.class, () -> {
            accesses.user().create(duplicateUser);
        });
    }

    @Test
    public void getUserPositive() throws ResponseException {
        Accesses accesses = getServices();
        UserData userData = new UserData("testUser", "password", "test@example.com");
        accesses.user().create(userData);

        UserData result = accesses.user().get("testUser");

        assertEquals("testUser", result.username());
        assertEquals("password", result.password());
        assertEquals("test@example.com", result.email());
    }

    @Test
    public void getUserNegative() throws ResponseException {
        Accesses accesses = getServices();

        assertThrows(ResponseException.class, () -> {
            accesses.user().get("nonExistentUser");
        });
    }

    @Test
    public void deleteAllUsersPositive() throws ResponseException {
        Accesses accesses = getServices();

        UserData user1 = new UserData("user1", "password1", "user1@example.com");
        UserData user2 = new UserData("user2", "password2", "user2@example.com");
        accesses.user().create(user1);
        accesses.user().create(user2);

        accesses.user().deleteAll();

        assertThrows(ResponseException.class, () -> {
            accesses.user().get("user1");
        });

        assertThrows(ResponseException.class, () -> {
            accesses.user().get("user2");
        });
    }

    @Test
    public void createGamePositive() throws ResponseException {
        Accesses accesses = getServices();
        GameData gameData = new GameData(0, null, null, "testGame", null);

        GameData result = accesses.game().create(gameData);

        assertEquals(0, result.gameID());
        assertEquals("testGame", result.gameName());
    }

    @Test
    public void createGameNegative() throws ResponseException {
        Accesses accesses = getServices();
        GameData invalidGameData = new GameData(0, null, null, null, null);

        assertThrows(ResponseException.class, () -> {
            accesses.game().create(invalidGameData);
        });
    }

    @Test
    public void getGamePositive() throws ResponseException {
        Accesses accesses = getServices();
        GameData gameData = new GameData(0, null, null, "testGame", null);
        GameData createdGame = accesses.game().create(gameData);

        GameData result = accesses.game().get(createdGame.gameID());

        assertEquals(createdGame.gameID(), result.gameID());
        assertEquals("testGame", result.gameName());
    }

    @Test
    public void getGameNegative() throws ResponseException {
        Accesses accesses = getServices();

        assertThrows(ResponseException.class, () -> {
            accesses.game().get(999);
        });
    }

    @Test
    public void getAllGamesPositive() throws ResponseException {
        Accesses accesses = getServices();

        GameData game1 = new GameData(0, null, null, "game1", null);
        GameData game2 = new GameData(1, null, null, "game2", null);
        accesses.game().create(game1);
        accesses.game().create(game2);

        Collection<GameData> games = accesses.game().getAll();

        assertEquals(2, games.size());
    }

    @Test
    public void addPlayerPositive() throws ResponseException {
        Accesses accesses = getServices();

        UserData userData = new UserData("testUser", "password", "test@example.com");
        accesses.user().create(userData);

        GameData gameData = new GameData(0, null, null, "testGame", null);
        GameData createdGame = accesses.game().create(gameData);

        accesses.game().addPlayer(createdGame.gameID(), "WHITE", "testUser");

        GameData updatedGame = accesses.game().get(createdGame.gameID());
        assertEquals("testUser", updatedGame.whiteUsername());
    }

    @Test
    public void addPlayerNegative() throws ResponseException {
        Accesses accesses = getServices();

        UserData user1 = new UserData("user1", "password1", "user1@example.com");
        UserData user2 = new UserData("user2", "password2", "user2@example.com");
        accesses.user().create(user1);
        accesses.user().create(user2);

        GameData gameData = new GameData(0, null, null, "testGame", null);
        GameData createdGame = accesses.game().create(gameData);

        accesses.game().addPlayer(createdGame.gameID(), "WHITE", "user1");

        assertThrows(ResponseException.class, () -> {
            accesses.game().addPlayer(createdGame.gameID(), "WHITE", "user2");
        });
    }

    @Test
    public void deleteAllGamesPositive() throws ResponseException {
        Accesses accesses = getServices();

        GameData game1 = new GameData(0, null, null, "game1", null);
        GameData game2 = new GameData(1, null, null, "game2", null);
        GameData createdGame1 = accesses.game().create(game1);
        GameData createdGame2 = accesses.game().create(game2);

        accesses.game().deleteAll();

        Collection<GameData> games = accesses.game().getAll();
        assertTrue(games.isEmpty());

        assertThrows(ResponseException.class, () -> {
            accesses.game().get(createdGame1.gameID());
        });

        assertThrows(ResponseException.class, () -> {
            accesses.game().get(createdGame2.gameID());
        });
    }
}