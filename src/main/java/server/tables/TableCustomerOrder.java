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

    /**
     * Adds a new customer order to the database. The order status is automatically set to "PENDING" and the creation and update timestamps are set to the current time.
     * 
     * @param userId The id of the user placing the order (can be null if the order is placed by a guest, but if provided must be greater than 0)
     * @param customerEmail The email of the customer placing the order (must be a valid email format and non-blank)
     * @param pickupSlot The pickup slot selected for the order (must be non-blank)
     * @param pickupDate The pickup date selected for the order (must be non-blank)
     * @param totalCents The total price of the order in cents (must be non-negative)
     * @return The newly created CustomerOrder object representing the order that was added to the database
     */
    public static synchronized CustomerOrder addOrder(Integer userId, String customerEmail, String pickupSlot, String pickupDate, int totalCents) {
        if (userId != null && userId <= 0) throw new IllegalArgumentException("User id must be greater than 0");
        customerEmail = normalizeEmail(customerEmail);
        pickupSlot = App.requireNonBlank(pickupSlot, "Pickup slot");
        pickupDate = App.requireNonBlank(pickupDate, "Pickup date");
        if (totalCents < 0) throw new IllegalArgumentException("Total cannot be negative");

        final int ORDER_ID = getNextOrderId();
        final long NOW = System.currentTimeMillis();
        InsertionManager.insert(App.DATA_BASE, TableCustomerOrder.class,
            "id", "user_id", "customer_email", "status", "pickup_slot", "pickup_date", "total_cents", "created_at", "updated_at")
            .row(ORDER_ID, userId, customerEmail, "PENDING", pickupSlot, pickupDate, totalCents, NOW, NOW)
            .execute();

        return SelectionManager.select(App.DATA_BASE, TableCustomerOrder.class,
                "COALESCE(NULLIF(id, 0), rowid) AS id", "user_id", "customer_email", "status", "pickup_slot", "pickup_date", "total_cents", "created_at", "updated_at")
            .where(Expression.of("COALESCE(NULLIF(id, 0), rowid)").isEqualTo(ORDER_ID))
            .executeSerializable(CustomerOrder.class);
    }

    /**
     * Gets a customer order from the database by its id.
     * 
     * @param orderId The id of the order to get
     * @return The CustomerOrder object representing the order, or null if not found
     */
    public static CustomerOrder getOrderById(int orderId) {
        validateOrderId(orderId);

        return SelectionManager.select(App.DATA_BASE, TableCustomerOrder.class,
            "COALESCE(NULLIF(id, 0), rowid) AS id", "user_id", "customer_email", "status", "pickup_slot", "pickup_date", "total_cents", "created_at", "updated_at")
            .where(Expression.of("COALESCE(NULLIF(id, 0), rowid)").isEqualTo(orderId))
            .executeSerializable(CustomerOrder.class);
    }

    /**
     * Gets all customer orders from the database, ordered by creation date in descending order.
     * 
     * @return A list of all CustomerOrder objects representing the orders in the database, ordered by creation date in descending order. If there are no orders, returns an empty list.
     */
    public static List<CustomerOrder> getAllOrders() {
        final SelectionManager QUERY = SelectionManager.select(App.DATA_BASE, TableCustomerOrder.class,
            "COALESCE(NULLIF(id, 0), rowid) AS id", "user_id", "customer_email", "status", "pickup_slot", "pickup_date", "total_cents", "created_at", "updated_at")
            .orderBy("rowid", SelectionManager.EnumOrder.DESC);

        try {
            return QUERY.executeList(CustomerOrder.class);
        } catch (IllegalStateException ignored) {
            final CustomerOrder single = QUERY.executeSerializable(CustomerOrder.class);
            return single == null ? List.of() : List.of(single);
        }
    }

    /**
     * Updates the status of a customer order in the database.
     * 
     * @param orderId The id of the order to update
     * @param status The new status for the order (must be one of "PENDING", "CONFIRMED", "READY", "CANCELLED") 
     */
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

    /**
     * Deletes a customer order and all associated order items from the database by the order id.
     * 
     * @param orderId The id of the order to delete
     */
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
        status = App.requireNonBlank(status, "Status").toUpperCase();
        if (!status.equals("PENDING") && !status.equals("CONFIRMED") && !status.equals("READY") && !status.equals("CANCELLED"))
            throw new IllegalArgumentException("Status must be one of PENDING, CONFIRMED, READY, CANCELLED");
        
        return status;
    }

    private static String normalizeEmail(String email) {
        email = App.requireNonBlank(email, "Customer email").toLowerCase();
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) throw new IllegalArgumentException("Customer email is not valid");
        
        return email;
    }

    private static int getNextOrderId() {
        return getAllOrders().stream()
            .mapToInt(CustomerOrder::id)
            .max()
            .orElse(0) + 1;
    }
}