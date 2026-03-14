package server.db.tables;

import server.db.DeletionManager;
import server.db.InsertionManager;
import server.db.SelectionManager;
import server.db.Table;
import server.objects.User;

public class TableUser extends Table {

    @Override
    public void register() {
        createTable("user")
            .addColumn(createColumn("id", ColumnType.INT).autoIncrement().primaryKey())
            .addColumn(createTextColumn("email", 255).notNull().unique())
            .addColumn(createColumn("is_admin", ColumnType.BOOLEAN).notNull().defaultValue(false))
            .execute();

        // addUser("test@boboz.gg", false);
        // deleteUserByEmail("test@boboz.gg");

        var test = getUserByEmail("test@boboz.gg");
    }

    public static void addUser(String email, boolean isAdmin) {
        InsertionManager.insert("user")
            .value("id", 2) // Auto-increment
            .value("email", email)
            .value("is_admin", isAdmin)
            .execute();
    }

    public static void deleteUserByEmail(String email) {
        DeletionManager.delete("user")
            .where("email", email)
            .execute();
    }

    public static User getUserByEmail(String email) {
        return SelectionManager.select("user")
            .where("email", email)
            .execute(User.class);
    }
}