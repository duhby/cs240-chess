package websocket.messages;

public class Error extends ServerMessage {
    String message;

    public Error(String message) {
        super(ServerMessageType.ERROR);
        this.message = message;
    }
}
