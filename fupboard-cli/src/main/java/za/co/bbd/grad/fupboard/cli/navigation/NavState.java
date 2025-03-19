package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;

import za.co.bbd.grad.fupboard.cli.common.NavStateException;

public interface NavState {
    public String getLocation();
    public NavResponse handle(Scanner scanner) throws NavStateException;
}
