package server.endpoints.auth;

import java.util.LinkedHashMap;
import java.util.Map;

import io.javalin.http.Context;
import server.endpoints.EndpointUtils;
import server.endpoints.IEndpoint;
import server.objects.User;
import server.tables.TableUser;

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
        final var body = EndpointUtils.parseJsonBody(ctx.body());
        final String email = EndpointUtils.getRequiredString(body, "email");
        final String password = EndpointUtils.getRequiredString(body, "password");

        if (TableUser.doesUserExist(email)) {
            ctx.status(409).result("An account already exists for this email");
            return;
        }

        try {
            TableUser.createAccount(email, password);
        } catch (RuntimeException error) {
            ctx.status(400).result("Could not create account with provided credentials");
            return;
        }

        final User user = TableUser.getUserByEmail(email);

        final Map<String, Object> response = new LinkedHashMap<>();
        response.put("email", user.email());
        response.put("is_admin", user.isAdmin());

        ctx.status(201).json(response);
    }
}
