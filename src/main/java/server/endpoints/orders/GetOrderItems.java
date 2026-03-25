package server.endpoints.orders;

import io.javalin.http.Context;
import server.endpoints.ApiMappers;
import server.endpoints.IEndpoint;
import server.tables.TableOrderItem;

public class GetOrderItems implements IEndpoint {

    @Override
    public String path() {
        return "/api/orders/{id}/items";
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }

    @Override
    public void handle(Context ctx) {
        final int ORDER_ID = Integer.parseInt(ctx.pathParam("id"));
        ctx.json(ApiMappers.orderItems(TableOrderItem.getItemsByOrderId(ORDER_ID)));
    }
}