package za.co.bbd.grad.fupboard.cli;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Scanner;

import za.co.bbd.grad.fupboard.cli.navigation.NavResponse;
import za.co.bbd.grad.fupboard.cli.navigation.NavState;
import za.co.bbd.grad.fupboard.cli.navigation.StartState;

public class Main {
    private static String authToken;

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        // Welcome to FUPBoard
        displayWelcomeMessage();
        // Sign into Google account to proceed
        try {
            authToken = Authentication.performOAuth2Login();
            System.out.println("Signed in successfully! Token: " + authToken);
        } catch (Exception e) {
            System.out.println("Sign-in was not successful: " + e.getMessage());
            // Maybe ask to retry instead
            return;
        }

        Scanner scanner = new Scanner(System.in);
        boolean continueUsing = true;

        ArrayList<NavState> states = new ArrayList<NavState>();
        states.add(new StartState());

        while (continueUsing) {
            if (states.isEmpty()) {
                System.out.println("Goodbye! :)");
                break;
            }
            
            var navState = states.getLast();

            var locationList = states.stream()
                .map(s -> s.getLocation())
                .filter(l -> l != null && !l.isEmpty())
                .toList();
            var location = String.join(" -> ", locationList);
            System.out.println("\n" + location);
            
            var navResponse = navState.handle(scanner);

            if (navResponse instanceof NavResponse.Exit) {
                break;
            } else if (navResponse instanceof NavResponse.Back) {
                states.removeLast();
            } else if (navResponse instanceof NavResponse.Push) {
                var newState = ((NavResponse.Push)navResponse).getNewState();
                states.add(newState);
            }
        }

        scanner.close();
    }

    private static void displayWelcomeMessage() {

        String reset = "\u001B[0m";
        String green = "\u001B[32m";

        System.out.println(green + """
        ─────────────────────────────────────────────────────────────
        ─██████████████────────────────██████──██████─██████████████─
        ─██░░░░░░░░░░██────────────────██░░██──██░░██─██░░░░░░░░░░██─
        ─██░░██████████────────────────██░░██──██░░██─██░░██████░░██─
        ─██░░██────────────────────────██░░██──██░░██─██░░██──██░░██─
        ─██░░██████████─██████████████─██░░██──██░░██─██░░██████░░██─
        ─██░░░░░░░░░░██─██░░░░░░░░░░██─██░░██──██░░██─██░░░░░░░░░░██─
        ─██░░██████████─██████████████─██░░██──██░░██─██░░██████████─
        ─██░░██────────────────────────██░░██──██░░██─██░░██─────────
        ─██░░██────────────────────────██░░██████░░██─██░░██─────────
        ─██░░██────────────────────────██░░░░░░░░░░██─██░░██─────────
        ─██████────────────────────────██████████████─██████─────────
        ─────────────────────────────────────────────────────────────
        ──────────────────────────────────────────────────────────────────────────────────
        ─██████████████───██████████████─██████████████─████████████████───████████████───
        ─██░░░░░░░░░░██───██░░░░░░░░░░██─██░░░░░░░░░░██─██░░░░░░░░░░░░██───██░░░░░░░░████─
        ─██░░██████░░██───██░░██████░░██─██░░██████░░██─██░░████████░░██───██░░████░░░░██─
        ─██░░██──██░░██───██░░██──██░░██─██░░██──██░░██─██░░██────██░░██───██░░██──██░░██─
        ─██░░██████░░████─██░░██──██░░██─██░░██████░░██─██░░████████░░██───██░░██──██░░██─
        ─██░░░░░░░░░░░░██─██░░██──██░░██─██░░░░░░░░░░██─██░░░░░░░░░░░░██───██░░██──██░░██─
        ─██░░████████░░██─██░░██──██░░██─██░░██████░░██─██░░██████░░████───██░░██──██░░██─
        ─██░░██────██░░██─██░░██──██░░██─██░░██──██░░██─██░░██──██░░██─────██░░██──██░░██─
        ─██░░████████░░██─██░░██████░░██─██░░██──██░░██─██░░██──██░░██████─██░░████░░░░██─
        ─██░░░░░░░░░░░░██─██░░░░░░░░░░██─██░░██──██░░██─██░░██──██░░░░░░██─██░░░░░░░░████─
        ─████████████████─██████████████─██████──██████─██████──██████████─████████████───
        ──────────────────────────────────────────────────────────────────────────────────                                                
            """ + reset);
    }
}
