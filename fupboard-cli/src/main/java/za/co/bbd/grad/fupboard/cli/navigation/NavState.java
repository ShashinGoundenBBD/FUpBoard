package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;

public interface NavState {
    public String getLocation();
    public NavResponse handle(Scanner scanner);
}
