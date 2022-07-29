package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.GameStorage;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.PioneersService;
import de.uniks.pioneers.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSubViewTest extends ApplicationTest {
    @Spy
    GameStorage gameStorage;
    @Mock
    PioneersService pioneersService;
    @Spy
    IDStorage idStorage;

    @Mock
    GameFieldSubController gameFieldSubController;
    @Mock
    UserService userService;

    final HashMap<String, Integer> hm = new HashMap<>() {{
        put("wool", 2);
        put("grain", 3);
        put("ore", 2);
        put("lumber", 2);
        put("brick", 6);
    }};

    @InjectMocks
    UserSubView userSubView = new UserSubView(idStorage, gameStorage, userService, new Player("id", "2",
            "#000000", true, 2, hm, new HashMap<>(), 2, 2, null, null), gameFieldSubController, 10, pioneersService);


    public void start(Stage stage) {
        when(idStorage.getID()).thenReturn("2");
        when(userService.findAllUsers()).thenReturn(Observable.just(List.of(
                new User("1234", "12345", "2", "tests", "online", null, new ArrayList<>()))));
        when(pioneersService.findAllPlayers(any())).thenReturn(Observable.empty());

        userSubView = new UserSubView(idStorage, gameStorage, userService, new Player("id", "2",
                "#000000", true, 2, hm, new HashMap<>(), 2, 10, null, null), gameFieldSubController, 10, pioneersService);

        final App app = new App(userSubView);
        app.start(stage);
        testParameters();

    }

    @Test
    public void testParameters() {

        // will be added in 4h release, old version did not test anything
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        gameFieldSubController = null;
        hm.clear();
    }
}
