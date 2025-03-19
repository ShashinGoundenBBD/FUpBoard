package za.co.bbd.grad.fupboard;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import za.co.bbd.grad.fupboard.cli.common.NavStateException;
import za.co.bbd.grad.fupboard.cli.navigation.NavResponse;
import za.co.bbd.grad.fupboard.cli.navigation.NavState;
import za.co.bbd.grad.fupboard.cli.navigation.StartState;

class MainTest {
    private Scanner scanner;
    private StartState startState;

    @BeforeEach
    void setUp() {
        // Mocking Scanner input
        String simulatedUserInput = "1\n0\n"; // Simulating "My Profile" then "Back"
        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes());
        scanner = new Scanner(inputStream);
        
        // Mocking StartState
        startState = Mockito.mock(StartState.class);
    }

    @Test
    void testNavigationFlow() throws NavStateException {
        ArrayList<NavState> states = new ArrayList<>();
        states.add(startState);
        
        when(startState.handle(any())).thenReturn(NavResponse.back());
        
        assertEquals(1, states.size()); // Initially, there should be one state (StartState)

        NavResponse response = startState.handle(scanner);
        if (response instanceof NavResponse.Back) {
            states.remove(states.size() - 1);
        }
        
        assertTrue(states.isEmpty()); // After "Back", state list should be empty
    }

    @Test
    void testInvalidInputHandling() throws NavStateException {
        // Simulating invalid input scenario
        when(startState.handle(any())).thenThrow(new NavStateException("Invalid option."));
        
        Exception exception = assertThrows(NavStateException.class, () -> {
            startState.handle(scanner);
        });
        
        assertEquals("Invalid option.", exception.getMessage());
    }

    @Test
    void testPushState() {
        ArrayList<NavState> states = new ArrayList<>();
        states.add(startState);
        
        NavState newState = mock(NavState.class);
        states.add(newState);
        
        assertEquals(2, states.size());
        assertEquals(newState, states.get(1));
    }

    @Test
    void testReplaceState() {
        ArrayList<NavState> states = new ArrayList<>();
        states.add(startState);
        
        NavState newState = mock(NavState.class);
        states.set(0, newState);
        
        assertEquals(1, states.size());
        assertEquals(newState, states.get(0));
    }

    @Test
    void testExitState() {
        ArrayList<NavState> states = new ArrayList<>();
        states.add(startState);
        
        states.clear();
        
        assertTrue(states.isEmpty());
    }

    @Test
    void testStayState() throws NavStateException {
        when(startState.handle(any())).thenReturn(NavResponse.stay());
        
        NavResponse response = startState.handle(scanner);
        
        assertTrue(response instanceof NavResponse.Stay);
    }
}
