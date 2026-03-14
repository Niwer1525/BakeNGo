package server.db;

import java.util.List;

import org.junit.jupiter.api.Test;

class SQLInteractionManagerTest {
    /* 
        Basic statement handling
    */

    @Test void testCloseStatement() {
        // This method is static and does not return anything, so we just need to ensure it does not throw an exception
        SQLInteractionManager.closeStatement(null, null);
    }

    /*
        Command executions
    */

    @Test void testExecuteSQLCommand() {
        DataManager.load();
        SQLInteractionManager.executeSQLCommand("SELECT 1");
    }

    @Test void testExecuteSQLCommandWithParams() {
        DataManager.load();
        SQLInteractionManager.executeSQLCommand("SELECT ? + ?", 1, 2);
    }

    @Test void testExecuteSQLCommandWithSerializer() {
        DataManager.load();
        SQLInteractionManager.executeSQLCommand(null, "SELECT 1", new Object[]{});
    }

    @Test void testExecuteSQLCommandWithSerializerAndParams() {
        DataManager.load();
        SQLInteractionManager.executeSQLCommand(null, "SELECT ? + ?", new Object[]{1, 2});
    }

    @Test void testExecuteSQLCommandWithNullParams() {
        DataManager.load();
        SQLInteractionManager.executeSQLCommand("SELECT ? + ?", (Object[]) null);
    }

    @Test void testExecuteSQLCommandList() {
        DataManager.load();
        SQLInteractionManager.executeSQLCommand("SELECT ? + ?", List.of(1, 2).toArray());
    }
}
