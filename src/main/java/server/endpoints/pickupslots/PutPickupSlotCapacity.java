package server.endpoints.pickupslots;

import io.javalin.http.Context;
import server.endpoints.EndpointUtils;
import server.endpoints.IEndpoint;
import server.tables.TablePickupSlot;

public class PutPickupSlotCapacity implements IEndpoint {

    @Override
    public String path() {
        return "/api/pickup-slots/{id}/capacity";
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.PUT;
    }

    @Override
    public void handle(Context ctx) {
        final int id = Integer.parseInt(ctx.pathParam("id"));
        final var body = EndpointUtils.parseJsonBody(ctx.body());
        final int capacity = EndpointUtils.getRequiredInt(body, "capacity");

        TablePickupSlot.updateCapacity(id, capacity);
        ctx.json(TablePickupSlot.getPickupSlotById(id));
    }
}