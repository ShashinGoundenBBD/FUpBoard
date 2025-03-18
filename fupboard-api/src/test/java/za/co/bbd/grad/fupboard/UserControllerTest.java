package za.co.bbd.grad.fupboard;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;

import za.co.bbd.grad.fupboard.api.dbobjects.User;
import za.co.bbd.grad.fupboard.api.models.UserUpdateRequest;
import za.co.bbd.grad.fupboard.api.services.UserService;

import org.springframework.security.oauth2.jwt.Jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest(classes = za.co.bbd.grad.fupboard.api.FUpboardApplication.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testUser");
    }

    private Jwt createMockJwt() {
        return Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("sub", "testUser")
                .claim("email", "test@example.com")
                .claim("email_verified", true)
                .build();
    }

    @Test
    void testGetUserMe_Success() throws Exception {
        when(userService.getUserByJwt(any())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/v1/users/me")
                .with(jwt().jwt(createMockJwt())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testUser"));

        verify(userService).getUserByJwt(any());
    }

    @Test
    void testUpdateUserMe_Success() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest("newuser@example.com", "newUsername");

        when(userService.getUserByJwt(any())).thenReturn(Optional.of(user));
        when(userService.saveUser(any(User.class))).thenReturn(user);

        mockMvc.perform(patch("/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .with(jwt().jwt(createMockJwt())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.username").value("newUsername"));

        verify(userService).saveUser(any(User.class));
    }

    @Test
    void testUpdateUserMe_EmailAlreadyExists() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest("duplicate@example.com", "newUsername");

        when(userService.getUserByJwt(any())).thenReturn(Optional.of(user));
        doThrow(new org.springframework.dao.DataIntegrityViolationException("Email already exists"))
                .when(userService).saveUser(any(User.class));

        mockMvc.perform(patch("/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .with(jwt().jwt(createMockJwt())))
                .andExpect(status().isBadRequest());

        verify(userService).saveUser(any(User.class));
    }
}

