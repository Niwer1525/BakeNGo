package niwer.bakengo.endpoints.pickupslots;

import io.javalin.http.Context;
import niwer.bakengo.endpoints.ApiMappers;
import niwer.bakengo.endpoints.EndpointUtils;
import niwer.bakengo.endpoints.IEndpoint;
import niwer.bakengo.tables.TablePickupSlot;

public class PostPickupSlots implements IEndpoint {

    @Override
    public String path() {
        return "/api/pickup-slots";
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public void handle(Context ctx) {
        final var BODY = EndpointUtils.parseJsonBody(ctx.body());

        final String DAY = EndpointUtils.getRequiredString(BODY, "day");
        final String START_TIME = EndpointUtils.getRequiredString(BODY, "start_time");
        final String END_TIME = EndpointUtils.getRequiredString(BODY, "end_time");
        final int CAPACITY = EndpointUtils.getRequiredInt(BODY, "capacity");
        final boolean IS_ENABLED = EndpointUtils.getOptionalBoolean(BODY, "is_enabled", true);

        TablePickupSlot.addPickupSlot(DAY, START_TIME, END_TIME, CAPACITY, IS_ENABLED);
        ctx.status(201).json(ApiMappers.pickupSlots(TablePickupSlot.getAllPickupSlots()));
    }
}