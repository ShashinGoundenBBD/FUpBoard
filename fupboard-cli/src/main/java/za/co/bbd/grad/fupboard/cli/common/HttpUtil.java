package za.co.bbd.grad.fupboard.cli.common;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import za.co.bbd.grad.fupboard.cli.models.ApiError;
import za.co.bbd.grad.fupboard.cli.services.AuthenticationService;

public class HttpUtil {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static ObjectMapper mapper = new ObjectMapper();

    public static <T> T get(String url, final TypeReference<T> type) throws NavStateException {
        var request = HttpRequest.newBuilder(URI.create(url))
            .header("Authorization", "Bearer " + AuthenticationService.getAuthToken())
            .GET().build();
        return send(request, type);
    }

    public static <T> T delete(String url, final TypeReference<T> type) throws NavStateException {
        var request = HttpRequest.newBuilder(URI.create(url))
            .header("Authorization", "Bearer " + AuthenticationService.getAuthToken())
            .DELETE().build();
        return send(request, type);
    }

    public static <R, T> T post(String url, R body, final TypeReference<T> type) throws NavStateException {
        String bodyString;
        try {
            bodyString = mapper.writeValueAsString(body);
        } catch (JacksonException e) {
            throw new NavStateException("Client could not serialise request.");
        }
        var request = HttpRequest.newBuilder(URI.create(url))
            .header("Authorization", "Bearer " + AuthenticationService.getAuthToken())
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(bodyString)).build();
        return send(request, type);
    }

    public static <R, T> T patch(String url, R body, final TypeReference<T> type) throws NavStateException {
        String bodyString;
        try {
            bodyString = mapper.writeValueAsString(body);
        } catch (JacksonException e) {
            throw new NavStateException("Client could not serialise request.");
        }
        var request = HttpRequest.newBuilder(URI.create(url))
            .header("Authorization", "Bearer " + AuthenticationService.getAuthToken())
            .header("Content-Type", "application/json")
            .method("PATCH", HttpRequest.BodyPublishers.ofString(bodyString)).build();
        return send(request, type);
    }
    
    private static <T> T send(HttpRequest request, final TypeReference<T> type) throws NavStateException {
        HttpResponse<String> response;
        try {
            response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new NavStateException("Failed to send request.");
        }

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            String errorMessage;
            
            try {
                var error = mapper.readValue(response.body(), ApiError.class);
                errorMessage = error.getError();
                throw new NavStateException(errorMessage);
            } catch (JacksonException e) {
                throw new NavStateException(e);
            }

        }
        
        var respBody = response.body();

        if (type == null) return null;
            
        T resp;
        try {
            resp = mapper.readValue(respBody, type);
        } catch (JacksonException e) {
            System.out.println(e.getMessage());
            throw new NavStateException(e);
        }

        return resp;
    }
}  


