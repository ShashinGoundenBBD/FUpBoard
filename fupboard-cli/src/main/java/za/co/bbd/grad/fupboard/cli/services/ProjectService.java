package za.co.bbd.grad.fupboard.cli.services;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.HttpUtil;
import za.co.bbd.grad.fupboard.cli.common.NavStateException;
import za.co.bbd.grad.fupboard.cli.models.CreateProjectRequest;
import za.co.bbd.grad.fupboard.cli.models.Leaderboard;
import za.co.bbd.grad.fupboard.cli.models.Project;
import za.co.bbd.grad.fupboard.cli.models.UpdateProjectRequest;

public class ProjectService {
    public static List<Project> getProjects() throws NavStateException {
        var projects = HttpUtil.get(Constants.BASE_URL + "/v1/projects", new TypeReference<List<Project>>() {});
        return projects;
    }
    
    public static List<Project> getOwnedProjects() throws NavStateException {
        var projects = HttpUtil.get(Constants.BASE_URL + "/v1/users/me/projects", new TypeReference<List<Project>>() {});
        return projects;
    }
    
    public static Project getProject(int projectId) throws NavStateException {
        var project = HttpUtil.get(Constants.BASE_URL + "/v1/projects/" + projectId, new TypeReference<Project>() {});
        return project;
    }
    
    public static Leaderboard getProjectLeaderboard(int projectId) throws NavStateException {
        var leaderboard = HttpUtil.get(Constants.BASE_URL + "/v1/projects/" + projectId + "/leaderboard", new TypeReference<Leaderboard>() {});
        return leaderboard;
    }

    public static Project createProject(String name) throws NavStateException {
        var request = new CreateProjectRequest(name);
        var project = HttpUtil.post(Constants.BASE_URL + "/v1/projects", request, new TypeReference<Project>() {});
        return project;
    }

    public static Project renameProject(int projectId, String newName) throws NavStateException {
        var request = new UpdateProjectRequest(newName);
        var project = HttpUtil.patch(Constants.BASE_URL + "/v1/projects/" + projectId, request, new TypeReference<Project>() {});
        return project;
    }

    public static void deleteProject(int projectId) throws NavStateException {
        HttpUtil.delete(Constants.BASE_URL + "/v1/projects/" + projectId, null);
    }
}
