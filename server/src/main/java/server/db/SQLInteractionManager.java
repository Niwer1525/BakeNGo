package server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import niwer.lumen.Console;
import server.SQLSerializable;
import server.logging.CroissantFlowLogTypes;

public class SQLInteractionManager {

    /**
     * Close statement and result set safely.
     * 
     * @param statement The statement to close
     * @param result The result set to close
     */
    public static void closeStatement(Statement statement, ResultSet result) {
        try {
            if (statement != null) statement.close();
            if (result != null) result.close();
        } catch (SQLException ignore) {}
    }

    /**
     * Execute an SQL command without expecting a return value.
     * @param command The SQL command to execute
     * @param params The parameters to set in the prepared statement
     * @author Niwer
     */
    public static void executeSQLCommand(String command, Object... params) { executeSQLCommand(null, command, params); }

    /**
     * @param command The SQL command to execute
     * @param params  The parameters to set in the prepared statement
     * @return The objectified result of type T, or null if no result
     * @author Niwer
     */
    public static <T extends SQLSerializable<T>> T executeSQLCommand(Class<T> serializer, String command, Object... params) {
        try {
            final PreparedStatement STATEMENT = DataManager.getConnexion().prepareStatement(command);

            /* Pass all objects to the command */
            for (int i = 0; i < params.length; i++) STATEMENT.setObject(i + 1, params[i]);

            /* If no serializer is provided, execute without return */
            if(serializer == null) {
                STATEMENT.executeUpdate();
                closeStatement(STATEMENT, null);
                return null;
            }

            /* Execture and save the result */
            T obj = null; // Object to return
            final ResultSet RESULT = STATEMENT.executeQuery();

            /* Convert the result and return it */
            if (RESULT.next()) {
                obj = (T) serializer.getDeclaredConstructor().newInstance();
                obj = obj.objectify(RESULT);
            }
            closeStatement(STATEMENT, RESULT);
            return obj;
        } catch (SQLException e) {
            Console.log(String.format("Error while executing SQL command (%s) : ", command) + e.getMessage()).error().send();
            return null;
        } catch (Exception e) {
            Console.log("Error while serializing : " + e.getMessage()).type(CroissantFlowLogTypes.SQL).error().send();
            return null;
        }
    }

    public static <T extends SQLSerializable<T>> List<T> executeSQLCommandList(Class<T> serializer, String command, Object... params) {
        try {
            final PreparedStatement STATEMENT = DataManager.getConnexion().prepareStatement(command);

            /* Pass all objects to the command */
            for (int i = 0; i < params.length; i++) STATEMENT.setObject(i + 1, params[i]);

            /* Execture and save the result */
            final ResultSet RESULT = STATEMENT.executeQuery();
            final java.util.List<T> list = new java.util.ArrayList<>();

            /* Convert the result and return it */
            while (RESULT.next()) {
                T obj = (T) serializer.getDeclaredConstructor().newInstance();
                obj = obj.objectify(RESULT);
                list.add(obj);
            }
            closeStatement(STATEMENT, RESULT);
            return list;
        } catch (SQLException e) {
            Console.log(String.format("Error while executing SQL command (%s) : ", command) + e.getMessage()).error().type(CroissantFlowLogTypes.SQL).send();
            return null;
        } catch (Exception e) {
            Console.log("Error while serializing : " + e.getMessage()).type(CroissantFlowLogTypes.SQL).error().send();
            return null;
        }
    }

    /**
     * Execute an SQL command and return a single value of a default Java type.
     * @param command The SQL command to execute
     * @param params The parameters to set in the prepared statement
     * @return The result as an Object, or null if no result
     */
    public static Object executeSQLCommandForPrimitive(String command, Object... params) {
        try {
            final PreparedStatement STATEMENT = DataManager.getConnexion().prepareStatement(command);

            /* Pass all objects to the command */
            for (int i = 0; i < params.length; i++) STATEMENT.setObject(i + 1, params[i]);

            final ResultSet RESULT = STATEMENT.executeQuery();

            /* If there is a result, return the first column of the first row */
            if (RESULT.next()) {
                Object result = RESULT.getObject(1);
                closeStatement(STATEMENT, RESULT);
                return result;
            }
            closeStatement(STATEMENT, RESULT);
            return null;
        } catch (SQLException e) {
            Console.log(String.format("Error while executing SQL command (%s) : ", command) + e.getMessage()).error().type(CroissantFlowLogTypes.SQL).send();
            return null;
        }
    }
}
