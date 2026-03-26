package niwer.bakengo.endpoints.products;

import io.javalin.http.Context;
import niwer.bakengo.endpoints.IEndpoint;
import niwer.bakengo.tables.TableProduct;

public class DeleteProduct implements IEndpoint {

    @Override
    public String path() {
        return "/api/products/{id}";
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.DELETE;
    }

    @Override
    public void handle(Context ctx) {
        final int PRODUCT_ID = Integer.parseInt(ctx.pathParam("id"));
        TableProduct.deleteProduct(PRODUCT_ID);
        ctx.status(204);
    }
}