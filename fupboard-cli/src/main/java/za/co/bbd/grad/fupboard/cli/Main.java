package za.co.bbd.grad.fupboard.cli;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        Authentication.performOAuth2Login();
    }
}
