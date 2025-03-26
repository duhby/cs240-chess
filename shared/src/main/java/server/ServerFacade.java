package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;
import record.*;

import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    public RegisterResult register(RegisterRequest req) throws ResponseException {
        return this.makeRequest("POST", "/user", req, RegisterResult.class, null);
    }

    public LoginResult login(LoginRequest req) throws ResponseException {
        return this.makeRequest("POST", "/session", req, LoginResult.class, null);
    }

    public void logout(String auth) throws ResponseException {
        this.makeRequest("DELETE", "/session", null, null, auth);
    }

    public CreateGameResponse createGame(CreateGameRequest req, String auth) throws ResponseException {
        return this.makeRequest("POST", "/game", req, CreateGameResponse.class, auth);
    }

    public ListGamesResponse listGames(String auth) throws ResponseException {
        return this.makeRequest("GET", "/game", null, ListGamesResponse.class, auth);
    }

    public void joinGame(JoinGameRequest req, String auth) throws ResponseException {
        this.makeRequest("PUT", "/game", req, JoinGameRequest.class, auth);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String auth) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            if (auth != null) {
                http.setRequestProperty("authorization", auth);
            }
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
