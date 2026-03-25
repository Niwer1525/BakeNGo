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
        final int productId = Integer.parseInt(ctx.pathParam("id"));
        final var body = EndpointUtils.parseJsonBody(ctx.body());
        final int stock = EndpointUtils.getRequiredInt(body, "stock");
        final int priceCents = EndpointUtils.getRequiredInt(body, "price_cents");
        final boolean isActive = EndpointUtils.getOptionalBoolean(body, "is_active", true);

        TableProduct.updateProductDetails(productId, stock, priceCents, isActive);
        ctx.json(ApiMappers.product(TableProduct.getProductById(productId)));
    }
}
