package server;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class AppTest {

    @Test void testMain() {
        // Main is a static and does not return anything, so we just need to ensure it does not throw an exception
        assertDoesNotThrow(() -> App.main(new String[]{}));
    }
}
