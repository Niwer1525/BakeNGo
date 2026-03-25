package server.tables;

import java.util.List;

import niwer.queryon.DataBase;
import niwer.queryon.queries.Expression;
import niwer.queryon.queries.interaction.DeletionManager;
import niwer.queryon.queries.interaction.InsertionManager;
import niwer.queryon.queries.interaction.SelectionManager;
import server.App;
import niwer.queryon.tables.Table;
import server.objects.OrderItem;

public class TableOrderItem extends Table {

    public TableOrderItem(DataBase db) {
        super(db);

        this.addColumnsFromClass(OrderItem.class).execute();
    }

    @Override public String name() { return "order_items"; }

    /**
     * Adds a new order item to the database with the specified order id, product id, quantity, and unit price. The line total is automatically calculated as quantity multiplied by unit price.
     * 
     * @param orderId The id of the order to which the item belongs (must be greater than 0)
     * @param productId The id of the product associated with the order item (must be greater than 0)
     * @param quantity The quantity of the product in the order item (must be greater than 0)
     * @param unitPriceCents The unit price of the product in cents (must be non-negative)
     */
    public static synchronized void addOrderItem(int orderId, int productId, int quantity, int unitPriceCents) {
        if (orderId <= 0) throw new IllegalArgumentException("Order id must be greater than 0");
        if (productId <= 0) throw new IllegalArgumentException("Product id must be greater than 0");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than 0");
        if (unitPriceCents < 0) throw new IllegalArgumentException("Unit price cannot be negative");
        final int orderItemId = getNextOrderItemId();

        InsertionManager.insert(App.DATA_BASE, TableOrderItem.class,
            "id", "order_id", "product_id", "quantity", "unit_price_cents", "line_total_cents")
            .row(orderItemId, orderId, productId, quantity, unitPriceCents, quantity * unitPriceCents)
            .execute();
    }

    /**
     * Gets all order items associated with a specific order id from the database, ordered by id ascending.
     * 
     * @param orderId The id of the order for which to get items
     * @return A list of all order items associated with the specified order id from the database, ordered by id ascending. If there are no order items for the specified order id, returns an empty list.
     */
    public static List<OrderItem> getItemsByOrderId(int orderId) {
        if (orderId <= 0) throw new IllegalArgumentException("Order id must be greater than 0");

        final SelectionManager query = SelectionManager.select(App.DATA_BASE, TableOrderItem.class,
            "COALESCE(NULLIF(id, 0), rowid) AS id", "order_id", "product_id", "quantity", "unit_price_cents", "line_total_cents")
            .where(Expression.of("order_id").isEqualTo(orderId))
            .orderBy("rowid", SelectionManager.EnumOrder.ASC);

        try {
            return query.executeList(OrderItem.class);
        } catch (IllegalStateException ignored) {
            final OrderItem single = query.executeSerializable(OrderItem.class);
            return single == null ? List.of() : List.of(single);
        }
    }

    /**
     * Deletes all order items associated with a specific order id from the database.
     * 
     * @param orderId The id of the order for which to delete items
     */
    public static void deleteItemsByOrderId(int orderId) {
        if (orderId <= 0) throw new IllegalArgumentException("Order id must be greater than 0");

        DeletionManager.delete(App.DATA_BASE, TableOrderItem.class)
            .where(Expression.of("order_id").isEqualTo(orderId))
            .execute();
    }

    private static int getNextOrderItemId() {
        return getAllOrderItems().stream()
            .mapToInt(OrderItem::id)
            .max()
            .orElse(0) + 1;
    }

    private static List<OrderItem> getAllOrderItems() {
        final SelectionManager query = SelectionManager.select(App.DATA_BASE, TableOrderItem.class,
            "COALESCE(NULLIF(id, 0), rowid) AS id", "order_id", "product_id", "quantity", "unit_price_cents", "line_total_cents")
            .orderBy("rowid", SelectionManager.EnumOrder.ASC);

        try {
            return query.executeList(OrderItem.class);
        } catch (IllegalStateException ignored) {
            final OrderItem single = query.executeSerializable(OrderItem.class);
            return single == null ? List.of() : List.of(single);
        }
    }
}
