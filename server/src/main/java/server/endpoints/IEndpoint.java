package server.endpoints;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

/**
 * Interface representing an API endpoint in the application. Each endpoint must specify its path, HTTP method, and handler function.
 * 
 * @author Niwer
 */
public interface IEndpoint {
    String path();
    
    HttpMethod method();

    void handle(Context handler);

    /**
     * Registers the given endpoint class with the provided JavalinConfig. The endpoint class must have a no-argument constructor.
     * 
     * @param cfg The JavalinConfig to register the endpoint with
     * @param endpointClass The class of the endpoint to register
     * @return The instance of the registered endpoint
     */
    public static IEndpoint register(JavalinConfig cfg, Class<? extends IEndpoint> endpointClass) {
        try {
            final IEndpoint END_POINT = endpointClass.getDeclaredConstructor().newInstance();
            switch (END_POINT.method()) {
                case GET -> cfg.routes.get(END_POINT.path(), END_POINT::handle);
                case POST -> cfg.routes.post(END_POINT.path(), END_POINT::handle);
                case PUT -> cfg.routes.put(END_POINT.path(), END_POINT::handle);
                case DELETE -> cfg.routes.delete(END_POINT.path(), END_POINT::handle);
            }
            return END_POINT;
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate endpoint: " + endpointClass.getName(), e);
        }
    }

    public static enum HttpMethod {
        GET, POST, PUT, DELETE
    }
}