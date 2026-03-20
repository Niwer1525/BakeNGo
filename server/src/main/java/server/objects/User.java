package server.objects;

import niwer.queryon.SQLSerializable;
import niwer.queryon.tables.api.IColumnField;

public class User extends SQLSerializable<User> {

    @IColumnField(name = "id", primaryKey = true, autoIncrement = true)
    private int id;

    @IColumnField(name = "email", unique = true, notNull = true)
    private String email;

    @IColumnField(name = "password", unique = true, notNull = true)
    private String password;

    @IColumnField(name = "is_admin")
    private boolean isAdmin = false;

    public int id() { return id; }

    public String email() { return email; }

    public String password() { return password; }

    public boolean isAdmin() { return isAdmin; }
}