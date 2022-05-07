package de.uniks.pioneers;

public class Constants {

    public static final String VERSION_PREFIX = "/v1";
    public static final String API_PREFIX = "/api";

    public static final String BASE_URL = "https://pioneers.uniks.de" + API_PREFIX + VERSION_PREFIX + "/";

    //Network
    public static final String WS_URL = "wss://pioneers.uniks.de";

    public static final String WS_PREFIX = "/ws";
    public static final String WS_AUTHTOKEN_PREFIX = "/events?authToken=";

    public static final String WS_AUTHTOKEN_URL = WS_URL + WS_PREFIX + VERSION_PREFIX + WS_AUTHTOKEN_PREFIX;
}
