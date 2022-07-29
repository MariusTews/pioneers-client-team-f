package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.Achievement;
import de.uniks.pioneers.service.AchievementsService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

import static de.uniks.pioneers.Constants.*;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AchievementsScreenControllerTest extends ApplicationTest {

    @Mock
    App app;

    @Mock
    Provider<LobbyController> lobbyController;

    @Mock
    AchievementsService achievementsService;

    @InjectMocks
    AchievementsScreenController achievementsScreenController;

    @Override
    public void start(Stage stage) {
        Achievement firstRoad = new Achievement(null, null, "me", FIRST_ROAD, java.time.LocalDateTime.now().toString(), 2);
        Achievement roadBuilder = new Achievement(null, null, "me", ROAD_BUILDER, null, 13);
        Achievement settlementBuilder = new Achievement(null, null, "me", SETTLEMENT_BUILDER, null, 4);
        Achievement cityBuilder = new Achievement(null, null, "me", CITY_BUILDER, null, 1);

        List<Achievement> achievementList = new ArrayList<>();
        achievementList.add(firstRoad);
        achievementList.add(roadBuilder);
        achievementList.add(settlementBuilder);
        achievementList.add(cityBuilder);

        when(achievementsService.listUserAchievements()).thenReturn(Observable.just(achievementList));
        App app = new App(achievementsScreenController);
        app.start(stage);

    }

    @Test
    void testScreen() {
        verifyThat("#achievementsList", NodeMatchers.isVisible());
        ListView<HBox> list = lookup("#achievementsList").query();
        assertThat(list).hasExactlyNumItems(13);

    }

}