package server.endpoints;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import server.objects.CustomerOrder;
import server.objects.OrderItem;
import server.objects.PickupSlot;
import server.objects.Product;

/**
 * Utility class for mapping internal objects to API response formats.
 * Each method converts a specific object type into a Map representation suitable for JSON serialization in API responses.
 * 
 * @author Niwer
 */
public final class ApiMappers {

    private ApiMappers() {}

    /**
     * Maps a Product object to a Map<String, Object> for API responses.
     * 
     * @param product The Product object to be mapped.
     * @return A Map<String, Object> containing the product's properties in a format suitable for API responses.
     */
    public static Map<String, Object> product(Product product) {
        final Map<String, Object> MAP = new LinkedHashMap<>();
        MAP.put("id", product.id());
        MAP.put("name", product.name());
        MAP.put("description", product.description());
        MAP.put("price_cents", product.priceCents());
        MAP.put("stock", product.stock());
        MAP.put("is_active", product.isActive());
        return MAP;
    }

    /**
     * Maps a list of Product objects to a list of Map<String, Object> for API responses.
     * 
     * @param products The list of Product objects to be mapped.
     * @return A List<Map<String, Object>> where each Map represents a product's properties in a format suitable for API responses.
     */
    public static List<Map<String, Object>> products(List<Product> products) {
        return products.stream().map(ApiMappers::product).toList();
    }

    /**
     * Maps a PickupSlot object to a Map<String, Object> for API responses.
     * 
     * @param slot The PickupSlot object to be mapped.
     * @return A Map<String, Object> containing the pickup slot's properties in a format suitable for API responses.
     */
    public static Map<String, Object> pickupSlot(PickupSlot slot) {
        final Map<String, Object> MAP = new LinkedHashMap<>();
        MAP.put("id", slot.id());
        MAP.put("day", slot.day());
        MAP.put("start_time", slot.startTime());
        MAP.put("end_time", slot.endTime());
        MAP.put("capacity", slot.capacity());
        MAP.put("is_enabled", slot.isEnabled());
        return MAP;
    }

    /**
     * Maps a list of PickupSlot objects to a list of Map<String, Object> for API responses.
     * 
     * @param slots The list of PickupSlot objects to be mapped.
     * @return A List<Map<String, Object>> where each Map represents a pickup slot's properties in a format suitable for API responses.
     */
    public static List<Map<String, Object>> pickupSlots(List<PickupSlot> slots) {
        return slots.stream().map(ApiMappers::pickupSlot).toList();
    }

    public static Map<String, Object> order(CustomerOrder order) {
        final Map<String, Object> MAP = new LinkedHashMap<>();
        MAP.put("id", order.id());
        MAP.put("user_id", order.userId());
        MAP.put("customer_email", order.customerEmail());
        MAP.put("status", order.status());
        MAP.put("pickup_slot", order.pickupSlot());
        MAP.put("pickup_date", order.pickupDate());
        MAP.put("total_cents", order.totalCents());
        MAP.put("created_at", order.createdAt());
        MAP.put("updated_at", order.updatedAt());
        return MAP;
    }

    /**
     * Maps a list of CustomerOrder objects to a list of Map<String, Object> for API responses.
     * 
     * @param orders The list of CustomerOrder objects to be mapped.
     * @return A List<Map<String, Object>> where each Map represents a customer order's properties in a format suitable for API responses.
     */
    public static List<Map<String, Object>> orders(List<CustomerOrder> orders) {
        return orders.stream().map(ApiMappers::order).toList();
    }

    /**
     * Maps an OrderItem object to a Map<String, Object> for API responses.
     * 
     * @param item The OrderItem object to be mapped.
     * @return A Map<String, Object> containing the order item's properties in a format suitable for API responses.
     */
    public static Map<String, Object> orderItem(OrderItem item) {
        final Map<String, Object> MAP = new LinkedHashMap<>();
        MAP.put("id", item.id());
        MAP.put("order_id", item.orderId());
        MAP.put("product_id", item.productId());
        MAP.put("quantity", item.quantity());
        MAP.put("unit_price_cents", item.unitPriceCents());
        MAP.put("line_total_cents", item.lineTotalCents());
        return MAP;
    }

    /**
     * Maps a list of OrderItem objects to a list of Map<String, Object> for API responses.
     * 
     * @param items The list of OrderItem objects to be mapped.
     * @return A List<Map<String, Object>> where each Map represents an order item's properties in a format suitable for API responses.
     */
    public static List<Map<String, Object>> orderItems(List<OrderItem> items) {
        return items.stream().map(ApiMappers::orderItem).toList();
    }
}
