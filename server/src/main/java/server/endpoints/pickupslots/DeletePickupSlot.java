package server.endpoints.pickupslots;

import io.javalin.http.Context;
import server.endpoints.IEndpoint;
import server.tables.TablePickupSlot;

public class DeletePickupSlot implements IEndpoint {

    @Override
    public String path() {
        return "/api/pickup-slots/{label}";
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.DELETE;
    }

    @Override
    public void handle(Context ctx) {
        final String label = ctx.pathParam("label");
        TablePickupSlot.deletePickupSlot(label);
        ctx.status(204);
    }
}