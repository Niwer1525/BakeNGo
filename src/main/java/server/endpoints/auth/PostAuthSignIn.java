package server.endpoints.auth;

import java.util.LinkedHashMap;
import java.util.Map;

import io.javalin.http.Context;
import server.endpoints.EndpointUtils;
import server.endpoints.IEndpoint;
import server.objects.User;
import server.tables.TableUser;

public class PostAuthSignIn implements IEndpoint {

    @Override
    public String path() {
        return "/api/auth/sign-in";
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

        final User user = TableUser.signIn(email, password);
        if (user == null) {
            ctx.status(401).result("Invalid email or password");
            return;
        }

        final Map<String, Object> response = new LinkedHashMap<>();
        response.put("email", user.email());
        response.put("is_admin", user.isAdmin());

        ctx.json(response);
    }
}
