package server.endpoints.products;

import io.javalin.http.Context;
import server.endpoints.ApiMappers;
import server.endpoints.EndpointUtils;
import server.endpoints.IEndpoint;
import server.tables.TableProduct;

public class PutProductDetails implements IEndpoint {

    @Override
    public String path() {
        return "/api/products/{id}";
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
        final int PRICE_CENTS = EndpointUtils.getRequiredInt(BODY, "price_cents");
        final boolean IS_ENABLED = EndpointUtils.getOptionalBoolean(BODY, "is_active", true);

        TableProduct.updateProductDetails(PRODUCT_ID, STOCK, PRICE_CENTS, IS_ENABLED);
        ctx.json(ApiMappers.product(TableProduct.getProductById(PRODUCT_ID)));
    }
}
