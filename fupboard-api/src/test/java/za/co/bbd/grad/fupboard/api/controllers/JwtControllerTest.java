package za.co.bbd.grad.fupboard.api.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import za.co.bbd.grad.fupboard.api.models.JwtRequest;

@ExtendWith(MockitoExtension.class)
class JwtControllerTest {

    @InjectMocks
    private JwtController jwtController;

    @Mock
    private Jwt jwt;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtController, "clientId", "testClientId");
        ReflectionTestUtils.setField(jwtController, "clientSecret", "testClientSecret");
        ReflectionTestUtils.setField(jwtController, "tokenUri", "http://test.token.uri");
    }

    @Test
    void getClaims_ReturnsClaimsFromJwt() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("claim1", "value1");
        claims.put("claim2", "value2");

        when(jwt.getClaims()).thenReturn(claims);

        Map<String, Object> result = jwtController.getClaims(jwt);

        assertEquals(claims, result);
    }

    @Test
    void postJwt_SuccessfulResponse_ReturnsResponseBody() throws URISyntaxException, IOException, InterruptedException {
        JwtRequest jwtRequest = new JwtRequest();
        jwtRequest.setCode("testCode");
        jwtRequest.setUri("http://test.redirect.uri");

        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("testResponseBody");

        try (MockedStatic<HttpClient> mockedStatic = Mockito.mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(httpClient);
            when(httpClient.send(Mockito.any(HttpRequest.class), Mockito.<java.net.http.HttpResponse.BodyHandler<String>>any())).thenReturn(httpResponse);

            String result = jwtController.postJwt(jwtRequest);

            assertEquals("testResponseBody", result);
        }
    }

    @Test
    void postJwt_UnsuccessfulResponse_ThrowsResponseStatusException() throws URISyntaxException, IOException, InterruptedException {
        JwtRequest jwtRequest = new JwtRequest();
        jwtRequest.setCode("testCode");
        jwtRequest.setUri("http://test.redirect.uri");

        when(httpResponse.statusCode()).thenReturn(400);

        try (MockedStatic<HttpClient> mockedStatic = Mockito.mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(httpClient);
            when(httpClient.send(Mockito.any(HttpRequest.class), Mockito.<java.net.http.HttpResponse.BodyHandler<String>>any())).thenReturn(httpResponse);

            assertThrows(ResponseStatusException.class, () -> jwtController.postJwt(jwtRequest));
        }
    }

    @Test
    void postJwt_URISyntaxException_PropagatesException() throws URISyntaxException, IOException, InterruptedException {
        JwtRequest jwtRequest = new JwtRequest();
        jwtRequest.setCode("testCode");
        jwtRequest.setUri("invalid uri");

        try (MockedStatic<HttpClient> mockedStatic = Mockito.mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(httpClient);

            assertThrows(URISyntaxException.class, () -> jwtController.postJwt(jwtRequest));
        }
    }

    @Test
    void postJwt_IOException_PropagatesException() throws URISyntaxException, IOException, InterruptedException {
        JwtRequest jwtRequest = new JwtRequest();
        jwtRequest.setCode("testCode");
        jwtRequest.setUri("http://test.redirect.uri");

        try (MockedStatic<HttpClient> mockedStatic = Mockito.mockStatic(HttpClient.class)) {
            mockedStatic.when(HttpClient::newHttpClient).thenReturn(httpClient);
            when(httpClient.send(Mockito.any(HttpRequest.class), Mockito.<java.net.http.HttpResponse.BodyHandler<String>>any())).thenThrow(new IOException("test io exception"));

            assertThrows(IOException.class, () -> jwtController.postJwt(jwtRequest));
        }
    }
}