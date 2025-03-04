package service;

import dataaccess.*;
import org.junit.jupiter.api.*;
import record.CreateGameRequest;
import record.LoginRequest;
import record.RegisterRequest;

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
}
