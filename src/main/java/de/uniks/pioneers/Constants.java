package de.uniks.pioneers;

import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.util.Comparator;
import java.util.HashMap;

public class Constants {
    public static final String VERSION_PREFIX = "/v4";
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

    //Achievements Api Service
    public static final String ACHIEVEMENTS_URL = "achievements";
    public static final String USER_ACHIEVEMENTS_URL = "users/{userId}/" + ACHIEVEMENTS_URL;

    public static final String USER_ACHIEVEMENTS_BY_ID_URL = USER_ACHIEVEMENTS_URL + "/{id}";

    //Achievements
    public static final String FIRST_ROAD = "first-road";

    public static final String ROAD_BUILDER = "road-builder";

    public static final String WIN_GAME = "win-game";

    public static final String CREATE_MAP = "create-map";

    public static final String SETTLEMENT_BUILDER = "settlement-builder";

    public static final String CITY_BUILDER = "city-builder";

    public static final String TRADE_BANK = "trade-bank";

    public static final String TRADE_PLAYER = "trade-player";

    public static final String VENUS_GRAIN_PICKER = "venus-grain-picker";

    public static final String MARS_BAR_PICKER = "mars-bar-picker";

    public static final String MOON_ROCK_PICKER = "moon-rock-picker";

    public static final String EARTH_CACTUS_PICKER = "earth-cactus-picker";

    public static final String NEPTUNE_CRYSTAL_PICKER = "neptune-crystal-picker";

    public static final HashMap<String, Integer> ACHIEVEMENT_UNLOCK_VALUES = new HashMap<>();

    static {
        ACHIEVEMENT_UNLOCK_VALUES.put(FIRST_ROAD, 1);
        ACHIEVEMENT_UNLOCK_VALUES.put(ROAD_BUILDER, 100);
        ACHIEVEMENT_UNLOCK_VALUES.put(WIN_GAME, 1);
        ACHIEVEMENT_UNLOCK_VALUES.put(CREATE_MAP, 1);
        ACHIEVEMENT_UNLOCK_VALUES.put(SETTLEMENT_BUILDER, 20);
        ACHIEVEMENT_UNLOCK_VALUES.put(CITY_BUILDER, 20);
        ACHIEVEMENT_UNLOCK_VALUES.put(TRADE_BANK, 30);
        ACHIEVEMENT_UNLOCK_VALUES.put(TRADE_PLAYER, 30);
        ACHIEVEMENT_UNLOCK_VALUES.put(VENUS_GRAIN_PICKER, 50);
        ACHIEVEMENT_UNLOCK_VALUES.put(MARS_BAR_PICKER, 50);
        ACHIEVEMENT_UNLOCK_VALUES.put(MOON_ROCK_PICKER, 50);
        ACHIEVEMENT_UNLOCK_VALUES.put(EARTH_CACTUS_PICKER, 50);
        ACHIEVEMENT_UNLOCK_VALUES.put(NEPTUNE_CRYSTAL_PICKER, 50);
    }

    public static final HashMap<String, String> ACHIEVEMENT_NAMES = new HashMap<>();

    static {
        ACHIEVEMENT_NAMES.put(FIRST_ROAD, "build your first road");
        ACHIEVEMENT_NAMES.put(ROAD_BUILDER, "road builder");
        ACHIEVEMENT_NAMES.put(SETTLEMENT_BUILDER, "settlement builder");
        ACHIEVEMENT_NAMES.put(CITY_BUILDER, "city builder");
        ACHIEVEMENT_NAMES.put(WIN_GAME, "win your first game");
        ACHIEVEMENT_NAMES.put(CREATE_MAP, "create your own map");
        ACHIEVEMENT_NAMES.put(TRADE_BANK, "trade with the bank");
        ACHIEVEMENT_NAMES.put(TRADE_PLAYER, "trade with a player");
        ACHIEVEMENT_NAMES.put(VENUS_GRAIN_PICKER, "venus grain picker");
        ACHIEVEMENT_NAMES.put(MARS_BAR_PICKER, "mars bar picker");
        ACHIEVEMENT_NAMES.put(MOON_ROCK_PICKER, "moon rock picker");
        ACHIEVEMENT_NAMES.put(EARTH_CACTUS_PICKER, "earth cactus picker");
        ACHIEVEMENT_NAMES.put(NEPTUNE_CRYSTAL_PICKER, "neptune crystal picker");
    }

    public static final HashMap<String, String> ACHIEVEMENT_PATHS = new HashMap<>();

    static {
        ACHIEVEMENT_PATHS.put(FIRST_ROAD, "first_road");
        ACHIEVEMENT_PATHS.put(ROAD_BUILDER, "100_roads");
        ACHIEVEMENT_PATHS.put(SETTLEMENT_BUILDER, "settlement_builder");
        ACHIEVEMENT_PATHS.put(CITY_BUILDER, "city_builder");
        ACHIEVEMENT_PATHS.put(WIN_GAME, "win_game");
        ACHIEVEMENT_PATHS.put(CREATE_MAP, "create_map");
        ACHIEVEMENT_PATHS.put(TRADE_BANK, "trade_bank");
        ACHIEVEMENT_PATHS.put(TRADE_PLAYER, "trade_player");
        ACHIEVEMENT_PATHS.put(VENUS_GRAIN_PICKER, "venus_grain");
        ACHIEVEMENT_PATHS.put(MARS_BAR_PICKER, "mars_bar");
        ACHIEVEMENT_PATHS.put(MOON_ROCK_PICKER, "moon_rock");
        ACHIEVEMENT_PATHS.put(EARTH_CACTUS_PICKER, "earth_kaktus");
        ACHIEVEMENT_PATHS.put(NEPTUNE_CRYSTAL_PICKER, "neptune_crystal");
    }

    //Sorting
    public static final Comparator<User> userComparator = Comparator.comparing(User::status).reversed().thenComparing(User::name);

    public static final Comparator<Game> gameComparator = Comparator.comparing(Game::started).thenComparing(Game::name);

    //Tabname
    public static final String DirectMessage = "DirectMessage ";

    //JSON
    public static final String JSON_REMEMBER_ME = "rememberMe";
    public static final String JSON_NAME = "name";
    public static final String JSON_TOKEN = "token";
    public static final String JSON_GAME_ID = "gameId";

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

    public static final String BUILD = "build";

    public static final String NEW = "new";
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
    public static final String ROAD = "road";

    //Card names
    public static final String KNIGHT = "knight";
    public static final String ROAD_BUILDING = "road-building";

    public static final String YEAR_OF_PLENTY = "year-of-plenty";

    public static final String MONOPOLY = "monopoly";

    //Array of Colors as Strings for the Label text
    public static final String[] COLORSTRINGARRAY = {"DARKRED", "BLUE", "ORANGE", "DARKVIOLET", "TEAL", "YELLOW",
            "ROSYBROWN", "OLIVE", "MAGENTA", "AQUA", "BEIGE"};

    //Array of Colors
    public static final Color[] COLORARRAY = {Color.DARKRED, Color.BLUE, Color.ORANGE, Color.DARKVIOLET, Color.TEAL,
            Color.YELLOW, Color.ROSYBROWN, Color.OLIVE, Color.MAGENTA, Color.AQUA, Color.BEIGE};

    // use a second array without Color.BEIGE because there is no image for this color, and it would lead to
    // a NullPointerException in the CircleSubController setSettlement and setCity methods if this color would be inside
    public static final Color[] COLORARRAY2 = {Color.DARKRED, Color.BLUE, Color.ORANGE, Color.DARKVIOLET, Color.TEAL,
            Color.YELLOW, Color.ROSYBROWN, Color.OLIVE, Color.MAGENTA, Color.AQUA,};

    //Array of resources
    public static final String[] RESOURCES = {"lumber", "brick", "ore", "wool", "grain"};
}
