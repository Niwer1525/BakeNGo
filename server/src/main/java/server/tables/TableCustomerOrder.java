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
import server.objects.CustomerOrder;

public class TableCustomerOrder extends Table {

    public TableCustomerOrder(DataBase db) {
        super(db);

        this.addColumnsFromClass(CustomerOrder.class).execute();
    }

    @Override public String name() { return "orders"; }

    public static CustomerOrder addOrder(Integer userId, String customerEmail, String pickupSlot, String pickupDate, int totalCents) {
        if (userId != null && userId <= 0) throw new IllegalArgumentException("User id must be greater than 0");
        customerEmail = normalizeEmail(customerEmail);
        pickupSlot = requireNonBlank(pickupSlot, "Pickup slot");
        pickupDate = requireNonBlank(pickupDate, "Pickup date");
        if (totalCents < 0) throw new IllegalArgumentException("Total cannot be negative");

        final long now = System.currentTimeMillis();
        InsertionManager.insert(App.DATA_BASE, TableCustomerOrder.class,
            "user_id", "customer_email", "status", "pickup_slot", "pickup_date", "total_cents", "created_at", "updated_at")
            .row(userId, customerEmail, "PENDING", pickupSlot, pickupDate, totalCents, now, now)
            .execute();

        return SelectionManager.select(App.DATA_BASE, TableCustomerOrder.class,
                "COALESCE(NULLIF(id, 0), rowid) AS id", "user_id", "customer_email", "status", "pickup_slot", "pickup_date", "total_cents", "created_at", "updated_at")
            .where(Expression.of("customer_email").isEqualTo(customerEmail)
                .and(Expression.of("created_at").isEqualTo(now)))
            .executeSerializable(CustomerOrder.class);
    }

    public static CustomerOrder getOrderById(int orderId) {
        validateOrderId(orderId);

        return SelectionManager.select(App.DATA_BASE, TableCustomerOrder.class,
            "COALESCE(NULLIF(id, 0), rowid) AS id", "user_id", "customer_email", "status", "pickup_slot", "pickup_date", "total_cents", "created_at", "updated_at")
            .where(Expression.of("COALESCE(NULLIF(id, 0), rowid)").isEqualTo(orderId))
            .executeSerializable(CustomerOrder.class);
    }

    public static List<CustomerOrder> getAllOrders() {
        final SelectionManager query = SelectionManager.select(App.DATA_BASE, TableCustomerOrder.class,
            "COALESCE(NULLIF(id, 0), rowid) AS id", "user_id", "customer_email", "status", "pickup_slot", "pickup_date", "total_cents", "created_at", "updated_at")
            .orderBy("rowid", SelectionManager.EnumOrder.DESC);

        try {
            return query.executeList(CustomerOrder.class);
        } catch (IllegalStateException ignored) {
            final CustomerOrder single = query.executeSerializable(CustomerOrder.class);
            return single == null ? List.of() : List.of(single);
        }
    }

    public static void updateOrderStatus(int orderId, String status) {
        validateOrderId(orderId);
        status = normalizeStatus(status);
        if (getOrderById(orderId) == null) throw new IllegalArgumentException("Order with id " + orderId + " does not exist");

        UpdateManager.update(App.DATA_BASE, TableCustomerOrder.class)
            .set("status", status)
            .set("updated_at", System.currentTimeMillis())
            .where(Expression.of("COALESCE(NULLIF(id, 0), rowid)").isEqualTo(orderId))
            .execute();
    }

    public static void deleteOrder(int orderId) {
        validateOrderId(orderId);
        if (getOrderById(orderId) == null) throw new IllegalArgumentException("Order with id " + orderId + " does not exist");

        DeletionManager.delete(App.DATA_BASE, TableOrderItem.class)
            .where(Expression.of("order_id").isEqualTo(orderId))
            .execute();

        DeletionManager.delete(App.DATA_BASE, TableCustomerOrder.class)
            .where(Expression.of("COALESCE(NULLIF(id, 0), rowid)").isEqualTo(orderId))
            .execute();
    }

    private static void validateOrderId(int orderId) {
        if (orderId <= 0) throw new IllegalArgumentException("Order id must be greater than 0");
    }

    private static String normalizeStatus(String status) {
        status = requireNonBlank(status, "Status").toUpperCase();
        if (!status.equals("PENDING") && !status.equals("CONFIRMED") && !status.equals("READY") && !status.equals("CANCELLED")) {
            throw new IllegalArgumentException("Status must be one of PENDING, CONFIRMED, READY, CANCELLED");
        }
        return status;
    }

    private static String normalizeEmail(String email) {
        email = requireNonBlank(email, "Customer email").toLowerCase();
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) throw new IllegalArgumentException("Customer email is not valid");
        return email;
    }

    private static String requireNonBlank(String value, String label) {
        if (value == null) throw new IllegalArgumentException(label + " cannot be null");
        final String normalizedValue = value.trim();
        if (normalizedValue.isEmpty()) throw new IllegalArgumentException(label + " cannot be empty");
        return normalizedValue;
    }
}