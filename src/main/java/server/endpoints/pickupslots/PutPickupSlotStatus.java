package server.endpoints.pickupslots;

import io.javalin.http.Context;
import server.endpoints.EndpointUtils;
import server.endpoints.IEndpoint;
import server.tables.TablePickupSlot;

public class PutPickupSlotStatus implements IEndpoint {

    @Override
    public String path() {
        return "/api/pickup-slots/{id}/status";
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.PUT;
    }

    @Override
    public void handle(Context ctx) {
        final int id = Integer.parseInt(ctx.pathParam("id"));
        final var body = EndpointUtils.parseJsonBody(ctx.body());
        final boolean isEnabled = EndpointUtils.getRequiredBoolean(body, "is_enabled");

        TablePickupSlot.updateEnabled(id, isEnabled);
        ctx.json(TablePickupSlot.getPickupSlotById(id));
    }
}
