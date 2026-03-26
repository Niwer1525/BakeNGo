package niwer.bakengo.endpoints.auth;

import java.util.LinkedHashMap;
import java.util.Map;

import io.javalin.http.Context;
import niwer.bakengo.endpoints.EndpointUtils;
import niwer.bakengo.endpoints.IEndpoint;
import niwer.bakengo.objects.User;
import niwer.bakengo.tables.TableUser;

public class PostAuthSignUp implements IEndpoint {

    @Override
    public String path() {
        return "/api/auth/sign-up";
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public void handle(Context ctx) {
        final var BODY = EndpointUtils.parseJsonBody(ctx.body());
        final String EMAIL = EndpointUtils.getRequiredString(BODY, "email");
        final String PASSWORD = EndpointUtils.getRequiredString(BODY, "password");

        if (TableUser.doesUserExist(EMAIL)) {
            ctx.status(409).result("An account already exists for this email");
            return;
        }

        try {
            TableUser.createAccount(EMAIL, PASSWORD);
        } catch (RuntimeException error) {
            ctx.status(400).result("Could not create account with provided credentials");
            return;
        }

        final User USER = TableUser.getUserByEmail(EMAIL);
        final Map<String, Object> RESPONSE = new LinkedHashMap<>();
        RESPONSE.put("email", USER.email());
        RESPONSE.put("is_admin", USER.isAdmin());

        ctx.status(201).json(RESPONSE);
    }
}
