package za.co.bbd.grad.fupboard.cli;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Scanner;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.NavStateException;
import za.co.bbd.grad.fupboard.cli.navigation.NavResponse;
import za.co.bbd.grad.fupboard.cli.navigation.NavState;
import za.co.bbd.grad.fupboard.cli.navigation.StartState;
import za.co.bbd.grad.fupboard.cli.services.AuthenticationService;

public class Main {
    private static final String WELCOME_MESSAGE = Constants.GREEN + """
        ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
        ░░░░░░░███████╗░░░░░░██╗░░░██╗██████╗░░░░░░░
        ░░░░░░░██╔════╝░░░░░░██║░░░██║██╔══██╗░░░░░░
        ░░░░░░░█████╗░░█████╗██║░░░██║██████╔╝░░░░░░
        ░░░░░░░██╔══╝░░╚════╝██║░░░██║██╔═══╝░░░░░░░
        ░░░░░░░██║░░░░░░░░░░░╚██████╔╝██║░░░░░░░░░░░
        ░░░░░░░╚═╝░░░░░░░░░░░░╚═════╝░╚═╝░░░░░░░░░░░
        ░░██████╗░░█████╗░░█████╗░██████╗░██████╗░░░
        ░░██╔══██╗██╔══██╗██╔══██╗██╔══██╗██╔══██╗░░
        ░░██████╦╝██║░░██║███████║██████╔╝██║░░██║░░
        ░░██╔══██╗██║░░██║██╔══██║██╔══██╗██║░░██║░░
        ░░██████╦╝╚█████╔╝██║░░██║██║░░██║██████╔╝░░
        ░░╚═════╝░░╚════╝░╚═╝░░╚═╝╚═╝░░╚═╝╚═════╝░░░
        ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                        [ Welcome ]               """ + Constants.RESET;

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        // Sign into Google account to proceed
        try {
            AuthenticationService.performOAuth2Login();
        } catch (Exception e) {
            System.out.println("Sign-in was not successful: " + e.getMessage());
            return;
        }

        // Welcome to FUPBoard
        System.out.println(WELCOME_MESSAGE);

        Scanner scanner = new Scanner(System.in);
        boolean continueUsing = true;

        ArrayList<NavState> states = new ArrayList<NavState>();
        states.add(new StartState());

        while (continueUsing) {
            if (states.isEmpty()) {
                break;
            }
            
            var navState = states.get(states.size() - 1);

            System.out.println();
            
            var locationList = states.stream()
                .map(s -> s.getLocation())
                .filter(l -> l != null && !l.isEmpty())
                .toList();
            var location = String.join(" -> ", locationList);

            System.out.println(location);
            
            NavResponse navResponse;
            try {
                try {
                    navResponse = navState.handle(scanner);
                } catch (NumberFormatException e) {
                    throw new NavStateException(e);
                }
            } catch (NavStateException e) {
                System.out.println(Constants.RED + e.getMessage() + Constants.RESET);
                navResponse = e.getResponse();
            }

            if (navResponse instanceof NavResponse.Exit) {
                break;
            } else if (navResponse instanceof NavResponse.Back) {
                states.remove(states.size() - 1);
            } else if (navResponse instanceof NavResponse.Push) {
                var newState = ((NavResponse.Push)navResponse).getNewState();
                states.add(newState);
            } else if (navResponse instanceof NavResponse.Replace) {
                var newState = ((NavResponse.Replace)navResponse).getNewState();
                states.set(states.size()-1, newState);
            }
        }

        scanner.close();
        System.out.println(Constants.GREEN + "                [ Goodbye ]               " + Constants.RESET);
    }
}
