package server.endpoints.products;

import io.javalin.http.Context;
import server.endpoints.ApiMappers;
import server.endpoints.EndpointUtils;
import server.endpoints.IEndpoint;
import server.tables.TableProduct;

public class PutProductStock implements IEndpoint {

    @Override
    public String path() {
        return "/api/products/{id}/stock";
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.PUT;
    }

    @Override
    public void handle(Context ctx) {
        final int PRODUCT_ID = Integer.parseInt(ctx.pathParam("id"));
        final var BODY = EndpointUtils.parseJsonBody(ctx.body());
        final int STOCK = EndpointUtils.getRequiredInt(BODY, "stock");

        TableProduct.updateStock(PRODUCT_ID, STOCK);
        ctx.json(ApiMappers.product(TableProduct.getProductById(PRODUCT_ID)));
    }
}
