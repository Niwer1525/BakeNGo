package server.db;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Table class is an abstraction of SQL/SQLite tabke creation.
 * It's very basic and only supports a limited set of features, but it allows to define our database schema in Java code without having to write raw SQL commands.
 * 
 * @author Niwer
 */
public abstract class Table {

    public abstract void register();

    protected SQLTable createTable(String name) { return new SQLTable(name); }

    protected SQLColumn createColumn(String name, ColumnType type) { return new SQLColumn(name, type, 0); }

    protected SQLColumn createTextColumn(String name, int size) { return new SQLColumn(name, ColumnType.VARCHAR, size); }

    protected static class SQLTable {
        private final String NAME;
        private final Set<SQLColumn> COLUMNS = new LinkedHashSet<>();

        private SQLTable(String name) { this.NAME = name; }
        
        /**
         * Add a column to the table definition (Use Table.createColumn or Table.createTextColumn to create a column)
         * @param column The column to add
         * @return The SQLTable instance for chaining
         */
        public SQLTable addColumn(SQLColumn column) {
            COLUMNS.add(column);
            return this;
        }

        /**
         * Execute the SQL command to create the table in the database with the defined columns.
         * This should be called after defining all columns for the table.
         */
        public void execute() {
            final StringBuilder QUERY = new StringBuilder("CREATE TABLE IF NOT EXISTS " + NAME + " (");
            for (final SQLColumn COLUMN : COLUMNS) QUERY.append(COLUMN.getSQLDefinition()).append(", ");
            QUERY.setLength(QUERY.length() - 2); // Remove last comma and space
            QUERY.append(")");
            SQLInteractionManager.executeSQLCommand(QUERY.toString());
        }
    }

    protected static class SQLColumn {
        private final String NAME;
        private final ColumnType TYPE;
        private final int SIZE; // Only used for VARCHAR, ignored otherwise
        
        private boolean autoIncrement = false;
        private boolean notNull = false;
        private boolean unique = false;
        private boolean primaryKey = false;
        private Object defaultValue = null;

        private String foreignKeyReferenceTable = null;
        private String foreignKeyReferenceColumn = null;

        private SQLColumn(String name, ColumnType type, int size) {
            this.NAME = name;
            this.TYPE = type;
            this.SIZE = size;
        }
        
        public SQLColumn autoIncrement() {
            this.autoIncrement = true;
            return this;
        }

        public SQLColumn notNull() {
            this.notNull = true;
            return this;
        }

        public SQLColumn unique() {
            this.unique = true;
            return this;
        }

        public SQLColumn primaryKey() {
            this.primaryKey = true;
            return this.notNull().unique();
        }

        public SQLColumn foreignKey(String referenceTable, String referenceColumn) {
            this.foreignKeyReferenceTable = referenceTable;
            this.foreignKeyReferenceColumn = referenceColumn;
            return this;
        }
        
        public SQLColumn defaultValue(Object value) {
            try {
                switch (TYPE) {
                    case INT -> this.defaultValue = (Integer) value;
                    case VARCHAR -> this.defaultValue = value.toString();
                    case BOOLEAN -> this.defaultValue = (Boolean) value;
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Default value type does not match column type for column " + NAME);
            }
            return this;
        }

        private String getSQLDefinition() {
            final StringBuilder DEFINITION = new StringBuilder(NAME + " " + TYPE.getSqlType());
            if (TYPE == ColumnType.VARCHAR) DEFINITION.append("(").append(SIZE).append(")");
            if (autoIncrement) DEFINITION.append(" AUTO_INCREMENT");
            if (notNull) DEFINITION.append(" NOT NULL");
            if (unique) DEFINITION.append(" UNIQUE");
            if (defaultValue != null) DEFINITION.append(" DEFAULT '").append(defaultValue).append("'");
            if (foreignKeyReferenceTable != null && foreignKeyReferenceColumn != null) {
                DEFINITION.append(" REFERENCES ").append(foreignKeyReferenceTable).append("(").append(foreignKeyReferenceColumn).append(")");
            }
            return DEFINITION.toString();
        }

        @Override
        public int hashCode() {
            return NAME.hashCode()
                + TYPE.hashCode()
                + Boolean.hashCode(this.autoIncrement)
                + Boolean.hashCode(this.notNull)
                + Boolean.hashCode(this.unique)
                + Boolean.hashCode(this.primaryKey)
                + (defaultValue != null ? defaultValue.hashCode() : 0)
            ;
        }
    }

    protected enum ColumnType {
        INT("INT"),
        VARCHAR("VARCHAR"),
        BOOLEAN("BOOLEAN");

        private final String sqlType;

        ColumnType(String sqlType) { this.sqlType = sqlType; }

        public String getSqlType() { return sqlType; }
    }
}