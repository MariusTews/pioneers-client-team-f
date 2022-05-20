package de.uniks.pioneers.util;



import kong.unirest.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class RememberMeTest extends ApplicationTest {

    @Test
    public void JsonUtilTest() {
        String expectedDefault = new JSONObject()
                .put("rememberMe", false)
                .put("name", "")
                .toString();
        String jsonObject = JsonUtil.createDefaultConfig();
        assertEquals(expectedDefault, jsonObject);

        String expectedRememberMe = new JSONObject()
                .put("rememberMe", true)
                .put("name", "Bob")
                .toString();
        String jsonObjectRememberMe = JsonUtil.createRememberMeConfig("Bob");
        assertEquals(expectedRememberMe, jsonObjectRememberMe);
    }

    @Test
    public void ResourceManagerTest() {
        String expectedDefault = new JSONObject()
                .put("rememberMe", false)
                .put("name", "Bob")
                .toString();
        ResourceManager.saveConfig(expectedDefault);

        String jsonObjectLoaded = ResourceManager.loadConfig().toString();

        assertEquals(expectedDefault, jsonObjectLoaded);

        String reset = new JSONObject()
                .put("rememberMe", false)
                .put("name", "")
                .toString();
        ResourceManager.saveConfig(reset);

    }
}
