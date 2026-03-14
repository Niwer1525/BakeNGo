package server.db;

import org.junit.jupiter.api.Test;

class DeletionManagerTest {

    @Test void testDelete() {
        DeletionManager.delete("user")
            .where("email", "test@example.com")
            .where("is_admin", false)
            .execute();
    }
}
