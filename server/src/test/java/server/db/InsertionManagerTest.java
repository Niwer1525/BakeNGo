package server.db;

import org.junit.jupiter.api.Test;

class InsertionManagerTest {

    @Test void testInsertion() {
        InsertionManager.insert("user")
            .value("id", 1)
            .value("email", "test@test.gg")
            .value("is_admin", false)
            .execute();
    }

    @Test void testInsertionOrIgnore() {
        InsertionManager.insertOrIgnore("user")
            .value("id", 1)
            .value("email", "test@test.gg")
            .value("is_admin", false)
            .execute();
    }

}
