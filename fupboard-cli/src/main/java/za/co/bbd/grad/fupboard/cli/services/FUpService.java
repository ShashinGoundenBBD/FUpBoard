package za.co.bbd.grad.fupboard.cli.services;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.HttpUtil;
import za.co.bbd.grad.fupboard.cli.common.NavStateException;
import za.co.bbd.grad.fupboard.cli.models.CreateFUpRequest;
import za.co.bbd.grad.fupboard.cli.models.FUp;
import za.co.bbd.grad.fupboard.cli.models.Leaderboard;
import za.co.bbd.grad.fupboard.cli.models.UpdateFUpRequest;

public class FUpService {
    public static List<FUp> getFUps(int projectId) throws NavStateException {
        var fUps = HttpUtil.get(Constants.BASE_URL + "/v1/projects/" + projectId + "/fups", new TypeReference<List<FUp>>() {});
        return fUps;
    }
    
    public static Leaderboard getFUpLeaderboard(int projectId, int fUpId) throws NavStateException {
        var leaderboard = HttpUtil.get(Constants.BASE_URL + "/v1/projects/" + projectId + "/fups/" + fUpId + "/leaderboard", new TypeReference<Leaderboard>() {});
        return leaderboard;
    }

    public static void deleteFUp(int projectId, int fUpId) throws NavStateException {
        HttpUtil.delete(Constants.BASE_URL + "/v1/projects/" + projectId + "/fups/" + fUpId, null);
    }

    public static FUp updateFUp(int projectId, int fUpId, String newName, String newDescription) throws NavStateException {
        var request = new UpdateFUpRequest(newName, newDescription);
        var fUp = HttpUtil.patch(Constants.BASE_URL + "/v1/projects/" + projectId + "/fups/" + fUpId, request, new TypeReference<FUp>() {});
        return fUp;
    }

    public static FUp createFUp(int projectId, String name, String description) throws NavStateException {
        var request = new CreateFUpRequest(name, description);
        var fUp = HttpUtil.post(Constants.BASE_URL + "/v1/projects/" + projectId + "/fups", request, new TypeReference<FUp>() {});
        return fUp;
    }
}
