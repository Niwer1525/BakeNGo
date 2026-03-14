package server.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DataManagerTest {

    @Test void testDisconnectWithoutConnection() {
        /* Connect and disconnect to ensure connection is properly closed */
        DataManager.connect();
        DataManager.disconnect();

        /* Try to disconnect again */
        DataManager.disconnect();
        assertNull(DataManager.getConnexion());
    }

    @Test void testConnection() {
        try {
            DataManager.connect();
        } catch (Exception e) {
            assertEquals("Failed to connect to database", e.getMessage());
        }
        assertNotNull(DataManager.getConnexion());
    }

    @Test void testReconnectWithoutConnection() {
        DataManager.connect();
        assertNotNull(DataManager.getConnexion());

        /* Simulate connection loss */
        DataManager.disconnect();

        /* Reconnect */
        boolean reconnected = DataManager.reconnect();
        assertNotNull(DataManager.getConnexion());
        assertTrue(reconnected);
    }

    @Test void testReconnectWithConnection() {
        DataManager.connect();
        assertNotNull(DataManager.getConnexion());

        boolean reconnected = DataManager.reconnect();
        assertNotNull(DataManager.getConnexion());
        assertFalse(reconnected);
    }

    @Test void testDisconnect() {
        DataManager.connect();
        assertNotNull(DataManager.getConnexion());

        DataManager.disconnect();
        assertNull(DataManager.getConnexion());
    }

    @Test void testLoad() {
        DataManager.load();
        assertNotNull(DataManager.getConnexion());
    }
}
