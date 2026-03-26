package niwer.bakengo;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

/**
 * Json Configuration class for the application. This class is responsible for reading and writing the configuration file, as well as providing default values for the configuration parameters.
 * The configuration file is stored in the base folder of the application and is named "config.json
 * 
 * @author Niwer
 */
public class Configuration {
    private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Gson GSON = new GsonBuilder().create();
    private static final String CONFIG_FILE = App.BASE_FOLDER.getAbsolutePath() + "/config.json";

    @SerializedName("server_port") private int server_port = 7000;

    @SerializedName("default_admin_email") private String defaultAdminEmail = "default@admin.com";
    @SerializedName("default_admin_password") private String defaultAdminPassword = "admin";

    public int getServerPort() { return server_port; }

    public String getDefaultAdminEmail() { return defaultAdminEmail; }

    public String getDefaultAdminPassword() { return defaultAdminPassword; }

    /**
     * Serialize this configuration to a JSON string.
     * @param json the JSON string to deserialize
     * @return a Configuration object
     */
    public static Configuration fromJson(String json) {
        return GSON.fromJson(json, Configuration.class);
    }

    /**
     * Serialize this configuration to a JSON string.
     * @return a JSON string representation of this configuration
     */
    public String toJson() {
        return PRETTY_GSON.toJson(this);
    }

    /**
     * Write this configuration to a file named "config.json".
     */
    public void toFile() {
        try (FileWriter WRITER = new FileWriter(CONFIG_FILE)) {
            WRITER.write(this.toJson());
        } catch (IOException e) {
            System.out.println("Error writing configuration file: " + e.getMessage());
        }
    }

    /**
     * Read the configuration from a file named "config.json" and return a Configuration object.
     * If the file does not exist or cannot be read, return a new Configuration object with default values.
     * @return a Configuration object read from the file, or a new Configuration object if the file cannot be read
     */
    public static Configuration fromFile() {
        try {
            final Path CONFIG_PATH = Paths.get(CONFIG_FILE);
            final String CONTENT = new String(Files.readAllBytes(CONFIG_PATH));
            return fromJson(CONTENT);
        } catch (IOException e) {
            System.out.println("Error reading configuration file: " + e.getMessage());

            final Configuration DEFAULT_CONFIG = new Configuration();
            DEFAULT_CONFIG.toFile(); // Generate a default configuration file if it doesn't exist
            return DEFAULT_CONFIG;
        }
    }
}
