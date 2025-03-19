package za.co.bbd.grad.fupboard.cli.services;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.HttpUtil;
import za.co.bbd.grad.fupboard.cli.common.NavStateException;
import za.co.bbd.grad.fupboard.cli.models.CreateVoteRequest;
import za.co.bbd.grad.fupboard.cli.models.UpdateVoteRequest;
import za.co.bbd.grad.fupboard.cli.models.Vote;

public class VoteService {
    public static List<Vote> getVotes(int projectId, int fUpId) throws NavStateException {
        var votes = HttpUtil.get(Constants.BASE_URL + "/v1/projects/" + projectId + "/fups/" + fUpId + "/votes", new TypeReference<List<Vote>>() {});
        return votes;
    }

    public static Vote updateVote(int projectId, int fUpId, int voteId, int newScore) throws NavStateException {
        var request = new UpdateVoteRequest(newScore);
        var vote = HttpUtil.patch(Constants.BASE_URL + "/v1/projects/" + projectId + "/fups/" + fUpId + "/votes/" + voteId, request, new TypeReference<Vote>() {});
        return vote;
    }

    public static void deleteVote(int projectId, int fUpId, int voteId) throws NavStateException {
        HttpUtil.delete(Constants.BASE_URL + "/v1/projects/" + projectId + "/fups/" + fUpId + "/votes/" + voteId, null);
    }

    public static Vote createVote(int projectId, int fUpId, String accused, int score) throws NavStateException {
        var request = new CreateVoteRequest(accused, score);
        var vote = HttpUtil.post(Constants.BASE_URL + "/v1/projects/" + projectId + "/fups/" + fUpId + "/votes", request, new TypeReference<Vote>() {});
        return vote;
    }
}
