package niwer.bakengo.endpoints.orders;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;

import io.javalin.http.Context;
import niwer.bakengo.endpoints.ApiMappers;
import niwer.bakengo.endpoints.EndpointUtils;
import niwer.bakengo.endpoints.IEndpoint;
import niwer.bakengo.objects.CustomerOrder;
import niwer.bakengo.objects.Product;
import niwer.bakengo.tables.TableCustomerOrder;
import niwer.bakengo.tables.TableOrderItem;
import niwer.bakengo.tables.TableProduct;

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
        final var BODY = EndpointUtils.parseJsonBody(ctx.body());

        final Integer USER_ID = EndpointUtils.getOptionalInt(BODY, "user_id", null);
        final String CUSTOMER_EMAIL = EndpointUtils.getRequiredString(BODY, "customer_email");
        final String PICKUP_SLOT = EndpointUtils.getRequiredString(BODY, "pickup_slot");
        final String PICKUP_DATE = EndpointUtils.getRequiredString(BODY, "pickup_date");
        final JsonArray ITEMS_JSON = EndpointUtils.getRequiredArray(BODY, "items");

        if (ITEMS_JSON.isEmpty()) throw new IllegalArgumentException("Order items cannot be empty");

        final List<OrderDraftItem> ITEMS = new ArrayList<>();
        int totalCents = 0;

        for (int i = 0; i < ITEMS_JSON.size(); i++) {
            final var ITEM_JSON = ITEMS_JSON.get(i).getAsJsonObject();
            final int PRODUCT_ID = EndpointUtils.getRequiredInt(ITEM_JSON, "product_id");
            final int QUANTITY = EndpointUtils.getRequiredInt(ITEM_JSON, "quantity");
            if (QUANTITY <= 0) throw new IllegalArgumentException("Quantity must be greater than 0");

            final Product PRODUCT = TableProduct.getProductById(PRODUCT_ID);
            if (PRODUCT == null) throw new IllegalArgumentException("Product with id " + PRODUCT_ID + " does not exist");
            if (!PRODUCT.isActive()) throw new IllegalArgumentException("Product with id " + PRODUCT_ID + " is not active");
            if (PRODUCT.stock() < QUANTITY) throw new IllegalArgumentException("Insufficient stock for product id " + PRODUCT_ID);

            final int LINE_TOTAL = PRODUCT.priceCents() * QUANTITY;
            totalCents += LINE_TOTAL;
            ITEMS.add(new OrderDraftItem(PRODUCT_ID, QUANTITY, PRODUCT.priceCents(), PRODUCT.stock()));
        }

        final CustomerOrder ORDER = TableCustomerOrder.addOrder(USER_ID, CUSTOMER_EMAIL, PICKUP_SLOT, PICKUP_DATE, totalCents);
        if (ORDER == null) throw new IllegalStateException("Failed to create order");

        for (final OrderDraftItem ITEM : ITEMS) {
            TableOrderItem.addOrderItem(ORDER.id(), ITEM.productId(), ITEM.quantity(), ITEM.unitPriceCents());
            TableProduct.updateStock(ITEM.productId(), ITEM.initialStock() - ITEM.quantity());
        }

        final Map<String, Object> RESPONSE = new LinkedHashMap<>();
        RESPONSE.put("order", ApiMappers.order(TableCustomerOrder.getOrderById(ORDER.id())));
        RESPONSE.put("items", ApiMappers.orderItems(TableOrderItem.getItemsByOrderId(ORDER.id())));
        ctx.status(201).json(RESPONSE);
    }

    private static record OrderDraftItem(int productId, int quantity, int unitPriceCents, int initialStock) {}
}