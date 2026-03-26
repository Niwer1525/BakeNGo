package niwer.bakengo.endpoints.products;

import io.javalin.http.Context;
import niwer.bakengo.endpoints.ApiMappers;
import niwer.bakengo.endpoints.IEndpoint;
import niwer.bakengo.tables.TableProduct;

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