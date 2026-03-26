package niwer.bakengo.endpoints;

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

    /**
     * Parses a JSON string into a JsonObject. Throws IllegalArgumentException if the body is null, blank, or not valid JSON.
     * 
     * @param body The raw JSON string from the request body.
     * @return A JsonObject representing the parsed JSON data.
     */
    public static JsonObject parseJsonBody(String body) {
        if (body == null || body.isBlank()) throw new IllegalArgumentException("Request body cannot be empty");
        try {
            return JsonParser.parseString(body).getAsJsonObject();
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid JSON payload");
        }
    }

    /**
     * Retrieves a required string value from the JsonObject. Throws IllegalArgumentException if the key is missing, null, or blank.
     * 
     * @param json The JsonObject to extract the value from.
     * @param key The key of the required string field.
     * @return The trimmed string value associated with the key.
     */
    public static String getRequiredString(JsonObject json, String key) {
        if (!json.has(key) || json.get(key).isJsonNull()) throw new IllegalArgumentException("Missing required field: " + key);
        final String VALUE = json.get(key).getAsString();
        if (VALUE == null || VALUE.isBlank()) throw new IllegalArgumentException("Field " + key + " cannot be empty");
        return VALUE.trim();
    }

    /**
     * Retrieves an optional string value from the JsonObject. Returns the defaultValue if the key is missing or null. If the value is present but blank, it returns the defaultValue.
     * 
     * @param json The JsonObject to extract the value from.
     * @param key The key of the optional string field.
     * @param defaultValue The default value to return if the key is missing, null, or blank.
     * @return The trimmed string value associated with the key, or defaultValue if the key is missing, null, or blank.
     */
    public static String getOptionalString(JsonObject json, String key, String defaultValue) {
        if (!json.has(key) || json.get(key).isJsonNull()) return defaultValue;
        return json.get(key).getAsString();
    }

    /**
     * Retrieves a required integer value from the JsonObject. Throws IllegalArgumentException if the key is missing or null.
     * 
     * @param json The JsonObject to extract the value from.
     * @param key The key of the required integer field.
     * @return The integer value associated with the key.
     */
    public static int getRequiredInt(JsonObject json, String key) {
        if (!json.has(key) || json.get(key).isJsonNull()) throw new IllegalArgumentException("Missing required field: " + key);
        return json.get(key).getAsInt();
    }

    /**
     * Retrieves an optional integer value from the JsonObject. Returns the defaultValue if the key is missing or null.
     * 
     * @param json The JsonObject to extract the value from.
     * @param key The key of the optional integer field.
     * @param defaultValue The default value to return if the key is missing or null.
     * @return The integer value associated with the key, or defaultValue if the key is missing or null.
     */
    public static Integer getOptionalInt(JsonObject json, String key, Integer defaultValue) {
        if (!json.has(key) || json.get(key).isJsonNull()) return defaultValue;
        return json.get(key).getAsInt();
    }

    /**
     * Retrieves a required boolean value from the JsonObject. Throws IllegalArgumentException if the key is missing or null.
     * 
     * @param json The JsonObject to extract the value from.
     * @param key The key of the required boolean field.
     * @param defaultValue The default value to return if the key is missing or null.
     * @return The boolean value associated with the key, or defaultValue if the key is missing or null.
     */
    public static boolean getOptionalBoolean(JsonObject json, String key, boolean defaultValue) {
        if (!json.has(key) || json.get(key).isJsonNull()) return defaultValue;
        return json.get(key).getAsBoolean();
    }

    /**
     * Retrieves a required boolean value from the JsonObject. Throws IllegalArgumentException if the key is missing or null.
     * 
     * @param json The JsonObject to extract the value from.
     * @param key The key of the required boolean field.
     * @return The boolean value associated with the key.
     */
    public static boolean getRequiredBoolean(JsonObject json, String key) {
        if (!json.has(key) || json.get(key).isJsonNull()) throw new IllegalArgumentException("Missing required field: " + key);
        return json.get(key).getAsBoolean();
    }

    /**
     * Retrieves a required JsonArray from the JsonObject. Throws IllegalArgumentException if the key is missing or null.
     * 
     * @param json The JsonObject to extract the value from.
     * @param key The key of the required JsonArray field.
     * @return The JsonArray associated with the key.
     */
    public static JsonArray getRequiredArray(JsonObject json, String key) {
        if (!json.has(key) || json.get(key).isJsonNull()) throw new IllegalArgumentException("Missing required field: " + key);
        return json.getAsJsonArray(key);
    }
}