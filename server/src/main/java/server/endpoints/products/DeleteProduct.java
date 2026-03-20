package server.endpoints.products;

import io.javalin.http.Context;
import server.endpoints.IEndpoint;
import server.tables.TableProduct;

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
        final int productId = Integer.parseInt(ctx.pathParam("id"));
        TableProduct.deleteProduct(productId);
        ctx.status(204);
    }
}