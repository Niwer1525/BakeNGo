package server.endpoints.orders;

import io.javalin.http.Context;
import server.endpoints.IEndpoint;
import server.tables.TableCustomerOrder;

public class DeleteOrder implements IEndpoint {

    @Override
    public String path() {
        return "/api/orders/{id}";
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.DELETE;
    }

    @Override
    public void handle(Context ctx) {
        final int ORDER_ID = Integer.parseInt(ctx.pathParam("id"));
        TableCustomerOrder.deleteOrder(ORDER_ID);
        ctx.status(204);
    }
}