package websocket.messages;

public class Error extends ServerMessage {
    String errorMessage;

    public Error(String message) {
        super(ServerMessageType.ERROR);
        this.errorMessage = message;
    }
}
