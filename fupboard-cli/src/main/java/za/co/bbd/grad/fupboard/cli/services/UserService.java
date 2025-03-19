package za.co.bbd.grad.fupboard.cli.services;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.HttpUtil;
import za.co.bbd.grad.fupboard.cli.models.InviteResponse;
import za.co.bbd.grad.fupboard.cli.models.UserInfoResponse;

public class UserService {

    public static void viewMyInformation() {
        String responseBody = HttpUtil.sendRequest(HttpUtil.getRequest(Constants.BASE_URL + "/v1/users/me"));

        if (responseBody != null) {
            displayUserInfo(responseBody);
        } else {
            System.out.println(Constants.RED + "Failed to get user information" + Constants.RESET);
        }
    }

    public static void displayUserInfo(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserInfoResponse userInfo = objectMapper.readValue(responseBody, UserInfoResponse.class);

            System.out.println("Email: " + userInfo.getEmail());
            System.out.println("Username: " + userInfo.getUsername());
            System.out.println("Projects:");

            if (userInfo.getProjects() != null && !userInfo.getProjects().isEmpty()) {
                for (var project : userInfo.getProjects()) {
                    System.out.println("    Name: " + project.getProjectName());
                }
            } else {
                System.out.println("No projects found.");
            }
        } catch (IOException e) {
            System.err.println(Constants.RED + "Error parsing response: " + e.getMessage() + Constants.RESET);
        }
    }

    public static void updateMyDetails(Scanner scanner) {
        System.out.print(Constants.YELLOW + "Enter new username (leave blank if you don't want to change it): " + Constants.RESET);
        String username = scanner.nextLine();

        System.out.print(Constants.YELLOW + "Enter new email (leave blank if you don't want to change it): " + Constants.RESET);
        String email = scanner.nextLine();

        JSONObject jsonBody = new JSONObject();
        if (!email.isEmpty()) {
            jsonBody.put("email", email);
        } else {
            jsonBody.put("email", null);
        }

        if (!username.isEmpty()) {
            jsonBody.put("username", username);
        } else {
            jsonBody.put("username", null);
        }

        String responseBody = HttpUtil.sendRequest(HttpUtil.patchRequest(Constants.BASE_URL + "/v1/users/me", jsonBody.toString()));

        if (responseBody != null) {
            System.out.println(Constants.GREEN + "-> Details updated successfully!" + Constants.RESET);
        } else {
            System.out.println(Constants.RED + "-> Failed to update details!" + Constants.RESET);
        }
    }

    public static boolean showMyInvites() {
        String responseBody = HttpUtil.sendRequest(HttpUtil.getRequest(Constants.BASE_URL + "/v1/users/me/invites"));

        if (responseBody != null) {
            return userHasInvites(responseBody);
        } else {
            System.out.println(Constants.RED + "Failed to get user information" + Constants.RESET);
            return false;
        }
    }

    public static boolean userHasInvites(String inviteList) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<InviteResponse> invites = objectMapper.readValue(inviteList, objectMapper.getTypeFactory().constructCollectionType(List.class, InviteResponse.class));

            boolean foundUnaccepted = false;

            for (InviteResponse invite : invites) {
                if (!invite.isAccepted()) {
                    foundUnaccepted = true;
                    System.out.println("Invite ID: " + invite.getProjectInviteId());
                    System.out.println("Username: " + invite.getUsername());
                    System.out.println("Project ID: " + invite.getProjectId());
                    System.out.println("Accepted: " + invite.isAccepted());
                    System.out.println("----------------------------");
                }
            }

            if (!foundUnaccepted) {
                return false;
            }

        } catch (IOException e) {
            System.err.println(Constants.RED + "Error parsing invites response: " + e.getMessage() + Constants.RESET);
        }

        return true;
    }
}
