package za.co.bbd.grad.fupboard.api.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;
import za.co.bbd.grad.fupboard.api.dbobjects.FUp;
import za.co.bbd.grad.fupboard.api.dbobjects.Project;
import za.co.bbd.grad.fupboard.api.dbobjects.User;
import za.co.bbd.grad.fupboard.api.dbobjects.Vote;
import za.co.bbd.grad.fupboard.api.models.CreateVoteRequest;
import za.co.bbd.grad.fupboard.api.models.UpdateVoteRequest;
import za.co.bbd.grad.fupboard.api.services.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteControllerTest {
    @Mock
    private VoteService voteService;

    @Mock
    private FUpController fUpController;

    @Mock
    private FUpService fUpService;

    @Mock
    private ProjectService projectService;

    @Mock
    private UserService userService;

    @InjectMocks
    private VoteController voteController;

    private Jwt jwt;
    private User user;
    private Project project;
    private FUp fUp;
    private Vote vote;

    @BeforeEach
    void setUp() {
        jwt = mock(Jwt.class);
        user = new User();
        user.setUserId(1);
        user.setUsername("testUser");
        project = new Project();
        project.setProjectId(1);
        project.setOwner(user);
        fUp = mock(FUp.class); // Mock the FUp object
        fUp.setfUpId(1);
        vote = new Vote(user, user, fUp, 5);
        vote.setVoteId(1);
    }

    @Test
    void getVotes_Success() throws Exception {
        when(userService.getUserByJwt(jwt)).thenReturn(Optional.of(user));
        when(projectService.getProjectById(1)).thenReturn(Optional.of(project));
        when(projectService.allowedToReadProject(project, user)).thenReturn(true);
        when(fUpService.getFUpById(1)).thenReturn(Optional.of(fUp));
        when(fUp.getVotes()).thenReturn(List.of(vote));

        List<Vote> result = voteController.getVotes(jwt, 1, 1);

        assertEquals(1, result.size());

        assertEquals(vote, result.getFirst());
    }

    @Test
    void getVotes_ProjectNotFound() {
        when(userService.getUserByJwt(jwt)).thenReturn(Optional.of(user));
        when(projectService.getProjectById(1)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> voteController.getVotes(jwt, 1, 1));
    }

    @Test
    void getVotes_Forbidden() {
        when(userService.getUserByJwt(jwt)).thenReturn(Optional.of(user));
        when(projectService.getProjectById(1)).thenReturn(Optional.of(project));
        when(projectService.allowedToReadProject(project, user)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> voteController.getVotes(jwt, 1, 1));
    }

    @Test
    void getVoteById_Success() throws Exception {
        when(userService.getUserByJwt(jwt)).thenReturn(Optional.of(user));
        when(projectService.getProjectById(1)).thenReturn(Optional.of(project));
        when(projectService.allowedToReadProject(project, user)).thenReturn(true);
        when(fUpService.getFUpById(1)).thenReturn(Optional.of(fUp));
        when(voteService.getVoteById(1)).thenReturn(Optional.of(vote));

        Vote result = voteController.getVotes(jwt, 1, 1, 1);

        assertEquals(vote, result);
    }

    @Test
    void deleteVote_Success_Reporter() throws Exception {
        when(userService.getUserByJwt(jwt)).thenReturn(Optional.of(user));
        when(projectService.getProjectById(1)).thenReturn(Optional.of(project));
        when(fUpService.getFUpById(1)).thenReturn(Optional.of(fUp));
        when(voteService.getVoteById(1)).thenReturn(Optional.of(vote));

        voteController.deleteVote(jwt, 1, 1, 1);

        verify(voteService, times(1)).deleteVote(vote);
    }

    @Test
    void deleteVote_Success_Owner() throws Exception {
        User owner = new User();
        owner.setUserId(2);
        Project ownerProject = new Project();
        ownerProject.setProjectId(1);
        ownerProject.setOwner(owner);

        when(userService.getUserByJwt(jwt)).thenReturn(Optional.of(owner));
        when(projectService.getProjectById(1)).thenReturn(Optional.of(ownerProject));
        when(fUpService.getFUpById(1)).thenReturn(Optional.of(fUp));
        when(voteService.getVoteById(1)).thenReturn(Optional.of(vote));

        voteController.deleteVote(jwt, 1, 1, 1);

        verify(voteService, times(1)).deleteVote(vote);
    }

    @Test
    void deleteVote_Forbidden_NotOwnerOrReporter() {
        User otherUser = new User();
        otherUser.setUserId(2);

        Vote otherVote = new Vote(otherUser, user, fUp, 5);
        otherVote.setVoteId(1);

        System.out.println("User ID: " + user.getUserId());
        System.out.println("Other User ID: " + otherUser.getUserId());
        System.out.println("Project Owner ID: " + project.getOwner().getUserId());
        System.out.println("Vote Reporter ID: " + otherVote.getReporter().getUserId());

        when(userService.getUserByJwt(jwt)).thenReturn(Optional.of(user));
        when(projectService.getProjectById(1)).thenReturn(Optional.of(project));
        when(fUpService.getFUpById(1)).thenReturn(Optional.of(fUp));
        when(voteService.getVoteById(1)).thenReturn(Optional.of(otherVote));

        assertThrows(ResponseStatusException.class, () -> voteController.deleteVote(jwt, 1, 1, 1));
        verify(voteService, times(0)).deleteVote(any(Vote.class));
    }
}