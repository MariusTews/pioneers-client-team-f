package de.uniks.pioneers.util;

import kong.unirest.JsonNode;
import kong.unirest.json.JSONObject;

import static de.uniks.pioneers.Constants.JSON_NAME;
import static de.uniks.pioneers.Constants.JSON_REMEMBER_ME;

public class JsonUtil {

    public static JsonNode parse(String json) {
        return new JsonNode(json);
    }

    public static String createRememberMeConfig(String nickname) {
        return new JSONObject()
                .put(JSON_REMEMBER_ME, true)
                .put(JSON_NAME, nickname)
                .toString();
    }

    public static String createDefaultConfig() {
        return new JSONObject()
                .put(JSON_REMEMBER_ME, false)
                .put(JSON_NAME, "")
                .toString();
    }
}
