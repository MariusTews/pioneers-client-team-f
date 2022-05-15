package de.uniks.pioneers;

import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;

import java.util.Comparator;

public class Constants {
    public static final String VERSION_PREFIX = "/v1";
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

    public static final Comparator<Game> gameComparator = Comparator.comparing(Game::name);
}
