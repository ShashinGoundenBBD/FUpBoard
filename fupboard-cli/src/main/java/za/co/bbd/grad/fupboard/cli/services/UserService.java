package za.co.bbd.grad.fupboard.cli.services;

import java.io.IOException;

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
    
}
