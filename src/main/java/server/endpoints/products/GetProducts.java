package server.endpoints.products;

import io.javalin.http.Context;
import server.endpoints.ApiMappers;
import server.endpoints.IEndpoint;
import server.tables.TableProduct;

public class GetProducts implements IEndpoint {

    @Override
    public String path() {
        return "/api/products";
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }

    @Override
    public void handle(Context ctx) {
        final boolean INCLUDE_INACTIVE = ctx.queryParamAsClass("include_inactive", Boolean.class).getOrDefault(false);
        if(INCLUDE_INACTIVE) ctx.json(ApiMappers.products(TableProduct.getAllProducts()));
        else ctx.json(ApiMappers.products(TableProduct.getActiveProducts()));
    }

}