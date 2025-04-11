package websocket.messages;

import model.GameData;

public class LoadGame extends ServerMessage {
    GameData game;

    public LoadGame(GameData game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public GameData getGameData() {
        return this.game;
    }
}
