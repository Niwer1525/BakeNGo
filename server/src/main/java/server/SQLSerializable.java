package server;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SQLSerializable<T> {
    /**
     * This method will convert a ResultSet row into an object of type T.
     * @param resultSet The ResultSet to objectify
     * @return The object of type T
     * @throws SQLException if an SQL error occurs
     */
    T objectify(ResultSet resultSet) throws SQLException;
}