package server.objects;

import java.sql.ResultSet;
import java.sql.SQLException;

import server.SQLSerializable;

public class User implements SQLSerializable<User> {

    private int id;
    private String email;
    private boolean isAdmin;

    public int id() { return id; }

    public String email() { return email; }

    public boolean isAdmin() { return isAdmin; }

    @Override
    public User objectify(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("id");
        this.email = resultSet.getString("email");
        this.isAdmin = resultSet.getBoolean("is_admin");
        return this;
    }
}
