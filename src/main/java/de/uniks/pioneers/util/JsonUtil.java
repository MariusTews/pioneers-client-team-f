package de.uniks.pioneers.util;

import kong.unirest.JsonNode;
import kong.unirest.json.JSONObject;

import static de.uniks.pioneers.Constants.*;

public class JsonUtil {

    public static JsonNode parse(String json) {
        return new JsonNode(json);
    }

    public static String createRememberMeConfig(String nickname, String token) {
        return new JSONObject()
                .put(JSON_REMEMBER_ME, true)
                .put(JSON_NAME, nickname)
                .put(JSON_TOKEN, token)
                .toString();
    }

    public static String updateConfigWithGameId(String gameId) {
        JSONObject loadConfig = ResourceManager.loadConfig();
        loadConfig.put(JSON_GAME_ID, gameId);
        return loadConfig.toString();
    }

    public static String removeGameIdFromConfig() {
        JSONObject loadConfig = ResourceManager.loadConfig();
        loadConfig.remove(JSON_GAME_ID);
        return loadConfig.toString();
    }

    public static String createDefaultConfig() {
        return new JSONObject()
                .put(JSON_REMEMBER_ME, false)
                .put(JSON_NAME, "")
                .put(JSON_TOKEN, "")
                .toString();
    }
}
