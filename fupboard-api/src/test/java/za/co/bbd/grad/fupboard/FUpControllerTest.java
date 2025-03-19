package za.co.bbd.grad.fupboard;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
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

import za.co.bbd.grad.fupboard.api.dbobjects.FUp;
import za.co.bbd.grad.fupboard.api.dbobjects.Project;
import za.co.bbd.grad.fupboard.api.dbobjects.User;
import za.co.bbd.grad.fupboard.api.models.CreateFUpRequest;
import za.co.bbd.grad.fupboard.api.models.UpdateFUpRequest;
import za.co.bbd.grad.fupboard.api.models.UpdateProjectRequest;
import za.co.bbd.grad.fupboard.api.services.FUpService;
import za.co.bbd.grad.fupboard.api.services.ProjectService;
import za.co.bbd.grad.fupboard.api.services.UserService;

import org.springframework.security.oauth2.jwt.Jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest(classes = za.co.bbd.grad.fupboard.api.FUpboardApplication.class)
@AutoConfigureMockMvc
class FUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FUpService fUpService;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User user;
    private Project project;
    private FUp fUp;


    // @BeforeEach
    // void setUp() {
    //     user = new User();
    //     project = new Project("Test Project", user);

    //     fUp = new FUp(project, "Test FUp", "Description of FUp");
    //     fUp.setVotes(new ArrayList<>()); // Ensure it's not null in test mock
    // }

    @BeforeEach
void setUp() {
    user = new User();
    user.setUserId(1); // Assign a user ID

    project = new Project("Test Project", user);
    project.setProjectId(1); // Explicitly set project ID

    fUp = new FUp(project, "Test FUp", "Description of FUp");
    fUp.setfUpId(1); // Assign an ID to the FUp
    fUp.setVotes(new ArrayList<>()); // Ensure votes are initialized

    // Mock return values
    when(userService.getUserByJwt(any())).thenReturn(Optional.of(user));
    when(projectService.getProjectById(1)).thenReturn(Optional.of(project));
    when(projectService.allowedToWriteProject(project, user)).thenReturn(true);
    when(fUpService.getFUpById(1)).thenReturn(Optional.of(fUp));
    when(fUpService.allowedToDeleteFUp(fUp, user)).thenReturn(true);


    // Mock return values
 
    
    
    // Ensure the user has permission to edit
    when(fUpService.allowedToWriteFUp(fUp, user)).thenReturn(true); 
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
    void testGetFUps_Success() throws Exception {
        when(userService.getUserByJwt(any())).thenReturn(Optional.of(user));
        when(projectService.getProjectById(anyInt())).thenReturn(Optional.of(project));
        when(projectService.allowedToReadProject(any(), any())).thenReturn(true);
        when(fUpService.getFUpById(anyInt())).thenReturn(Optional.of(fUp));

        mockMvc.perform(get("/v1/projects/1/fups")
                .with(jwt().jwt(createMockJwt())))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateFUp_Success() throws Exception {
        CreateFUpRequest request = new CreateFUpRequest("New FUp", "New Description");

        when(userService.getUserByJwt(any())).thenReturn(Optional.of(user));
        when(projectService.getProjectById(anyInt())).thenReturn(Optional.of(project));
        when(projectService.allowedToWriteProject(any(), any())).thenReturn(true);
        when(fUpService.saveFUp(any(FUp.class))).thenReturn(fUp);

        mockMvc.perform(post("/v1/projects/1/fups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().jwt(createMockJwt())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fUpName").value("Test FUp"));

        verify(fUpService).saveFUp(any(FUp.class));
    }

    @Test
    void testPatchFUp_Success() throws Exception {
        UpdateFUpRequest request = new UpdateFUpRequest("Updated FUp", "Updated Description");

        when(userService.getUserByJwt(any())).thenReturn(Optional.of(user));
        when(projectService.getProjectById(anyInt())).thenReturn(Optional.of(project));
        when(projectService.allowedToWriteProject(any(), any())).thenReturn(true);
        when(fUpService.getFUpById(anyInt())).thenReturn(Optional.of(fUp));
        when(fUpService.saveFUp(any(FUp.class))).thenReturn(fUp);

        mockMvc.perform(patch("/v1/projects/1/fups/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().jwt(createMockJwt())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fUpName").value("Updated FUp"));


        verify(fUpService).saveFUp(any(FUp.class));

    //     UpdateFUpRequest request = new UpdateFUpRequest("Updated FUp", "Updated Description");

    // mockMvc.perform(patch("/v1/projects/1/fups/1")
    //         .contentType(MediaType.APPLICATION_JSON)
    //         .content(objectMapper.writeValueAsString(request)) // Ensure request body is included
    //         .with(jwt().jwt(createMockJwt())))
    //         .andExpect(status().isOk()); // Expect 200 OK

    // verify(fUpService).updateFUp(eq(fUp), any(UpdateFUpRequest.class));
    }

    @Test
    void testDeleteFUp_Success() throws Exception {
        // when(userService.getUserByJwt(any())).thenReturn(Optional.of(user));
        // when(projectService.getProjectById(anyInt())).thenReturn(Optional.of(project));
        // when(projectService.allowedToWriteProject(any(), any())).thenReturn(true);
        // when(fUpService.getFUpById(anyInt())).thenReturn(Optional.of(fUp));

        // mockMvc.perform(delete("/v1/projects/1/fups/1")
        //         .with(jwt().jwt(createMockJwt())))
        //         .andExpect(status().isOk());

        // verify(fUpService).deleteFUp(any(FUp.class));

        //break---------

        //   UpdateProjectRequest request = new UpdateProjectRequest("Deleted Project"); // Required request body
        // when(userService.getUserByJwt(any())).thenReturn(Optional.of(user));
        // when(projectService.getProjectById(1)).thenReturn(Optional.of(project));
        // when(projectService.allowedToDeleteProject(project, user)).thenReturn(true);

        // mockMvc.perform(delete("/v1/projects/1")
        //         .contentType(MediaType.APPLICATION_JSON)
        //         .content(objectMapper.writeValueAsString(request)) // Include request body
        //         .with(jwt().jwt(createMockJwt())))
        //         .andExpect(status().is2xxSuccessful());

        // verify(projectService).deleteProject(project);

        // UpdateFUpRequest request = new UpdateFUpRequest("Deleted FUp", "Some description"); // Required request body

        // when(userService.getUserByJwt(any())).thenReturn(Optional.of(user));
        // when(projectService.getProjectById(1)).thenReturn(Optional.of(project));
        // when(projectService.allowedToWriteProject(project, user)).thenReturn(true); // Ensure the user has write permissions
        // when(fUpService.getFUpById(1)).thenReturn(Optional.of(fUp));
        // when(fUpService.allowedToDeleteFUp(fUp, user)).thenReturn(true);
    
        // mockMvc.perform(delete("/v1/projects/1/fups/1")
        //         .contentType(MediaType.APPLICATION_JSON)
        //         .content(objectMapper.writeValueAsString(request)) // Include request body
        //         .with(jwt().jwt(createMockJwt())))
        //         .andExpect(status().is2xxSuccessful());
    
        // verify(fUpService).deleteFUp(fUp);

       
    mockMvc.perform(delete("/v1/projects/1/fups/1")
            .contentType(MediaType.APPLICATION_JSON)
            .with(jwt().jwt(createMockJwt())))
            .andExpect(status().isNoContent()); // Expect 204 No Content

    verify(fUpService).deleteFUp(fUp);


    }


    

}
