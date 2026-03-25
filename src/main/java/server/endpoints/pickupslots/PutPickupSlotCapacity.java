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
        final int ID = Integer.parseInt(ctx.pathParam("id"));
        final var BODY = EndpointUtils.parseJsonBody(ctx.body());
        final int CAPACITY = EndpointUtils.getRequiredInt(BODY, "capacity");

        TablePickupSlot.updateCapacity(ID, CAPACITY);
        ctx.json(TablePickupSlot.getPickupSlotById(ID));
    }
}