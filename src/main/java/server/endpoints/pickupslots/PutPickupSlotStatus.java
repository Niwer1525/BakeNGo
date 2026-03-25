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
        final int ID = Integer.parseInt(ctx.pathParam("id"));
        final var BODY = EndpointUtils.parseJsonBody(ctx.body());
        final boolean IS_ENABLED = EndpointUtils.getRequiredBoolean(BODY, "is_enabled");

        TablePickupSlot.updateEnabled(ID, IS_ENABLED);
        ctx.json(TablePickupSlot.getPickupSlotById(ID));
    }
}
