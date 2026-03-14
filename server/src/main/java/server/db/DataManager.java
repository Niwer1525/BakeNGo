package server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import niwer.lumen.Console;
import server.App;
import server.db.tables.TableBooking;
import server.db.tables.TableUser;
import server.logging.CroissantFlowLogTypes;

public class DataManager {

    private static final String DATABASE_PATH = "jdbc:sqlite:" + App.BASE_FOLDER.getAbsolutePath() + "/main.db";
    private static Connection connexion = null;
    
    public static Connection getConnexion() {
        return connexion;
    }

    /**
     * Reconnect to database if connection is null.
     * 
     * @return true if reconnection was needed, false otherwise
     */
    protected static boolean reconnect() {
        if (connexion == null) {
            connect();
            return connexion != null;
        }
        return false;
    }

    /**
     * Safely disconnect from the database, closing the connection if it exists.
     */
    public static void disconnect() {
        try {
            if (connexion != null) connexion.close();
        } catch (SQLException e) {
            Console.log("Failed to disconnect from database: " + e.getMessage()).type(CroissantFlowLogTypes.SQL).error().send();
        } finally {
            connexion = null;
        }
    }

    /**
     * Establish connection to SQLite database and initialize tables.
     */
    public static void connect() {
        try {
            Console.log("(Re)Connecting to database").type(CroissantFlowLogTypes.SQL).send();
            connexion = DriverManager.getConnection(DATABASE_PATH);
        } catch (SQLException e) {
            Console.log("Failed to connect to database: " + e.getMessage()).type(CroissantFlowLogTypes.SQL).error().send();
        }
    }

    public static void load() {
        connect();

        /* Register all SQL interactions */
        {
            Console.log("Registering SQL tables...").type(CroissantFlowLogTypes.SQL).send();
            new TableUser().register();
            new TableBooking().register();
        }
    }
}
