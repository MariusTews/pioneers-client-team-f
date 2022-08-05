package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.model.Vote;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.MapsService;
import de.uniks.pioneers.service.UserService;
import de.uniks.pioneers.template.MapTemplate;
import de.uniks.pioneers.websocket.EventListener;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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
    User user3;
    User user4;
    User user5;
    User user6;
    MapTemplate user1Map;
    MapTemplate user2Map;

    public void start(Stage stage) {
        mapTemplateSubject = PublishSubject.create();
        user1 = new User("", "", "1", "bert", "online", null, null);
        user2 = new User("", "", "2", "kuno", "online", null, null);
        user3 = new User("", "", "3", "mark", "online", null, null);
        user4 = new User("", "", "4", "john", "online", null, null);
        user5 = new User("", "", "5", "paul", "online", null, null);
        user6 = new User("", "", "6", "chris", "online", null, null);
        user1Map = new MapTemplate("", "", user1._id(), "bert map", null, user1._id(), 0, List.of(), List.of());
        user2Map = new MapTemplate("", "", user2._id(), "kuno map", null, user2._id(), 0, List.of(), List.of());
        when(userService.findAllUsers()).thenReturn(Observable.just(List.of(user1, user2, user3, user4, user5, user6)));
        when(userService.findVotes(user1._id())).thenReturn(Observable.just(List.of()));
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
        String newName = "bert map v2";
        mapTemplateSubject.onNext(new Event<>(UPDATED, new MapTemplate(user1Map.createdAt(), user1Map.updatedAt(),
                user1Map._id(), newName, user1Map.icon(), user1Map.createdBy(), user1Map.votes(), user1Map.tiles(), user1Map.harbors())));

        WaitForAsyncUtils.waitForFxEvents();

        HashMap<String, MapTemplateSubController> mapTemplateSubCons = mapTemplatesScreenController.getMapTemplateSubCons();
        MapTemplateSubController controller = mapTemplateSubCons.get(user1Map._id());

        Assertions.assertThat(controller.nameLabel.getText()).isEqualTo(newName);
    }

    @Test
    void addOwnMapTemplate() {
        MapTemplate user1NewMap = new MapTemplate("", "", "3", "bert new map", null, user1._id(), 0, List.of(), List.of());
        mapTemplateSubject.onNext(new Event<>(CREATED, user1NewMap));

        WaitForAsyncUtils.waitForFxEvents();

        HashMap<String, MapTemplateSubController> mapTemplateSubCons = mapTemplatesScreenController.getMapTemplateSubCons();
        MapTemplateSubController controller = mapTemplateSubCons.getOrDefault(user1NewMap._id(), null);
        Assertions.assertThat(controller).isNotNull();

        ObservableList<Parent> mapTemplates = mapTemplatesScreenController.getMapTemplates();
        //assert that the new map is the second one (index 1) in the list (because it is a map from the current user)
        Assertions.assertThat(mapTemplates.get(1).getId()).isEqualTo(user1NewMap._id());
    }

    @Test
    void addMapTemplate() {
        MapTemplate user2NewMap = new MapTemplate("", "", "4", "kuno new map", null, user2._id(), 0, List.of(), List.of());
        mapTemplateSubject.onNext(new Event<>(CREATED, user2NewMap));

        WaitForAsyncUtils.waitForFxEvents();

        HashMap<String, MapTemplateSubController> mapTemplateSubCons = mapTemplatesScreenController.getMapTemplateSubCons();
        MapTemplateSubController controller = mapTemplateSubCons.getOrDefault(user2NewMap._id(), null);
        Assertions.assertThat(controller).isNotNull();

        ObservableList<Parent> mapTemplates = mapTemplatesScreenController.getMapTemplates();
        //assert that the new map is the third one in the list, because it is a map from another user (index 3 because the empty line is in between)
        Assertions.assertThat(mapTemplates.get(3).getId()).isEqualTo(user2NewMap._id());
    }

    @Test
    void deleteOwnMapTemplate() {
        when(mapsService.deleteMapTemplate(anyString())).thenReturn(Observable.just(user1Map));
        WaitForAsyncUtils.waitForFxEvents();
        HashMap<String, MapTemplateSubController> mapTemplateSubCons = mapTemplatesScreenController.getMapTemplateSubCons();
        MapTemplateSubController controller = mapTemplateSubCons.getOrDefault(user1Map._id(), null);
        Assertions.assertThat(controller).isNotNull();

        clickOn(controller.rightActionImageView);
        clickOn("Yes");
        mapTemplateSubject.onNext(new Event<>(DELETED, user1Map));
        WaitForAsyncUtils.waitForFxEvents();

        controller = mapTemplateSubCons.getOrDefault(user1Map._id(), null);
        Assertions.assertThat(controller).isNull();

        ObservableList<Parent> mapTemplates = mapTemplatesScreenController.getMapTemplates();
        //assert that there are only two list items left (one for the empty line, one for the other map)
        Assertions.assertThat(mapTemplates.size()).isEqualTo(2);
    }

    @Test
    void deleteMapTemplate() {
        mapTemplateSubject.onNext(new Event<>(DELETED, user2Map));

        WaitForAsyncUtils.waitForFxEvents();

        HashMap<String, MapTemplateSubController> mapTemplateSubCons = mapTemplatesScreenController.getMapTemplateSubCons();
        MapTemplateSubController controller = mapTemplateSubCons.getOrDefault(user2Map._id(), null);
        Assertions.assertThat(controller).isNull();

        ObservableList<Parent> mapTemplates = mapTemplatesScreenController.getMapTemplates();
        //assert that there are only two list items left (one for the empty line, one for the own map)
        Assertions.assertThat(mapTemplates.size()).isEqualTo(2);
    }

    @Test
    void selectMapTemplateItem() {
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#" + user1Map._id()).clickOn("#" + user1Map._id());
        WaitForAsyncUtils.waitForFxEvents();

        HashMap<String, MapTemplateSubController> mapTemplateSubCons = mapTemplatesScreenController.getMapTemplateSubCons();
        MapTemplateSubController controller = mapTemplateSubCons.get(user1Map._id());
        Assertions.assertThat(controller.selectedImageView.getImage()).isNotNull();
        Assertions.assertThat(mapTemplatesScreenController.selectedLabel).hasText("Selected: " + user1Map.name());

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#" + user2Map._id()).clickOn("#" + user2Map._id());
        WaitForAsyncUtils.waitForFxEvents();

        Assertions.assertThat(controller.selectedImageView.getImage()).isNull();
        controller = mapTemplateSubCons.get(user2Map._id());
        Assertions.assertThat(controller.selectedImageView.getImage()).isNotNull();
        Assertions.assertThat(mapTemplatesScreenController.selectedLabel).hasText("Selected: " + user2Map.name());
    }

    @Test
    void sortByName() {
        MapTemplate map1 = new MapTemplate("", "", "01", "b", null, user1._id(), 10, List.of(), List.of());
        MapTemplate map2 = new MapTemplate("", "", "02", "a", null, user1._id(), -17, List.of(), List.of());
        MapTemplate map3 = new MapTemplate("", "", "03", "bb", null, user2._id(), 3, List.of(), List.of());
        MapTemplate map4 = new MapTemplate("", "", "04", "aa", null, user2._id(), 28, List.of(), List.of());
        mapTemplateSubject.onNext(new Event<>(CREATED, map1));
        mapTemplateSubject.onNext(new Event<>(CREATED, map2));
        mapTemplateSubject.onNext(new Event<>(CREATED, map3));
        mapTemplateSubject.onNext(new Event<>(CREATED, map4));

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#nameArrow");
        WaitForAsyncUtils.waitForFxEvents();

        ObservableList<Parent> mapTemplates = mapTemplatesScreenController.getMapTemplates();
        Assertions.assertThat(mapTemplates.get(0).getId()).isEqualTo(map2._id());
        Assertions.assertThat(mapTemplates.get(1).getId()).isEqualTo(map1._id());
        Assertions.assertThat(mapTemplates.get(2).getId()).isEqualTo(user1Map._id());
        Assertions.assertThat(mapTemplates.get(4).getId()).isEqualTo(map4._id());
        Assertions.assertThat(mapTemplates.get(5).getId()).isEqualTo(map3._id());
        Assertions.assertThat(mapTemplates.get(6).getId()).isEqualTo(user2Map._id());
    }

    @Test
    void sortByOwner() {
        MapTemplate map1 = new MapTemplate("", "", "01", "b", null, user3._id(), 10, List.of(), List.of());
        MapTemplate map2 = new MapTemplate("", "", "02", "a", null, user4._id(), -17, List.of(), List.of());
        MapTemplate map3 = new MapTemplate("", "", "03", "bb", null, user5._id(), 3, List.of(), List.of());
        MapTemplate map4 = new MapTemplate("", "", "04", "aa", null, user6._id(), 28, List.of(), List.of());
        mapTemplateSubject.onNext(new Event<>(CREATED, map1));
        mapTemplateSubject.onNext(new Event<>(CREATED, map2));
        mapTemplateSubject.onNext(new Event<>(CREATED, map3));
        mapTemplateSubject.onNext(new Event<>(CREATED, map4));

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#createdByArrow");
        WaitForAsyncUtils.waitForFxEvents();

        ObservableList<Parent> mapTemplates = mapTemplatesScreenController.getMapTemplates();
        Assertions.assertThat(mapTemplates.get(0).getId()).isEqualTo(user1Map._id());
        Assertions.assertThat(mapTemplates.get(2).getId()).isEqualTo(map4._id());
        Assertions.assertThat(mapTemplates.get(3).getId()).isEqualTo(map2._id());
        Assertions.assertThat(mapTemplates.get(4).getId()).isEqualTo(user2Map._id());
        Assertions.assertThat(mapTemplates.get(5).getId()).isEqualTo(map1._id());
        Assertions.assertThat(mapTemplates.get(6).getId()).isEqualTo(map3._id());
    }

    @Test
    void sortByVotes() {
        MapTemplate map1 = new MapTemplate("", "", "01", "b", null, user3._id(), 10, List.of(), List.of());
        MapTemplate map2 = new MapTemplate("", "", "02", "a", null, user4._id(), -17, List.of(), List.of());
        MapTemplate map3 = new MapTemplate("", "", "03", "bb", null, user5._id(), 3, List.of(), List.of());
        MapTemplate map4 = new MapTemplate("", "", "04", "aa", null, user6._id(), 28, List.of(), List.of());
        mapTemplateSubject.onNext(new Event<>(CREATED, map1));
        mapTemplateSubject.onNext(new Event<>(CREATED, map2));
        mapTemplateSubject.onNext(new Event<>(CREATED, map3));
        mapTemplateSubject.onNext(new Event<>(CREATED, map4));

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#votesArrow");
        WaitForAsyncUtils.waitForFxEvents();

        ObservableList<Parent> mapTemplates = mapTemplatesScreenController.getMapTemplates();
        Assertions.assertThat(mapTemplates.get(0).getId()).isEqualTo(user1Map._id());
        Assertions.assertThat(mapTemplates.get(2).getId()).isEqualTo(map2._id());
        Assertions.assertThat(mapTemplates.get(3).getId()).isEqualTo(user2Map._id());
        Assertions.assertThat(mapTemplates.get(4).getId()).isEqualTo(map3._id());
        Assertions.assertThat(mapTemplates.get(5).getId()).isEqualTo(map1._id());
        Assertions.assertThat(mapTemplates.get(6).getId()).isEqualTo(map4._id());
    }

    @Test
    void addMapTemplateThenUpdateSort() {
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#nameArrow");
        WaitForAsyncUtils.waitForFxEvents();

        // this map will be added at the end of the list by default, but because the list was sorted before, it should be the 2nd item
        MapTemplate user2NewMap = new MapTemplate("", "", "4", "a", null, user2._id(), 0, List.of(), List.of());
        mapTemplateSubject.onNext(new Event<>(CREATED, user2NewMap));
        WaitForAsyncUtils.waitForFxEvents();

        ObservableList<Parent> mapTemplates = mapTemplatesScreenController.getMapTemplates();
        Assertions.assertThat(mapTemplates.get(0).getId()).isEqualTo(user1Map._id());
        Assertions.assertThat(mapTemplates.get(2).getId()).isEqualTo(user2NewMap._id());
        Assertions.assertThat(mapTemplates.get(3).getId()).isEqualTo(user2Map._id());
    }

    @Test
    void updateMapTemplateThenUpdateSort() {
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#votesArrow");
        WaitForAsyncUtils.waitForFxEvents();

        MapTemplate user2NewMap = new MapTemplate("", "", "4", "a", null, user2._id(), 0, List.of(), List.of());
        mapTemplateSubject.onNext(new Event<>(CREATED, user2NewMap));
        WaitForAsyncUtils.waitForFxEvents();
        MapTemplate user2NewMapUpdate = new MapTemplate("", "", "4", "a", null, user2._id(), -1, List.of(), List.of());
        mapTemplateSubject.onNext(new Event<>(UPDATED, user2NewMapUpdate));
        WaitForAsyncUtils.waitForFxEvents();

        ObservableList<Parent> mapTemplates = mapTemplatesScreenController.getMapTemplates();
        Assertions.assertThat(mapTemplates.get(0).getId()).isEqualTo(user1Map._id());
        Assertions.assertThat(mapTemplates.get(2).getId()).isEqualTo(user2NewMap._id());
        Assertions.assertThat(mapTemplates.get(3).getId()).isEqualTo(user2Map._id());
    }

    @Test
    void upvoteThenDeleteVote() {
        Vote vote = new Vote("", "", user2Map._id(), user1._id(), 1);
        when(mapsService.voteMap(anyString(), anyInt())).thenReturn(Observable.just(vote));
        when(mapsService.deleteVote(anyString(), anyString())).thenReturn(Observable.just(vote));

        WaitForAsyncUtils.waitForFxEvents();
        HashMap<String, MapTemplateSubController> mapTemplateSubCons = mapTemplatesScreenController.getMapTemplateSubCons();
        MapTemplateSubController controller = mapTemplateSubCons.get(user2Map._id());

        // up-vote
        clickOn(controller.leftActionImageView);

        MapTemplate user2MapUpdate = new MapTemplate("", "", user2Map._id(), user2Map.name(), null, user2._id(), 1, List.of(), List.of());
        mapTemplateSubject.onNext(new Event<>(UPDATED, user2MapUpdate));
        WaitForAsyncUtils.waitForFxEvents();

        Assertions.assertThat(controller.votesLabel).hasText("| +1");

        // delete vote
        clickOn(controller.leftActionImageView);

        user2MapUpdate = new MapTemplate("", "", user2Map._id(), user2Map.name(), null, user2._id(), 0, List.of(), List.of());
        mapTemplateSubject.onNext(new Event<>(UPDATED, user2MapUpdate));
        WaitForAsyncUtils.waitForFxEvents();

        Assertions.assertThat(controller.votesLabel).hasText("|  0");
        Assertions.assertThat(controller.leftActionImageView.getEffect()).isNull();
        Assertions.assertThat(controller.rightActionImageView.getEffect()).isNull();
    }

    @Test
    void downVote() {
        Vote vote = new Vote("", "", user2Map._id(), user1._id(), -1);
        when(mapsService.voteMap(anyString(), anyInt())).thenReturn(Observable.just(vote));

        WaitForAsyncUtils.waitForFxEvents();
        HashMap<String, MapTemplateSubController> mapTemplateSubCons = mapTemplatesScreenController.getMapTemplateSubCons();
        MapTemplateSubController controller = mapTemplateSubCons.get(user2Map._id());

        // down-vote
        clickOn(controller.rightActionImageView);

        MapTemplate user2MapUpdate = new MapTemplate("", "", user2Map._id(), user2Map.name(), null, user2._id(), -1, List.of(), List.of());
        mapTemplateSubject.onNext(new Event<>(UPDATED, user2MapUpdate));
        WaitForAsyncUtils.waitForFxEvents();

        Assertions.assertThat(controller.votesLabel).hasText("| -1");
        Assertions.assertThat(controller.leftActionImageView.getEffect()).isNotNull();
        Assertions.assertThat(controller.rightActionImageView.getEffect()).isNotNull();
    }

}
