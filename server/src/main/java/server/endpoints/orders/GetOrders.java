package server.endpoints.orders;

import io.javalin.http.Context;
import server.endpoints.ApiMappers;
import server.endpoints.IEndpoint;
import server.tables.TableCustomerOrder;

public class GetOrders implements IEndpoint {

    @Override
    public String path() {
        return "/api/orders";
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }

    @Override
    public void handle(Context ctx) {
        ctx.json(ApiMappers.orders(TableCustomerOrder.getAllOrders()));
    }
}