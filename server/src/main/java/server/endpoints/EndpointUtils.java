package server.endpoints;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Utility class for endpoint handlers to parse and validate JSON request bodies.
 * 
 * @author Niwer
 */
public final class EndpointUtils {

    private EndpointUtils() {}

    public static JsonObject parseJsonBody(String body) {
        if (body == null || body.isBlank()) throw new IllegalArgumentException("Request body cannot be empty");
        try {
            return JsonParser.parseString(body).getAsJsonObject();
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid JSON payload");
        }
    }

    public static String getRequiredString(JsonObject json, String key) {
        if (!json.has(key) || json.get(key).isJsonNull()) throw new IllegalArgumentException("Missing required field: " + key);
        final String VALUE = json.get(key).getAsString();
        if (VALUE == null || VALUE.isBlank()) throw new IllegalArgumentException("Field " + key + " cannot be empty");
        return VALUE.trim();
    }

    public static String getOptionalString(JsonObject json, String key, String defaultValue) {
        if (!json.has(key) || json.get(key).isJsonNull()) return defaultValue;
        return json.get(key).getAsString();
    }

    public static int getRequiredInt(JsonObject json, String key) {
        if (!json.has(key) || json.get(key).isJsonNull()) throw new IllegalArgumentException("Missing required field: " + key);
        return json.get(key).getAsInt();
    }

    public static Integer getOptionalInt(JsonObject json, String key, Integer defaultValue) {
        if (!json.has(key) || json.get(key).isJsonNull()) return defaultValue;
        return json.get(key).getAsInt();
    }

    public static boolean getOptionalBoolean(JsonObject json, String key, boolean defaultValue) {
        if (!json.has(key) || json.get(key).isJsonNull()) return defaultValue;
        return json.get(key).getAsBoolean();
    }

    public static JsonArray getRequiredArray(JsonObject json, String key) {
        if (!json.has(key) || json.get(key).isJsonNull()) throw new IllegalArgumentException("Missing required field: " + key);
        return json.getAsJsonArray(key);
    }
}