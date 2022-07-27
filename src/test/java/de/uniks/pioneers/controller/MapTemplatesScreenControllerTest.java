package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Template.MapTemplate;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.MapsService;
import de.uniks.pioneers.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
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
import java.util.HashMap;
import java.util.List;

import static de.uniks.pioneers.Constants.*;
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

    @Mock
    EventListener eventListener;

    @InjectMocks
    MapTemplatesScreenController mapTemplatesScreenController;

    private Subject<Event<MapTemplate>> mapTemplateSubject;
    User user1;
    User user2;
    MapTemplate user1Map;
    MapTemplate user2Map;

    public void start(Stage stage) {
        mapTemplateSubject = PublishSubject.create();
        user1 = new User("", "", "1", "bert", "online", null, null);
        user2 = new User("", "", "2", "kuno", "online", null, null);
        user1Map = new MapTemplate("", "", user1._id(), "berts map", null, user1._id(), 0, List.of(), List.of());
        user2Map = new MapTemplate("", "", user2._id(), "kunos map", null, user2._id(), 0, List.of(), List.of());
        when(userService.findAllUsers()).thenReturn(Observable.just(List.of(user2, user1)));
        when(idStorage.getID()).thenReturn(user1._id());
        when(mapsService.findAllMaps()).thenReturn(Observable.just(new ArrayList<>(List.of(user2Map, user1Map))));
        when(eventListener.listen("maps.*.*", MapTemplate.class)).thenReturn(mapTemplateSubject);

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

        ObservableList<Parent> mapTemplates = mapTemplatesScreenController.getMapTemplates();
        Assertions.assertThat(mapTemplates.get(0).getId()).isEqualTo(user1Map._id());
        //index 1 is the empty line between own and other maps
        Assertions.assertThat(mapTemplates.get(2).getId()).isEqualTo(user2Map._id());
    }

    @Test
    void updateMapTemplateName() {
        String newName = "berts map v2";
        mapTemplateSubject.onNext(new Event<>(UPDATED, new MapTemplate(user1Map.createdAt(), user1Map.updatedAt(),
                user1Map._id(), newName, user1Map.icon(), user1Map.createdBy(), user1Map.votes(), user1Map.tiles(), user1Map.harbors())));

        WaitForAsyncUtils.waitForFxEvents();

        HashMap<String, MapTemplateSubcontroller> mapTemplateSubCons = mapTemplatesScreenController.getMapTemplateSubCons();
        MapTemplateSubcontroller controller = mapTemplateSubCons.get(user1Map._id());

        Assertions.assertThat(controller.nameLabel.getText()).isEqualTo(newName);
    }

    @Test
    void addOwnMapTemplate() {
        MapTemplate user1NewMap = new MapTemplate("", "", "3", "berts new map", null, user1._id(), 0, List.of(), List.of());
        mapTemplateSubject.onNext(new Event<>(CREATED, user1NewMap));

        WaitForAsyncUtils.waitForFxEvents();

        HashMap<String, MapTemplateSubcontroller> mapTemplateSubCons = mapTemplatesScreenController.getMapTemplateSubCons();
        MapTemplateSubcontroller controller = mapTemplateSubCons.getOrDefault(user1NewMap._id(), null);
        Assertions.assertThat(controller).isNotNull();

        ObservableList<Parent> mapTemplates = mapTemplatesScreenController.getMapTemplates();
        //assert that the new map is the second one (index 1) in the list (because it is a map from the current user)
        Assertions.assertThat(mapTemplates.get(1).getId()).isEqualTo(user1NewMap._id());
    }

    @Test
    void addMapTemplate() {
        MapTemplate user2NewMap = new MapTemplate("", "", "4", "kunos new map", null, user2._id(), 0, List.of(), List.of());
        mapTemplateSubject.onNext(new Event<>(CREATED, user2NewMap));

        WaitForAsyncUtils.waitForFxEvents();

        HashMap<String, MapTemplateSubcontroller> mapTemplateSubCons = mapTemplatesScreenController.getMapTemplateSubCons();
        MapTemplateSubcontroller controller = mapTemplateSubCons.getOrDefault(user2NewMap._id(), null);
        Assertions.assertThat(controller).isNotNull();

        ObservableList<Parent> mapTemplates = mapTemplatesScreenController.getMapTemplates();
        //assert that the new map is the third one in the list, because it is a map from another user (index 3 because the empty line is in between)
        Assertions.assertThat(mapTemplates.get(3).getId()).isEqualTo(user2NewMap._id());
    }

    @Test
    void deleteOwnMapTemplate() {
        mapTemplateSubject.onNext(new Event<>(DELETED, user1Map));

        WaitForAsyncUtils.waitForFxEvents();

        HashMap<String, MapTemplateSubcontroller> mapTemplateSubCons = mapTemplatesScreenController.getMapTemplateSubCons();
        MapTemplateSubcontroller controller = mapTemplateSubCons.getOrDefault(user1Map._id(), null);
        Assertions.assertThat(controller).isNull();

        ObservableList<Parent> mapTemplates = mapTemplatesScreenController.getMapTemplates();
        //assert that there are only two list items left (one for the empty line, one for the other map)
        Assertions.assertThat(mapTemplates.size()).isEqualTo(2);
    }

    @Test
    void deleteMapTemplate() {
        mapTemplateSubject.onNext(new Event<>(DELETED, user2Map));

        WaitForAsyncUtils.waitForFxEvents();

        HashMap<String, MapTemplateSubcontroller> mapTemplateSubCons = mapTemplatesScreenController.getMapTemplateSubCons();
        MapTemplateSubcontroller controller = mapTemplateSubCons.getOrDefault(user2Map._id(), null);
        Assertions.assertThat(controller).isNull();

        ObservableList<Parent> mapTemplates = mapTemplatesScreenController.getMapTemplates();
        //assert that there are only two list items left (one for the empty line, one for the own map)
        Assertions.assertThat(mapTemplates.size()).isEqualTo(2);
    }

}
