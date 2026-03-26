package niwer.bakengo.endpoints.pickupslots;

import io.javalin.http.Context;
import niwer.bakengo.endpoints.IEndpoint;
import niwer.bakengo.tables.TablePickupSlot;

public class DeletePickupSlot implements IEndpoint {

    @Override
    public String path() {
        return "/api/pickup-slots/{id}";
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.DELETE;
    }

    @Override
    public void handle(Context ctx) {
        final int ID = Integer.parseInt(ctx.pathParam("id"));
        TablePickupSlot.deletePickupSlot(ID);
        ctx.status(204);
    }
}