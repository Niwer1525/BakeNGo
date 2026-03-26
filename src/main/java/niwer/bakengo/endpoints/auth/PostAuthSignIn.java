package niwer.bakengo.endpoints.auth;

import java.util.LinkedHashMap;
import java.util.Map;

import io.javalin.http.Context;
import niwer.bakengo.endpoints.EndpointUtils;
import niwer.bakengo.endpoints.IEndpoint;
import niwer.bakengo.objects.User;
import niwer.bakengo.tables.TableUser;

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
        final var BODY = EndpointUtils.parseJsonBody(ctx.body());
        final String EMAIL = EndpointUtils.getRequiredString(BODY, "email");
        final String PASSWORD = EndpointUtils.getRequiredString(BODY, "password");

        final User USER = TableUser.signIn(EMAIL, PASSWORD);
        if (USER == null) {
            ctx.status(401).result("Invalid email or password");
            return;
        }

        final Map<String, Object> RESPONSE = new LinkedHashMap<>();
        RESPONSE.put("email", USER.email());
        RESPONSE.put("is_admin", USER.isAdmin());

        ctx.json(RESPONSE);
    }
}
