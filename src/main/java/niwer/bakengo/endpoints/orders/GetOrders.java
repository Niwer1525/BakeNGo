package niwer.bakengo.endpoints.orders;

import io.javalin.http.Context;
import niwer.bakengo.endpoints.ApiMappers;
import niwer.bakengo.endpoints.IEndpoint;
import niwer.bakengo.tables.TableCustomerOrder;

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