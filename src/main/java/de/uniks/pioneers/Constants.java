package de.uniks.pioneers;

import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.util.Comparator;

public class Constants {
    public static final String VERSION_PREFIX = "/v2";
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

    //Name for Global
    public static final String GLOBAL = "global";

    //Welcome Message
    public static final String WELCOME = "Nice to see you again, ";

    // Game
    public static final int MAX_MEMBERS = 6;
    public static final String SETTLEMENT = "settlement";
    public static final String CITY = "city";
    public static final int AMOUNT_SETTLEMENTS_CITIES = 13;

    //Array of Colors as Strings for the Label text
    public static final String[] COLORSTRINGARRAY = {"RED", "BLUE", "ORANGE", "DARKORCHID", "LIMEGREEN", "SIENNA",
            "DEEPSKYBLUE", "SALMON"};

    //Array of Colors
    public static final Color[] COLORARRAY = {Color.RED, Color.BLUE, Color.ORANGE, Color.DARKORCHID, Color.LIMEGREEN,
            Color.SIENNA, Color.DEEPSKYBLUE, Color.SALMON};
}
