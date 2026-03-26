package niwer.bakengo.endpoints.products;

import io.javalin.http.Context;
import niwer.bakengo.endpoints.ApiMappers;
import niwer.bakengo.endpoints.EndpointUtils;
import niwer.bakengo.endpoints.IEndpoint;
import niwer.bakengo.tables.TableProduct;

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
