package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Template.MapTemplate;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.MapsService;
import de.uniks.pioneers.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapTemplatesScreenControllerTest extends ApplicationTest {
    @Mock
    App app;

    @Mock
    MapsService mapsService;

    @Mock
    UserService userService;

    @Mock
    IDStorage idStorage;

    @InjectMocks
    MapTemplatesScreenController mapTemplatesScreenController;

    User user1;
    User user2;
    MapTemplate user1Map;
    MapTemplate user2Map;

    public void start(Stage stage) {
        user1 = new User("", "", "1", "bert", "online", null, null);
        user2 = new User("", "", "2", "kuno", "online", null, null);
        user1Map = new MapTemplate("", "", user1._id(), "berts map", null, user1._id(), 0, List.of(), List.of());
        user2Map = new MapTemplate("", "", user2._id(), "kunos map", null, user2._id(), 0, List.of(), List.of());
        when(userService.findAllUsers()).thenReturn(Observable.just(List.of(user2, user1)));
        when(idStorage.getID()).thenReturn(user1._id());
        when(mapsService.findAllMaps()).thenReturn(Observable.just(new ArrayList<>(List.of(user2Map, user1Map))));

        // start application
        app = new App(mapTemplatesScreenController);
        app.start(stage);
    }

    @Test
    void loadAllMaps() {
        WaitForAsyncUtils.waitForFxEvents();

        HBox user1MapTemplateBox = lookup("#" + user1._id()).query();
        HBox user2MapTemplateBox = lookup("#" + user2._id()).query();

        Assertions.assertThat(user1MapTemplateBox.getChildren().size()).isEqualTo(7);
        Assertions.assertThat(user2MapTemplateBox.getChildren().size()).isEqualTo(7);
    }
}