
// package za.co.bbd.grad.fupboard;

package za.co.bbd.grad.fupboard;

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

import za.co.bbd.grad.fupboard.api.controllers.JwtController;
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

// import static org.mockito.Mockito.*;
// import static org.junit.jupiter.api.Assertions.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// import java.io.IOException;
// import java.net.URISyntaxException;
// import java.net.http.HttpClient;
// import java.net.http.HttpResponse;
// import java.security.Principal;
// import java.util.Map;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.security.oauth2.jwt.Jwt;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.beans.factory.annotation.Autowired;

// import za.co.bbd.grad.fupboard.api.controllers.JwtController;
// import za.co.bbd.grad.fupboard.api.models.JwtRequest;


// @SpringBootTest(classes = za.co.bbd.grad.fupboard.api.FUpboardApplication.class)
// @AutoConfigureMockMvc
// class JwtControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @InjectMocks
//     private JwtController jwtController;

//     @Mock
//     private HttpClient httpClient;

//     @Mock
//     private Jwt jwtMock;

//     @Value("${fupboard.oauth2.client-id}")
//     private String clientId;

//     @Value("${fupboard.oauth2.client-secret}")
//     private String clientSecret;

//     @Value("${fupboard.oauth2.token-uri}")
//     private String tokenUri;

//     @BeforeEach
//     void setUp() {
//         when(jwtMock.getClaims()).thenReturn(Map.of("sub", "testUser", "role", "USER"));
//     }

//     // ✅ Test getClaims() - Ensure it returns JWT claims
//     @Test
//     void testGetClaims_ReturnsCorrectClaims() throws Exception {
//         mockMvc.perform(get("/v1/jwt/claims").principal((Principal) jwtMock))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.sub").value("testUser"))
//                 .andExpect(jsonPath("$.role").value("USER"));
//     }

//     // ✅ Test postJwt() - Simulate successful token exchange
//     @Test
//     void testPostJwt_Success() throws Exception {
//         JwtRequest jwtRequest = new JwtRequest("auth-code-123", "https://redirect.uri");

//         String mockResponseBody = "{ \"access_token\": \"mocked_token\", \"token_type\": \"Bearer\" }";

//         // Mock HTTP response
//         HttpResponse<Object> mockResponse = mock(HttpResponse.class);
//         when(mockResponse.statusCode()).thenReturn(200);
//         when(mockResponse.body()).thenReturn(mockResponseBody);
//         when(httpClient.send(any(), any())).thenReturn(mockResponse);

//         // Perform request
//         mockMvc.perform(post("/v1/jwt")
//                 .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                 .param("code", jwtRequest.getCode())
//                 .param("uri", jwtRequest.getUri()))
//                 .andExpect(status().isOk())
//                 .andExpect(content().json(mockResponseBody));
//     }

//     // ✅ Test postJwt() - Handle failure scenario
//     @Test
//     void testPostJwt_Failure() throws Exception {
//         JwtRequest jwtRequest = new JwtRequest("invalid-code", "https://redirect.uri");

//         // Mock failed HTTP response (e.g., 400 Bad Request)
//         HttpResponse<Object> mockResponse = mock(HttpResponse.class);
//         when(mockResponse.statusCode()).thenReturn(400);
//         when(httpClient.send(any(), any())).thenReturn(mockResponse);

//         // Expect error status from the API
//         mockMvc.perform(post("/v1/jwt")
//                 .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                 .param("code", jwtRequest.getCode())
//                 .param("uri", jwtRequest.getUri()))
//                 .andExpect(status().isBadRequest());
//     }
// }
