package za.co.bbd.grad.fupboard.cli.common;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import za.co.bbd.grad.fupboard.cli.services.Authentication;

public class HttpUtil {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    public static String authToken;

    public static void oAuthSignIn()
    {
        try {
            authToken = Authentication.performOAuth2Login();
        } catch (Exception e) {
            System.out.println("Sign-in was not successful: " + e.getMessage());
            // Maybe ask to retry instead
            return;
        }
    }

    public static HttpRequest getRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + authToken)
                .GET().build();
    }

    public static HttpRequest postRequest(String url, String json) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json)).build();
    }

    public static HttpRequest deleteRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + authToken)
                .DELETE().build();
    }

    public static HttpRequest patchRequest(String url, String json) {
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
            
                ObjectMapper objectMapper = new ObjectMapper();
                String responseBody = response.body();
                String errorMessage = "Unknown error";

                try {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                
                    if (jsonNode.has("error")) {
                        errorMessage = jsonNode.get("error").asText();
                    }
                } catch (Exception e) {
                    System.out.println(Constants.RED + "Failed to parse error message: " + e.getMessage() + Constants.RESET);
                }

                System.out.println(Constants.RED + "Error : " + response.statusCode() + " - " + errorMessage + Constants.RESET);
          
                return null;
                
            }
        } catch (IOException | InterruptedException e) {
            System.err.println(Constants.RED + "Error: " + e.getMessage() + Constants.RESET);
            return null;
        }
    }
}  


