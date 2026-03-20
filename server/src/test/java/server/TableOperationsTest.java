package server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import niwer.queryon.queries.interaction.DeletionManager;
import server.objects.AnalyticsMetric;
import server.objects.CustomerOrder;
import server.objects.OrderItem;
import server.objects.Product;
import server.objects.User;
import server.tables.TableAnalyticsMetric;
import server.tables.TableCustomerOrder;
import server.tables.TableOrderItem;
import server.tables.TablePickupSlot;
import server.tables.TableProduct;
import server.tables.TableUser;

class TableOperationsTest {

    @BeforeAll
    static void registerTables() {
        App.DATA_BASE.registerTable(TableProduct.class)
            .registerTable(TablePickupSlot.class)
            .registerTable(TableCustomerOrder.class)
            .registerTable(TableOrderItem.class)
            .registerTable(TableAnalyticsMetric.class)
            .registerTable(TableUser.class);
    }

    @BeforeEach
    void cleanTables() {
        DeletionManager.delete(App.DATA_BASE, TableOrderItem.class).execute();
        DeletionManager.delete(App.DATA_BASE, TableCustomerOrder.class).execute();

        DeletionManager.delete(App.DATA_BASE, TablePickupSlot.class).execute();

        DeletionManager.delete(App.DATA_BASE, TableProduct.class).execute();

        DeletionManager.delete(App.DATA_BASE, TableAnalyticsMetric.class).execute();

        DeletionManager.delete(App.DATA_BASE, TableAnalyticsMetric.class).execute();

        DeletionManager.delete(App.DATA_BASE, TableUser.class).execute();
    }

    @Test
    void productCrudWorks() {
        TableProduct.addProduct("Croissant", "Classic butter", 250, 30, true);

        final List<Product> products = TableProduct.getAllProducts();
        assertEquals(1, products.size());

        final Product product = products.get(0);
        assertEquals("Croissant", product.name());
        assertEquals(250, product.priceCents());
        assertEquals(30, product.stock());

        TableProduct.updateStock(product.id(), 24);
        final Product updated = TableProduct.getProductById(product.id());
        assertNotNull(updated);
        assertEquals(24, updated.stock());

        TableProduct.deleteProduct(product.id());
        assertTrue(TableProduct.getAllProducts().isEmpty());
    }

    @Test
    void productValidationRejectsBadValues() {
        assertThrows(IllegalArgumentException.class, () -> TableProduct.addProduct("", "desc", 250, 10, true));
        assertThrows(IllegalArgumentException.class, () -> TableProduct.addProduct("Croissant", "desc", -1, 10, true));
        assertThrows(IllegalArgumentException.class, () -> TableProduct.addProduct("Croissant", "desc", 100, -5, true));
        assertThrows(IllegalArgumentException.class, () -> TableProduct.updateStock(0, 5));
        assertThrows(IllegalArgumentException.class, () -> TableProduct.updateStock(1, -2));
    }

    @Test
    void pickupSlotValidationAndReadWorks() {
        TablePickupSlot.addPickupSlot("Morning", "07:30", "08:00", 20, true);
        assertEquals(1, TablePickupSlot.getEnabledPickupSlots().size());

        assertThrows(IllegalArgumentException.class, () -> TablePickupSlot.addPickupSlot("", "07:30", "08:00", 20, true));
        assertThrows(IllegalArgumentException.class, () -> TablePickupSlot.addPickupSlot("Slot", "07:30", "08:00", 0, true));
    }

    @Test
    void orderAndOrderItemsFlowWorks() {
        TableProduct.addProduct("Butter Croissant", "Classic", 250, 15, true);
        final Product product = TableProduct.getAllProducts().get(0);

        final CustomerOrder order = TableCustomerOrder.addOrder(null, "customer@bakery.com", "08:00", "2026-03-20", 500);
        assertNotNull(order);

        TableOrderItem.addOrderItem(order.id(), product.id(), 2, product.priceCents());

        final List<OrderItem> items = TableOrderItem.getItemsByOrderId(order.id());
        assertEquals(1, items.size());
        assertEquals(2, items.get(0).quantity());
        assertEquals(500, items.get(0).lineTotalCents());

        TableCustomerOrder.updateOrderStatus(order.id(), "CONFIRMED");
        final CustomerOrder updated = TableCustomerOrder.getOrderById(order.id());
        assertNotNull(updated);
        assertEquals("CONFIRMED", updated.status());

        TableCustomerOrder.deleteOrder(order.id());
        assertTrue(TableOrderItem.getItemsByOrderId(order.id()).isEmpty());
    }

    @Test
    void orderValidationRejectsBadValues() {
        assertThrows(IllegalArgumentException.class, () -> TableCustomerOrder.addOrder(null, "", "08:00", "2026-03-20", 100));
        assertThrows(IllegalArgumentException.class, () -> TableCustomerOrder.addOrder(0, "user@mail.com", "08:00", "2026-03-20", 100));
        assertThrows(IllegalArgumentException.class, () -> TableCustomerOrder.addOrder(null, "bad-email", "08:00", "2026-03-20", 100));
        assertThrows(IllegalArgumentException.class, () -> TableCustomerOrder.updateOrderStatus(1, "UNKNOWN"));
        assertThrows(IllegalArgumentException.class, () -> TableOrderItem.addOrderItem(1, 1, 0, 100));
    }

    @Test
    void pickupSlotCrudAndValidationWork() {
        TablePickupSlot.addPickupSlot("Morning", "07:30", "08:00", 20, true);

        TablePickupSlot.updateCapacity("Morning", 25);
        assertEquals(25, TablePickupSlot.getPickupSlotByLabel("Morning").capacity());

        TablePickupSlot.deletePickupSlot("Morning");
        assertTrue(TablePickupSlot.getAllPickupSlots().isEmpty());

        assertThrows(IllegalArgumentException.class, () -> TablePickupSlot.updateCapacity("", 10));
        assertThrows(IllegalArgumentException.class, () -> TablePickupSlot.updateCapacity("Unknown", 10));
        assertThrows(IllegalArgumentException.class, () -> TablePickupSlot.deletePickupSlot("Unknown"));
    }

    @Test
    void userAuthFlowAndValidationWork() {
        final String email = "Baker+Test@Shop.com";
        final String password = "very-safe-password";

        TableUser.addUser(email, password, false);

        final User user = TableUser.getUserByEmail("baker+test@shop.com");
        assertNotNull(user);
        assertEquals("baker+test@shop.com", user.email());
        assertTrue(TableUser.doesUserExist("BAKER+TEST@SHOP.COM"));
        assertTrue(TableUser.authenticateUser(email, password));
        assertFalse(TableUser.authenticateUser(email, "bad-password"));
        assertFalse(TableUser.isUserAdmin(email));

        assertThrows(IllegalArgumentException.class, () -> TableUser.addUser("", "pwd", false));
        assertThrows(IllegalArgumentException.class, () -> TableUser.addUser("bad", "pwd", false));
        assertThrows(IllegalArgumentException.class, () -> TableUser.addUser("valid@mail.com", "", false));
    }

    @Test
    void analyticsMetricInsertAndValidationWork() {
        TableAnalyticsMetric.addMetric("top_seller", "Butter Croissant", "2026-03-20");

        final List<AnalyticsMetric> metrics = TableAnalyticsMetric.getAllMetrics();
        assertEquals(1, metrics.size());
        assertEquals("top_seller", metrics.get(0).metricKey());
        assertEquals("Butter Croissant", metrics.get(0).metricValue());

        assertThrows(IllegalArgumentException.class, () -> TableAnalyticsMetric.addMetric("", "x", "2026-03-20"));
        assertThrows(IllegalArgumentException.class, () -> TableAnalyticsMetric.addMetric("k", "", "2026-03-20"));
        assertThrows(IllegalArgumentException.class, () -> TableAnalyticsMetric.addMetric("k", "v", ""));
    }
}
