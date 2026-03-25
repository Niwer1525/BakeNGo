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

    public static Map<String, Object> product(Product product) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", product.id());
        map.put("name", product.name());
        map.put("description", product.description());
        map.put("price_cents", product.priceCents());
        map.put("stock", product.stock());
        map.put("is_active", product.isActive());
        return map;
    }

    public static List<Map<String, Object>> products(List<Product> products) {
        return products.stream().map(ApiMappers::product).toList();
    }

    public static Map<String, Object> pickupSlot(PickupSlot slot) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", slot.id());
        map.put("day", slot.day());
        map.put("start_time", slot.startTime());
        map.put("end_time", slot.endTime());
        map.put("capacity", slot.capacity());
        map.put("is_enabled", slot.isEnabled());
        return map;
    }

    public static List<Map<String, Object>> pickupSlots(List<PickupSlot> slots) {
        return slots.stream().map(ApiMappers::pickupSlot).toList();
    }

    public static Map<String, Object> order(CustomerOrder order) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", order.id());
        map.put("user_id", order.userId());
        map.put("customer_email", order.customerEmail());
        map.put("status", order.status());
        map.put("pickup_slot", order.pickupSlot());
        map.put("pickup_date", order.pickupDate());
        map.put("total_cents", order.totalCents());
        map.put("created_at", order.createdAt());
        map.put("updated_at", order.updatedAt());
        return map;
    }

    public static List<Map<String, Object>> orders(List<CustomerOrder> orders) {
        return orders.stream().map(ApiMappers::order).toList();
    }

    public static Map<String, Object> orderItem(OrderItem item) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", item.id());
        map.put("order_id", item.orderId());
        map.put("product_id", item.productId());
        map.put("quantity", item.quantity());
        map.put("unit_price_cents", item.unitPriceCents());
        map.put("line_total_cents", item.lineTotalCents());
        return map;
    }

    public static List<Map<String, Object>> orderItems(List<OrderItem> items) {
        return items.stream().map(ApiMappers::orderItem).toList();
    }
}
