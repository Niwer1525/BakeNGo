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
        final boolean includeDisabled = ctx.queryParamAsClass("include_disabled", Boolean.class).getOrDefault(false);
        if (includeDisabled) ctx.json(ApiMappers.pickupSlots(TablePickupSlot.getAllPickupSlots()));
        else ctx.json(ApiMappers.pickupSlots(TablePickupSlot.getEnabledPickupSlots()));
    }
}