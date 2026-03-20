package server.endpoints.orders;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;

import io.javalin.http.Context;
import server.endpoints.ApiMappers;
import server.endpoints.EndpointUtils;
import server.endpoints.IEndpoint;
import server.objects.CustomerOrder;
import server.objects.Product;
import server.tables.TableCustomerOrder;
import server.tables.TableOrderItem;
import server.tables.TableProduct;

public class PostOrders implements IEndpoint {

    @Override
    public String path() {
        return "/api/orders";
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public void handle(Context ctx) {
        final var body = EndpointUtils.parseJsonBody(ctx.body());

        final Integer userId = EndpointUtils.getOptionalInt(body, "user_id", null);
        final String customerEmail = EndpointUtils.getRequiredString(body, "customer_email");
        final String pickupSlot = EndpointUtils.getRequiredString(body, "pickup_slot");
        final String pickupDate = EndpointUtils.getRequiredString(body, "pickup_date");
        final JsonArray itemsJson = EndpointUtils.getRequiredArray(body, "items");

        if (itemsJson.isEmpty()) throw new IllegalArgumentException("Order items cannot be empty");

        final List<OrderDraftItem> items = new ArrayList<>();
        int totalCents = 0;

        for (int i = 0; i < itemsJson.size(); i++) {
            final var itemJson = itemsJson.get(i).getAsJsonObject();
            final int productId = EndpointUtils.getRequiredInt(itemJson, "product_id");
            final int quantity = EndpointUtils.getRequiredInt(itemJson, "quantity");
            if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than 0");

            final Product product = TableProduct.getProductById(productId);
            if (product == null) throw new IllegalArgumentException("Product with id " + productId + " does not exist");
            if (!product.isActive()) throw new IllegalArgumentException("Product with id " + productId + " is not active");
            if (product.stock() < quantity) throw new IllegalArgumentException("Insufficient stock for product id " + productId);

            final int lineTotal = product.priceCents() * quantity;
            totalCents += lineTotal;
            items.add(new OrderDraftItem(productId, quantity, product.priceCents(), product.stock()));
        }

        final CustomerOrder order = TableCustomerOrder.addOrder(userId, customerEmail, pickupSlot, pickupDate, totalCents);
        if (order == null) throw new IllegalStateException("Failed to create order");

        for (final OrderDraftItem item : items) {
            TableOrderItem.addOrderItem(order.id(), item.productId(), item.quantity(), item.unitPriceCents());
            TableProduct.updateStock(item.productId(), item.initialStock() - item.quantity());
        }

        final Map<String, Object> response = new LinkedHashMap<>();
        response.put("order", ApiMappers.order(TableCustomerOrder.getOrderById(order.id())));
        response.put("items", ApiMappers.orderItems(TableOrderItem.getItemsByOrderId(order.id())));
        ctx.status(201).json(response);
    }

    private static record OrderDraftItem(int productId, int quantity, int unitPriceCents, int initialStock) {}
}