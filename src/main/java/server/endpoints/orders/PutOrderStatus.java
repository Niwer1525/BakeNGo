package server.endpoints.orders;

import io.javalin.http.Context;
import server.endpoints.ApiMappers;
import server.endpoints.EndpointUtils;
import server.endpoints.IEndpoint;
import server.tables.TableCustomerOrder;

public class PutOrderStatus implements IEndpoint {

    @Override
    public String path() {
        return "/api/orders/{id}/status";
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.PUT;
    }

    @Override
    public void handle(Context ctx) {
        final int ORDER_ID = Integer.parseInt(ctx.pathParam("id"));
        final var BODY = EndpointUtils.parseJsonBody(ctx.body());
        final String STATUS = EndpointUtils.getRequiredString(BODY, "status");

        TableCustomerOrder.updateOrderStatus(ORDER_ID, STATUS);
        ctx.json(ApiMappers.order(TableCustomerOrder.getOrderById(ORDER_ID)));
    }
}