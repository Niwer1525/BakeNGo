package server.db;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class TableTest {

    @Test void createTableClass() {
        new Table() {
            @Override
            public void register() {
                createTable("test")
                    .addColumn(createColumn("id", ColumnType.INT).autoIncrement().primaryKey())
                    .addColumn(createTextColumn("name", 255).notNull().unique().defaultValue("TestContent"+UUID.randomUUID()))
                    .addColumn(createColumn("is_admin", ColumnType.BOOLEAN).notNull().defaultValue(false))
                    .addColumn(createColumn("is_admin", ColumnType.INT).notNull().defaultValue(1))
                    .addColumn(createColumn("foreign_id", ColumnType.INT).notNull().foreignKey("other_table", "id"))
                    .execute();
            }
        }.register();
    }

    @Test void createColumnWithWrongCastDefaultValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Table() {
                @Override
                public void register() {
                    createTable("test")
                        .addColumn(createColumn("is_admin", ColumnType.BOOLEAN).notNull().defaultValue("false"));
                }
            }.register();
        });
    }
}
