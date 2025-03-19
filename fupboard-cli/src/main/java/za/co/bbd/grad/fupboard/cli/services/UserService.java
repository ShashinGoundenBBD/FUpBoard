package za.co.bbd.grad.fupboard.cli.services;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.HttpUtil;
import za.co.bbd.grad.fupboard.cli.common.NavStateException;
import za.co.bbd.grad.fupboard.cli.models.Invite;
import za.co.bbd.grad.fupboard.cli.models.UpdateUserRequest;
import za.co.bbd.grad.fupboard.cli.models.User;

public class UserService {
    public static User getUserMe() throws NavStateException {
        var user = HttpUtil.get(Constants.BASE_URL + "/v1/users/me", new TypeReference<User>() {});
        return user;
    }
    
    public static User updateUserMe(String username, String email) throws NavStateException {
        var request = new UpdateUserRequest(username, email);
        var user = HttpUtil.patch(Constants.BASE_URL + "/v1/users/me", request, new TypeReference<User>() {});
        return user;
    }

    public static List<Invite> getMyInvites() throws NavStateException {
        var invites = HttpUtil.get(Constants.BASE_URL + "/v1/users/me/invites", new TypeReference<List<Invite>>() {});
        return invites;
    }
}
