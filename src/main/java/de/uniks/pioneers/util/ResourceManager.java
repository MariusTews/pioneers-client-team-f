package de.uniks.pioneers.util;

import kong.unirest.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceManager {
    private static Path configFilePath = Path.of("./config.json");

    static {
        if (!Files.exists(configFilePath)) {
            try {
                Files.createFile(configFilePath);
                Files.writeString(configFilePath, JsonUtil.createDefaultConfig() );
            } catch (Exception e) {
                System.err.println("config file does not exist");
                e.printStackTrace();
            }
        }
    }
    public static JSONObject loadConfig() {
        try {
            return JsonUtil.parse(Files.readString(configFilePath)).getObject();
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public static void saveConfig(String config) {
        try {
            Files.writeString(configFilePath, config);
        } catch (Exception e) {
            System.err.println("Cant save name");
            e.printStackTrace();
        }
    }

}
