package za.co.bbd.grad.fupboard.cli.services;

import java.io.IOException;
import java.util.Scanner;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.HttpUtil;
import za.co.bbd.grad.fupboard.cli.models.Project;
import za.co.bbd.grad.fupboard.cli.models.UserInfoResponse;

public class UserService {
        public static void viewMyInformation() {
        String responseBody = HttpUtil.sendRequest(HttpUtil.getRequest(Constants.BASE_URL + "/v1/users/me"));

        if (responseBody != null) {
            displayUserInfo(responseBody);
        }
        else
        {
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
                for (Project project : userInfo.getProjects()) {
                    System.out.println("    Name: " + project.getProjectName());
                }
            } else {
                System.out.println("No projects found.");
            }
        } catch (IOException e) {
            System.err.println(Constants.RED + "Error parsing response: " + e.getMessage() + Constants.RESET);
        }
    }

    public static void updateMyDetails(Scanner scanner)
    {
        System.out.print(Constants.YELLOW + "Enter new username (leave blank if you don't want to change it): " + Constants.RESET);
        String username = scanner.nextLine();

        System.out.print(Constants.YELLOW + "Enter new email (leave blank if you don't want to change it): " + Constants.RESET);
        String email = scanner.nextLine();

        JSONObject jsonBody = new JSONObject();
        if (!email.isEmpty()) {
            jsonBody.put("email", email);
        }
        else {
            jsonBody.put("email", null);
        }

        if (!username.isEmpty()) {
            jsonBody.put("username", username);
        }
        else
        {
            jsonBody.put("username", null);
        }

        System.out.println(jsonBody.toString());
        String responseBody = HttpUtil.sendRequest(HttpUtil.patchRequest(Constants.BASE_URL + "/v1/users/me", jsonBody.toString()));

        if (responseBody != null) {
            System.out.println(Constants.GREEN + "-> Details updated successfully!" + Constants.RESET);
        }
        else
        {
            System.out.println(Constants.RED + "-> Failed to update details!" + Constants.RESET);
        }
    }
    
}
