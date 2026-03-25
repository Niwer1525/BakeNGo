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
        final var body = EndpointUtils.parseJsonBody(ctx.body());

        final String name = EndpointUtils.getRequiredString(body, "name");
        final String description = EndpointUtils.getOptionalString(body, "description", "");
        final int priceCents = EndpointUtils.getRequiredInt(body, "price_cents");
        final int stock = EndpointUtils.getRequiredInt(body, "stock");
        final boolean isActive = EndpointUtils.getOptionalBoolean(body, "is_active", true);

        TableProduct.addProduct(name, description, priceCents, stock, isActive);
        ctx.status(201).json(ApiMappers.products(TableProduct.getAllProducts()));
    }

}