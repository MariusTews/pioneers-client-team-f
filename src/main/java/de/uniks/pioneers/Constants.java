package de.uniks.pioneers;

import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.util.Comparator;

public class Constants {
    public static final String VERSION_PREFIX = "/v3";
    public static final String API_PREFIX = "/api";

    public static final String BASE_URL = "https://pioneers.uniks.de" + API_PREFIX + VERSION_PREFIX + "/";

    //Network
    public static final String WS_URL = "wss://pioneers.uniks.de";

    public static final String WS_PREFIX = "/ws";
    public static final String WS_AUTHTOKEN_PREFIX = "/events?authToken=";

    public static final String WS_AUTHTOKEN_URL = WS_URL + WS_PREFIX + VERSION_PREFIX + WS_AUTHTOKEN_PREFIX;

    //Runtime
    public static final Scheduler FX_SCHEDULER = Schedulers.from(Platform::runLater);

    //Websocket calls
    public static final String CREATED = ".created";

    public static final String DELETED = ".deleted";

    public static final String UPDATED = ".updated";

    //Messages
    public static final String GROUPS = "groups";
    public static final String GAMES = "games";

    //Sorting
    public static final Comparator<User> userComparator = Comparator.comparing(User::status).reversed().thenComparing(User::name);

    public static final Comparator<Game> gameComparator = Comparator.comparing(Game::started).thenComparing(Game::name);

    //Tabname
    public static final String DirectMessage = "DirectMessage ";

    //JSON
    public static final String JSON_REMEMBER_ME = "rememberMe";
    public static final String JSON_NAME = "name";

    //Lobby ID for listening and sending Messages
    public static final String LOBBY_ID = "627cf3c93496bc00158f3859";

    //Bank ID for trading
    public static final String BANK_ID = "684072366f72202b72406465";

    //Name for Global
    public static final String GLOBAL = "global";

    //Welcome Message
    public static final String WELCOME = "Nice to see you again, ";

    // Game
    public static final int MAX_MEMBERS = 6;
    public static final String RENAME_SETTLEMENT = "UFO";
    public static final String RENAME_CITY = "Station";
    public static final String RENAME_ROAD = "Tube";
    public static final String RENAME_FOUNDING_SET1 = "Place-UFO-1";
    public static final String RENAME_FOUNDING_SET2 = "Place-UFO-2";
    public static final String RENAME_FOUNDING_ROAD1 = "Place-Tube-1";
    public static final String RENAME_FOUNDING_ROAD2 = "Place-Tube-2";
    public static final String VENUS_GRAIN = "grain";
    public static final String MARS_BAR = "brick";
    public static final String MOON_ROCK = "ore";
    public static final String EARTH_CACTUS = "lumber";
    public static final String NEPTUNE_CRYSTAL = "wool";
    public static final String DROP_ACTION = "drop";
    public static final String ROB_ACTION = "rob";
    public static final String BUILD_ACTION = "build";
    public static final String ROAD = "road";

    //Array of Colors as Strings for the Label text
    public static final String[] COLORSTRINGARRAY = {"DARKRED", "BLUE", "ORANGE", "DARKVIOLET", "TEAL", "YELLOW",
            "ROSYBROWN", "OLIVE", "MAGENTA", "AQUA"};

    //Array of Colors
    public static final Color[] COLORARRAY = {Color.DARKRED, Color.BLUE, Color.ORANGE, Color.DARKVIOLET, Color.TEAL,
            Color.YELLOW, Color.ROSYBROWN, Color.OLIVE, Color.MAGENTA, Color.AQUA};

    //Array of resources
    public static final String[] RESOURCES = {"lumber","brick","ore","wool","grain"};
}
