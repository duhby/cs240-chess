package exception;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

// Derived from the petshop example
public class ResponseException extends Exception {
    final private int statusCode;

    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", "Error: " + getMessage()));
    }

    public static ResponseException unauthorized() {
        return new ResponseException(401, "unauthorized");
    }

    public static ResponseException badRequest() {
        return new ResponseException(400, "bad request");
    }

    public static ResponseException alreadyTaken() {
        return new ResponseException(403, "already taken");
    }

    public static ResponseException fromJson(InputStream stream) {
        var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
        var status = ((Double)map.get("status")).intValue();
        String message = map.get("message").toString();
        return new ResponseException(status, message);
    }

    public int statusCode() {
        return statusCode;
    }
}
