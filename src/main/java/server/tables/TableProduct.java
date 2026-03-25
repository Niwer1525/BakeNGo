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

    /**
     * Adds a new product to the database.
     * 
     * @param name The name of the product to add
     * @param description The description of the product to add (can be null or empty)
     * @param priceCents The price of the product in cents (must be non-negative)
     * @param stock The stock quantity of the product (must be non-negative)
     * @param isActive Whether the product should be active and visible to customers
     */
    public static synchronized void addProduct(String name, String description, int priceCents, int stock, boolean isActive) {
        name = normalizeName(name);
        description = normalizeDescription(description);
        validatePrice(priceCents);
        validateStock(stock);
        final int PRODUCT_ID = getNextProductId();

        InsertionManager.insert(App.DATA_BASE, TableProduct.class, "id", "name", "description", "price_cents", "stock", "is_active")
            .row(PRODUCT_ID, name, description, priceCents, stock, isActive)
            .execute();
    }

    /**
     * Gets a product from the database by its id.
     * 
     * @param id The id of the product to get
     * @return The product with the given id, or null if no such product exists
     */
    public static Product getProductById(int id) {
        validateId(id, "Product id");

        return SelectionManager.select(App.DATA_BASE, TableProduct.class,
            "COALESCE(NULLIF(id, 0), rowid) AS id", "name", "description", "price_cents", "stock", "is_active")
            .where(Expression.of("COALESCE(NULLIF(id, 0), rowid)").isEqualTo(id))
            .executeSerializable(Product.class);
    }

    /**
     * Gets all products from the database, ordered by id ascending.
     * 
     * @return A list of all products in the database, ordered by id ascending. If there are no products, returns an empty list.
     */
    public static List<Product> getAllProducts() {
        final SelectionManager QUERY = SelectionManager.select(App.DATA_BASE, TableProduct.class,
            "COALESCE(NULLIF(id, 0), rowid) AS id", "name", "description", "price_cents", "stock", "is_active")
            .orderBy("rowid", SelectionManager.EnumOrder.ASC);

        try {
            return QUERY.executeList(Product.class);
        } catch (IllegalStateException ignored) {
            final Product SINGLE = QUERY.executeSerializable(Product.class);
            return SINGLE == null ? List.of() : List.of(SINGLE);
        }
    }

    /**
     * Gets all active products from the database, ordered by id ascending.
     * 
     * @return A list of all active products in the database, ordered by id ascending. If there are no active products, returns an empty list.
     */
    public static List<Product> getActiveProducts() {
        final SelectionManager QUERY = SelectionManager.select(App.DATA_BASE, TableProduct.class,
            "COALESCE(NULLIF(id, 0), rowid) AS id", "name", "description", "price_cents", "stock", "is_active")
            .where(Expression.of("is_active").isEqualTo(true))
            .orderBy("rowid", SelectionManager.EnumOrder.ASC);

        try {
            return QUERY.executeList(Product.class);
        } catch (IllegalStateException ignored) {
            final Product SINGLE = QUERY.executeSerializable(Product.class);
            return SINGLE == null ? List.of() : List.of(SINGLE);
        }
    }

    /**
     * Updates the stock quantity of a product in the database.
     * 
     * @param id The id of the product to update
     * @param stock The new stock quantity for the product
     */
    public static void updateStock(int id, int stock) {
        validateId(id, "Product id");
        validateStock(stock);
        ensureProductExists(id);

        UpdateManager.update(App.DATA_BASE, TableProduct.class)
            .set("stock", stock)
            .where(Expression.of("COALESCE(NULLIF(id, 0), rowid)").isEqualTo(id))
            .execute();
    }

    /**
     * Updates a product's stock, unit price, and active status in the database.
     * 
     * @param id The id of the product to update
     * @param stock The new stock quantity
     * @param priceCents The new unit price in cents
     * @param isActive The new active status
     */
    public static void updateProductDetails(int id, int stock, int priceCents, boolean isActive) {
        validateId(id, "Product id");
        validateStock(stock);
        validatePrice(priceCents);
        ensureProductExists(id);

        UpdateManager.update(App.DATA_BASE, TableProduct.class)
            .set("stock", stock)
            .set("price_cents", priceCents)
            .set("is_active", isActive)
            .where(Expression.of("COALESCE(NULLIF(id, 0), rowid)").isEqualTo(id))
            .execute();
    }

    /**
     * Deletes a product from the database by its id.
     * 
     * @param id The id of the product to delete
     */
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

    private static int getNextProductId() {
        return getAllProducts().stream()
            .mapToInt(Product::id)
            .max()
            .orElse(0) + 1;
    }
}