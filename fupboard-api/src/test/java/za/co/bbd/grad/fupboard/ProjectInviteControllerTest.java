package za.co.bbd.grad.fupboard;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
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

import za.co.bbd.grad.fupboard.api.dbobjects.Project;
import za.co.bbd.grad.fupboard.api.dbobjects.ProjectInvite;
import za.co.bbd.grad.fupboard.api.dbobjects.User;
import za.co.bbd.grad.fupboard.api.models.CreateProjectInviteRequest;
import za.co.bbd.grad.fupboard.api.models.UpdateProjectInviteRequest;
import za.co.bbd.grad.fupboard.api.services.ProjectInviteService;
import za.co.bbd.grad.fupboard.api.services.ProjectService;
import za.co.bbd.grad.fupboard.api.services.UserService;

import org.springframework.security.oauth2.jwt.Jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;


@SpringBootTest(classes = za.co.bbd.grad.fupboard.api.FUpboardApplication.class)
@AutoConfigureMockMvc
class ProjectInviteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectInviteService projectInviteService;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User requester;
    private User invitee;
    private Project project;
    private ProjectInvite projectInvite;

    @BeforeEach
    void setUp() {
        requester = new User();
        invitee = new User();
        project = new Project("Test Project", requester);
        projectInvite = new ProjectInvite(project, invitee, false);
    }

    private Jwt createMockJwt() {
        return Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("sub", "testUser")
                .claim("scope", "read write")
                .build();
    }

    @Test
    void testGetProjectInvites_Success() throws Exception {
        when(userService.getUserByJwt(any())).thenReturn(Optional.of(requester));
        when(projectService.getProjectById(1)).thenReturn(Optional.of(project));
        when(projectService.allowedToReadProject(project, requester)).thenReturn(true);
        when(projectInviteService.getProjectInviteById(1)).thenReturn(Optional.of(projectInvite));

        mockMvc.perform(get("/v1/projects/1/invites")
                .with(jwt().jwt(createMockJwt())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(projectService).allowedToReadProject(project, requester);
    }

    @Test
    void testCreateProjectInvite_Success() throws Exception {
        CreateProjectInviteRequest request = new CreateProjectInviteRequest("testUser");

        when(userService.getUserByJwt(any())).thenReturn(Optional.of(requester));
        when(projectService.getProjectById(1)).thenReturn(Optional.of(project));
        when(userService.getUserByUsername(any())).thenReturn(Optional.of(invitee));
        when(projectInviteService.saveProjectInvite(any(ProjectInvite.class))).thenReturn(projectInvite);

        mockMvc.perform(post("/v1/projects/1/invites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().jwt(createMockJwt())))
                .andExpect(status().isOk());

        verify(projectInviteService).saveProjectInvite(any(ProjectInvite.class));
    }

    @Test
    void testPatchProjectInvite_Success() throws Exception {
        UpdateProjectInviteRequest request = new UpdateProjectInviteRequest(true);

        when(userService.getUserByJwt(any())).thenReturn(Optional.of(invitee));
        when(projectInviteService.getProjectInviteById(1)).thenReturn(Optional.of(projectInvite));
        when(projectInviteService.saveProjectInvite(any(ProjectInvite.class))).thenReturn(projectInvite);

        mockMvc.perform(patch("/v1/projects/1/invites/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().jwt(createMockJwt())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accepted").value(true));

        verify(projectInviteService).saveProjectInvite(any(ProjectInvite.class));
    }

    @Test
    void testDeleteProjectInvite_Success() throws Exception {
        when(userService.getUserByJwt(any())).thenReturn(Optional.of(requester));
        when(projectService.getProjectById(1)).thenReturn(Optional.of(project));
        when(projectInviteService.getProjectInviteById(1)).thenReturn(Optional.of(projectInvite));

        mockMvc.perform(delete("/v1/projects/1/invites/1")
                .with(jwt().jwt(createMockJwt())))
                .andExpect(status().isOk());

        verify(projectInviteService).deleteProjectInvite(projectInvite);
    }
}
