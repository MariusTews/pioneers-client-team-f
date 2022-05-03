package de.uniks.pioneers;

public class Constants {

    //Network
    public static final String BASE_URL = "https://pioneers.uniks.de";

    public static final String WS_PREFIX = "/ws";
    public static final String VERSION_PREFIX = "/v1";
    public static final String WS_AUTHTOKEN_PREFIX = "/events?authToken=";

    public static final String WS_AUTHTOKEN_URL = BASE_URL + WS_PREFIX + VERSION_PREFIX + WS_AUTHTOKEN_PREFIX;
}
