package server.db.tables;

import server.db.Table;

/**
 * This class represents the "booking" table in the database. It is used to store information about bookings, the associated user and the food items/quantities in the booking.
 */
public class TableBooking extends Table {

    @Override
    public void register() {
        createTable("booking")
            .addColumn(createColumn("id", ColumnType.INT).autoIncrement().primaryKey())
            .addColumn(createColumn("user_id", ColumnType.INT).notNull().foreignKey("user", "id"))
            .addColumn(createColumn("food_id", ColumnType.INT).notNull())
            .addColumn(createColumn("quantity", ColumnType.INT).notNull())
            .execute();
    }
}