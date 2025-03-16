


package za.co.bbd.grad.fupboard;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;

import za.co.bbd.grad.fupboard.api.controllers.ProjectController;
import za.co.bbd.grad.fupboard.api.dbobjects.Project;
import za.co.bbd.grad.fupboard.api.models.CreateProjectRequest;
import za.co.bbd.grad.fupboard.api.models.UpdateProjectRequest;
import za.co.bbd.grad.fupboard.api.services.ProjectService;
import za.co.bbd.grad.fupboard.api.services.UserService;

import org.springframework.security.oauth2.jwt.Jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest(classes = za.co.bbd.grad.fupboard.api.FUpboardApplication.class)
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Project project;
    private za.co.bbd.grad.fupboard.api.dbobjects.User user;

    @BeforeEach
    void setUp() {
        user = new za.co.bbd.grad.fupboard.api.dbobjects.User();
        project = new Project("Test Project", user);
    }

    private Jwt createMockJwt() {
        return Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("sub", "testUser")
                .claim("scope", "read write")
                .build();
    }

    @Test
    void testGetProjects_Success() throws Exception {
        when(userService.getUserByJwt(any())).thenReturn(Optional.of(user));
        when(projectService.getProjectsForOwner(user)).thenReturn(List.of(project));

        mockMvc.perform(get("/v1/projects")
                .with(jwt().jwt(createMockJwt())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectName").value("Test Project"));

        verify(projectService).getProjectsForOwner(user);
    }

  
    @Test
void testCreateProject_Success() throws Exception {
    CreateProjectRequest request = new CreateProjectRequest("Test Project"); // Use constructor
    when(userService.getUserByJwt(any())).thenReturn(Optional.of(user));
    when(projectService.saveProject(any(Project.class))).thenReturn(project);

    mockMvc.perform(post("/v1/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .with(jwt().jwt(createMockJwt())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projectName").value("Test Project"));

    verify(projectService).saveProject(any(Project.class));
}

    @Test
    void testCreateProject_BadRequest() throws Exception {
        // Request without a name (invalid)
        CreateProjectRequest request = new CreateProjectRequest("");

        when(userService.getUserByJwt(any())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().jwt(createMockJwt())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetProject_Success() throws Exception {
        when(userService.getUserByJwt(any())).thenReturn(Optional.of(user));
        when(projectService.getProjectById(1)).thenReturn(Optional.of(project));
        when(projectService.allowedToReadProject(project, user)).thenReturn(true);

        mockMvc.perform(get("/v1/projects/1")
                .with(jwt().jwt(createMockJwt())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectName").value("Test Project"));

        verify(projectService).getProjectById(1);
    }

    @Test
    void testGetProject_NotFound() throws Exception {
        when(userService.getUserByJwt(any())).thenReturn(Optional.of(user));
        when(projectService.getProjectById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/projects/1")
                .with(jwt().jwt(createMockJwt())))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPatchProject_Success() throws Exception {
        // Constructing update request without setter method
        UpdateProjectRequest request = new UpdateProjectRequest("Updated Project");

        when(userService.getUserByJwt(any())).thenReturn(Optional.of(user));
        when(projectService.getProjectById(1)).thenReturn(Optional.of(project));
        when(projectService.allowedToWriteProject(project, user)).thenReturn(true);
        when(projectService.saveProject(any(Project.class))).thenReturn(project);

        mockMvc.perform(patch("/v1/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().jwt(createMockJwt())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectName").value("Updated Project"));

        verify(projectService).saveProject(any(Project.class));
    }

    @Test
    void testPatchProject_NotFound() throws Exception {
        UpdateProjectRequest request = new UpdateProjectRequest("Updated Project");

        when(userService.getUserByJwt(any())).thenReturn(Optional.of(user));
        when(projectService.getProjectById(1)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/v1/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().jwt(createMockJwt())))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProject_Success() throws Exception {
        UpdateProjectRequest request = new UpdateProjectRequest("Deleted Project"); // Required request body
        when(userService.getUserByJwt(any())).thenReturn(Optional.of(user));
        when(projectService.getProjectById(1)).thenReturn(Optional.of(project));
        when(projectService.allowedToDeleteProject(project, user)).thenReturn(true);

        mockMvc.perform(delete("/v1/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)) // Include request body
                .with(jwt().jwt(createMockJwt())))
                .andExpect(status().isOk());

        verify(projectService).deleteProject(project);
    }

    @Test
    void testDeleteProject_NotFound() throws Exception {
        UpdateProjectRequest request = new UpdateProjectRequest("Deleted Project"); // Required request body
        when(userService.getUserByJwt(any())).thenReturn(Optional.of(user));
        when(projectService.getProjectById(1)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/v1/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)) // Include request body
                .with(jwt().jwt(createMockJwt())))
                .andExpect(status().isNotFound());
    }
}
