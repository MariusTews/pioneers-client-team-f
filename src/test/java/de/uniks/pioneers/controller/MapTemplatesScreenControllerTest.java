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

import static org.junit.jupiter.api.Assertions.*;
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

    User bert;
    User kuno;
    MapTemplate bertsMap;
    MapTemplate kunosMap;

    public void start(Stage stage) {
        bert = new User("", "", "1", "bert", "online", null, null);
        kuno = new User("", "", "2", "kuno", "online", null, null);
        bertsMap = new MapTemplate("", "", bert._id(), "berts map", null, bert._id(), 0, List.of(), List.of());
        kunosMap = new MapTemplate("", "", kuno._id(), "kunos map", null, kuno._id(), 0, List.of(), List.of());
        when(userService.findAllUsers()).thenReturn(Observable.just(List.of(bert, kuno)));
        when(idStorage.getID()).thenReturn(bert._id());
        when(mapsService.findAllMaps()).thenReturn(Observable.just(new ArrayList<>(List.of(bertsMap, kunosMap))));

        // start application
        app = new App(mapTemplatesScreenController);
        app.start(stage);
    }

    @Test
    void loadAllMaps() {
        WaitForAsyncUtils.waitForFxEvents();

        HBox bertsMapTemplateBox = lookup("#" + bert._id()).query();
        HBox kunosMapTemplateBox = lookup("#" + kuno._id()).query();

        Assertions.assertThat(bertsMapTemplateBox.getChildren().size()).isEqualTo(7);
        Assertions.assertThat(kunosMapTemplateBox.getChildren().size()).isEqualTo(7);
    }
}