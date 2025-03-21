package za.co.bbd.grad.fupboard.cli.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.util.Random;

import javax.security.sasl.AuthenticationException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;

import za.co.bbd.grad.fupboard.cli.Main;
import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.NavStateException;
import za.co.bbd.grad.fupboard.cli.models.AuthResponse;

public class AuthenticationService {
    private static String authToken;
    private static int currentUserId;

    public static void performOAuth2Login() throws IOException, URISyntaxException, InterruptedException {
        var random = new Random();
        var server = new ServerSocket(0);
        var port = server.getLocalPort();

        var callbackUri = "http://127.0.0.1:" + port;
        var callbackUriEncoded = URLEncoder.encode(callbackUri, Charset.defaultCharset());
        
        var securityToken = random.nextLong(Integer.MAX_VALUE, Long.MAX_VALUE);
        var oauth2RequestUri = "https://accounts.google.com/o/oauth2/v2/auth" +
                "?scope=email%20profile" +
                "&response_type=code" +
                "&state=fupboard%3Asecurity_token%3A" + securityToken +
                "&redirect_uri=" + callbackUriEncoded +
                "&client_id=726398493120-51ed3jodt4omlkba1go3ppfv13uu37au.apps.googleusercontent.com";
        
        System.out.println(Constants.BLUE + "If the browser has not opened, please use this link to sign in: " + Constants.RESET + oauth2RequestUri);

        try {
            java.awt.Desktop.getDesktop().browse(new URI(oauth2RequestUri));
        } catch(URISyntaxException e) {
            // --
        } catch (IOException e) {
            // --
        }
        
        // wait for oauth2 callback
        var socket = server.accept();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        var callbackRoute = in.readLine();

        String errorMessage = "";

        String[] queryParams = new String[]{};
        if (callbackRoute != null) {
            var splits = callbackRoute.split(" ");
            if (splits.length >= 2) {
                queryParams = splits[1].split("&");
            } else {
                errorMessage = "Invalid request.";
            }
        } else {
            errorMessage = "Empty request.";
        }

        boolean foundSecurityToken = false;
        String code = null;
        for (int i = 0; i < queryParams.length; i++) {
            if (queryParams[i].equals("/?state=fupboard%3Asecurity_token%3A" + securityToken)) {
                foundSecurityToken = true;
            }
            if (queryParams[i].startsWith("code=")) {
                code = URLDecoder.decode(queryParams[i].substring(5), Charset.defaultCharset());
                break;
            }
        }
        
        if (code == null) {
            errorMessage = "Code not found.";
        }
        if (!foundSecurityToken) {
            errorMessage = "Missing/incorrect security token.";
        }

        // Make request to F-Up Board API with auth code to receive JWT
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(new URI(Constants.BASE_URL+"/v1/jwt"))
            .header("content-type", "application/x-www-form-urlencoded")
            .POST(BodyPublishers.ofString("code=" + URLEncoder.encode(code, Charset.defaultCharset()) + "&uri=" + callbackUriEncoded))
            .build();

        var response = client.send(request, BodyHandlers.ofString());
        var responseBody = response.body();
        var output = socket.getOutputStream();

        var objMapper = new ObjectMapper();
        AuthResponse authResponse = null;
        try {
            authResponse = objMapper.readValue(responseBody, AuthResponse.class);
        } catch (JacksonException e) {
            errorMessage = "Failed to parse response from Google.";
        }

        authToken = authResponse.getIdToken();

        try {
            currentUserId = UserService.getUserMe().getUserId();
        } catch (NavStateException e) {
            errorMessage = "Failed to get user information from server.";
        }

        // Generate response for user.
        InputStream responseStream;
        if (errorMessage.length() > 0) {
            responseStream = Main.class.getResourceAsStream("/authfailure.html");
            System.out.println(Constants.RED + "Signed in failed." + Constants.RESET);
        } else {
            responseStream = Main.class.getResourceAsStream("/authsuccess.html");
            System.out.println(Constants.GREEN + "Succesfully signed in!" + Constants.RESET);
        }
        var responseReader = new BufferedReader(new InputStreamReader(responseStream));

        var responseContent = "";
        
        String line;
        while ((line = responseReader.readLine()) != null) {
            responseContent += line;
        }

        responseContent = responseContent.replaceAll("\\{\\{errorMessage\\}\\}", errorMessage);

        var responseCode = errorMessage.length() > 0 ? "400 BAD REQUEST" : "200 OK";
        var responseHeaders = "HTTP/1.1 " + responseCode + "\r\n" +
                        "Server: F-Up Board OAuth2 Callback Server\r\n" +
                        "Content-Length: " + responseContent.length() + "\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Connection: Closed\r\n\r\n";
        output.write((responseHeaders + responseContent).getBytes());
        socket.close();
        server.close();

        if (errorMessage.length() > 0) {
            throw new AuthenticationException(errorMessage);
        }
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }
}
