package za.co.bbd.grad.fupboard.cli;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpUtil {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static HttpRequest getRequest(String authToken, String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + authToken)
                .GET().build();
    }

    public static HttpRequest postRequest(String authToken, String url, String json) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json)).build();
    }

    public static HttpRequest deleteRequest(String authtoken, String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + authtoken)
                .DELETE().build();
    }

    public static HttpRequest patchRequest(String authToken, String url, String json) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json)).build();
    }

    public static String sendRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return response.body();
            } else {
                System.out.println(ConsoleColors.RED + "Request failed: " + response.statusCode() + ConsoleColors.RESET);
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println(ConsoleColors.RED + "Error: " + e.getMessage() + ConsoleColors.RESET);
            return null;
        }
    }
}  


