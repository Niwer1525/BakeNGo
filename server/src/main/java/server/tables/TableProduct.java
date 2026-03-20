package server.tables;

import java.util.List;

import niwer.queryon.DataBase;
import niwer.queryon.queries.Expression;
import niwer.queryon.queries.interaction.DeletionManager;
import niwer.queryon.queries.interaction.InsertionManager;
import niwer.queryon.queries.interaction.SelectionManager;
import niwer.queryon.queries.interaction.UpdateManager;
import niwer.queryon.tables.Table;
import server.App;
import server.objects.Product;

public class TableProduct extends Table {

    public TableProduct(DataBase db) {
        super(db);

        this.addColumnsFromClass(Product.class).execute();
    }

    @Override public String name() { return "products"; }

    public static void addProduct(String name, String description, int priceCents, int stock, boolean isActive) {
        name = normalizeName(name);
        description = normalizeDescription(description);
        validatePrice(priceCents);
        validateStock(stock);

        InsertionManager.insert(App.DATA_BASE, TableProduct.class, "name", "description", "price_cents", "stock", "is_active")
            .row(name, description, priceCents, stock, isActive)
            .execute();
    }

    public static Product getProductById(int id) {
        validateId(id, "Product id");

        return SelectionManager.select(App.DATA_BASE, TableProduct.class,
            "COALESCE(NULLIF(id, 0), rowid) AS id", "name", "description", "price_cents", "stock", "is_active")
            .where(Expression.of("COALESCE(NULLIF(id, 0), rowid)").isEqualTo(id))
            .executeSerializable(Product.class);
    }

    public static List<Product> getAllProducts() {
        final SelectionManager query = SelectionManager.select(App.DATA_BASE, TableProduct.class,
            "COALESCE(NULLIF(id, 0), rowid) AS id", "name", "description", "price_cents", "stock", "is_active")
            .orderBy("rowid", SelectionManager.EnumOrder.ASC);

        try {
            return query.executeList(Product.class);
        } catch (IllegalStateException ignored) {
            final Product single = query.executeSerializable(Product.class);
            return single == null ? List.of() : List.of(single);
        }
    }

    public static List<Product> getActiveProducts() {
        final SelectionManager query = SelectionManager.select(App.DATA_BASE, TableProduct.class,
            "COALESCE(NULLIF(id, 0), rowid) AS id", "name", "description", "price_cents", "stock", "is_active")
            .where(Expression.of("is_active").isEqualTo(true))
            .orderBy("rowid", SelectionManager.EnumOrder.ASC);

        try {
            return query.executeList(Product.class);
        } catch (IllegalStateException ignored) {
            final Product single = query.executeSerializable(Product.class);
            return single == null ? List.of() : List.of(single);
        }
    }

    public static void updateStock(int id, int stock) {
        validateId(id, "Product id");
        validateStock(stock);
        ensureProductExists(id);

        UpdateManager.update(App.DATA_BASE, TableProduct.class)
            .set("stock", stock)
            .where(Expression.of("COALESCE(NULLIF(id, 0), rowid)").isEqualTo(id))
            .execute();
    }

    public static void deleteProduct(int id) {
        validateId(id, "Product id");
        ensureProductExists(id);

        DeletionManager.delete(App.DATA_BASE, TableProduct.class)
            .where(Expression.of("COALESCE(NULLIF(id, 0), rowid)").isEqualTo(id))
            .execute();
    }

    private static void ensureProductExists(int id) {
        if (getProductById(id) == null) throw new IllegalArgumentException("Product with id " + id + " does not exist");
    }

    private static void validateId(int id, String label) {
        if (id <= 0) throw new IllegalArgumentException(label + " must be greater than 0");
    }

    private static void validatePrice(int priceCents) {
        if (priceCents < 0) throw new IllegalArgumentException("Price cannot be negative");
    }

    private static void validateStock(int stock) {
        if (stock < 0) throw new IllegalArgumentException("Stock cannot be negative");
    }

    private static String normalizeName(String name) {
        if (name == null) throw new IllegalArgumentException("Name cannot be null");
        final String normalizedName = name.trim();
        if (normalizedName.isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
        return normalizedName;
    }

    private static String normalizeDescription(String description) {
        if (description == null) return "";
        return description.trim();
    }
}