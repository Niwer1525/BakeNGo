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
        final int orderId = Integer.parseInt(ctx.pathParam("id"));
        final var body = EndpointUtils.parseJsonBody(ctx.body());
        final String status = EndpointUtils.getRequiredString(body, "status");

        TableCustomerOrder.updateOrderStatus(orderId, status);
        ctx.json(ApiMappers.order(TableCustomerOrder.getOrderById(orderId)));
    }
}