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

    public static void addOrderItem(int orderId, int productId, int quantity, int unitPriceCents) {
        if (orderId <= 0) throw new IllegalArgumentException("Order id must be greater than 0");
        if (productId <= 0) throw new IllegalArgumentException("Product id must be greater than 0");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than 0");
        if (unitPriceCents < 0) throw new IllegalArgumentException("Unit price cannot be negative");

        InsertionManager.insert(App.DATA_BASE, TableOrderItem.class,
            "order_id", "product_id", "quantity", "unit_price_cents", "line_total_cents")
            .row(orderId, productId, quantity, unitPriceCents, quantity * unitPriceCents)
            .execute();
    }

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

    public static void deleteItemsByOrderId(int orderId) {
        if (orderId <= 0) throw new IllegalArgumentException("Order id must be greater than 0");

        DeletionManager.delete(App.DATA_BASE, TableOrderItem.class)
            .where(Expression.of("order_id").isEqualTo(orderId))
            .execute();
    }
}
