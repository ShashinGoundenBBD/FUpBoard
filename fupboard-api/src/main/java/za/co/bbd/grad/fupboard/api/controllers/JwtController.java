package za.co.bbd.grad.fupboard.api.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import za.co.bbd.grad.fupboard.api.models.JwtRequest;

@RestController
public class JwtController {
    @Value("${fupboard.oauth2.client-id}")
    private String clientId;
    @Value("${fupboard.oauth2.client-secret}")
    private String clientSecret;
    @Value("${fupboard.oauth2.token-uri}")
    private String tokenUri;
    
    @GetMapping("/v1/jwt/claims")
    public Map<String, Object> getClaims(@AuthenticationPrincipal Jwt jwt) {
        return jwt.getClaims();
    }

    @PostMapping(
        value = "/v1/jwt",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )

    public String postJwt(JwtRequest req) throws URISyntaxException, IOException, InterruptedException {
        var httpClient = HttpClient.newHttpClient();

        var encRequestCode = URLEncoder.encode(req.getCode(), Charset.defaultCharset());
        var encRequestUri = URLEncoder.encode(req.getUri(), Charset.defaultCharset());
        var encClientId = URLEncoder.encode(clientId, Charset.defaultCharset());
        var encClientSecret = URLEncoder.encode(clientSecret, Charset.defaultCharset());

        HttpRequest request = HttpRequest.newBuilder(new URI(tokenUri))
            .header("content-type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .POST(BodyPublishers.ofString(
                "code=" + encRequestCode +
                "&redirect_uri=" + encRequestUri +
                "&grant_type=authorization_code" +
                "&client_id=" +encClientId +
                "&client_secret=" + encClientSecret
            ))
            .build();
        
		var resp = httpClient.send(request, BodyHandlers.ofString());
		
		var status = HttpStatus.valueOf(resp.statusCode());
		
		if (!status.is2xxSuccessful()) {
			throw new ResponseStatusException(status);
		}
        
		return resp.body();
    }
}
