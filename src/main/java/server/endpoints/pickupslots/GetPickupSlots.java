package server.endpoints.pickupslots;

import io.javalin.http.Context;
import server.endpoints.ApiMappers;
import server.endpoints.IEndpoint;
import server.tables.TablePickupSlot;

public class GetPickupSlots implements IEndpoint {

    @Override
    public String path() {
        return "/api/pickup-slots";
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }

    @Override
    public void handle(Context ctx) {
        final boolean INCLUDED_DISABLED = ctx.queryParamAsClass("include_disabled", Boolean.class).getOrDefault(false);
        if (INCLUDED_DISABLED) ctx.json(ApiMappers.pickupSlots(TablePickupSlot.getAllPickupSlots()));
        else ctx.json(ApiMappers.pickupSlots(TablePickupSlot.getEnabledPickupSlots()));
    }
}