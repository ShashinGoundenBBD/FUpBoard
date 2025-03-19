package za.co.bbd.grad.fupboard.cli.services;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.HttpUtil;
import za.co.bbd.grad.fupboard.cli.common.NavStateException;
import za.co.bbd.grad.fupboard.cli.models.CreateInviteRequest;
import za.co.bbd.grad.fupboard.cli.models.Invite;
import za.co.bbd.grad.fupboard.cli.models.UpdateInviteRequest;

public class InviteService {
    public static List<Invite> getInvites(int projectId) throws NavStateException {
        var invites = HttpUtil.get(Constants.BASE_URL + "/v1/projects/" + projectId + "/invites", new TypeReference<List<Invite>>() {});
        return invites;
    }
    
    public static void deleteInvite(int projectId, int inviteId) throws NavStateException {
        HttpUtil.delete(Constants.BASE_URL + "/v1/projects/" + projectId + "/invites/" + inviteId, null);
    }

    public static Invite acceptInvite(int projectId, int inviteId) throws NavStateException {
        var request = new UpdateInviteRequest(true);
        var invite = HttpUtil.patch(Constants.BASE_URL + "/v1/projects/" + projectId + "/invites/" + inviteId, request, new TypeReference<Invite>() {});
        return invite;
    }

    public static Invite createInvite(int projectId, String username) throws NavStateException {
        var request = new CreateInviteRequest(username);
        var invite = HttpUtil.post(Constants.BASE_URL + "/v1/projects/" + projectId + "/invites", request, new TypeReference<Invite>() {});
        return invite;
    }
}
