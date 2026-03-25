package server.endpoints.products;

import io.javalin.http.Context;
import server.endpoints.ApiMappers;
import server.endpoints.EndpointUtils;
import server.endpoints.IEndpoint;
import server.tables.TableProduct;

public class PostProducts implements IEndpoint {

    @Override
    public String path() {
        return "/api/products";
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public void handle(Context ctx) {
        final var BODY = EndpointUtils.parseJsonBody(ctx.body());

        final String NAME = EndpointUtils.getRequiredString(BODY, "name");
        final String DESCRIPTION = EndpointUtils.getOptionalString(BODY, "description", "");
        final int PRICE_CENTS = EndpointUtils.getRequiredInt(BODY, "price_cents");
        final int STOCK = EndpointUtils.getRequiredInt(BODY, "stock");
        final boolean IS_ENABLED = EndpointUtils.getOptionalBoolean(BODY, "is_active", true);

        TableProduct.addProduct(NAME, DESCRIPTION, PRICE_CENTS, STOCK, IS_ENABLED);
        ctx.status(201).json(ApiMappers.products(TableProduct.getAllProducts()));
    }

}