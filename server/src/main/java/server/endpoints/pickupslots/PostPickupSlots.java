package server.endpoints.pickupslots;

import io.javalin.http.Context;
import server.endpoints.ApiMappers;
import server.endpoints.EndpointUtils;
import server.endpoints.IEndpoint;
import server.tables.TablePickupSlot;

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
        final var body = EndpointUtils.parseJsonBody(ctx.body());

        final String day = EndpointUtils.getRequiredString(body, "day");
        final String startTime = EndpointUtils.getRequiredString(body, "start_time");
        final String endTime = EndpointUtils.getRequiredString(body, "end_time");
        final int capacity = EndpointUtils.getRequiredInt(body, "capacity");
        final boolean isEnabled = EndpointUtils.getOptionalBoolean(body, "is_enabled", true);

        TablePickupSlot.addPickupSlot(day, startTime, endTime, capacity, isEnabled);
        ctx.status(201).json(ApiMappers.pickupSlots(TablePickupSlot.getAllPickupSlots()));
    }
}