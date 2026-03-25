package server.endpoints.pickupslots;

import io.javalin.http.Context;
import server.endpoints.IEndpoint;
import server.tables.TablePickupSlot;

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
        final int id = Integer.parseInt(ctx.pathParam("id"));
        TablePickupSlot.deletePickupSlot(id);
        ctx.status(204);
    }
}