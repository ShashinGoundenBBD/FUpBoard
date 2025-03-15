package za.co.bbd.grad.fupboard.cli;

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

import com.fasterxml.jackson.databind.ObjectMapper;

public class Authentication {
    private static final String fUpBoardApiBaseUrl = "http://localhost:8080";

    public static String performOAuth2Login() throws IOException, URISyntaxException, InterruptedException {
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
        
        System.out.println("Opening " + oauth2RequestUri);

        try {
            java.awt.Desktop.getDesktop().browse(new URI(oauth2RequestUri));
        } catch(URISyntaxException e) {
            // --
        } catch (IOException e) {
            // --
        }
        
        System.out.println("Navigate to above URL if browser has not opened.");
        
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
        var request = HttpRequest.newBuilder(new URI(fUpBoardApiBaseUrl+"/v1/jwt"))
            .header("content-type", "application/x-www-form-urlencoded")
            .POST(BodyPublishers.ofString("code=" + URLEncoder.encode(code, Charset.defaultCharset()) + "&uri=" + callbackUriEncoded))
            .build();

        var response = client.send(request, BodyHandlers.ofString());
        var responseBody = response.body();
        var output = socket.getOutputStream();

        // Generate response for user.
        InputStream responseStream;
        if (errorMessage.length() > 0) {
            responseStream = Main.class.getResourceAsStream("/authfailure.html");
        } else {
            responseStream = Main.class.getResourceAsStream("/authsuccess.html");
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

        var objMapper = new ObjectMapper();
        var authResponse = objMapper.readValue(responseBody, AuthResponse.class);

        return authResponse.getIdToken();
    }
}
