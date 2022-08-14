package de.uniks.pioneers.controller;

import de.uniks.pioneers.model.Move;
import de.uniks.pioneers.service.GameStorage;
import de.uniks.pioneers.service.PioneersService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.HashMap;

import static de.uniks.pioneers.Constants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class YearOfPlentyControllerTest extends ApplicationTest {

    @Mock
    PioneersService pioneersService;

    @Spy
    GameStorage gameStorage;

    final HashMap<String, Integer> resources = new HashMap<>() {{
        put(VENUS_GRAIN, 0);
        put(MOON_ROCK, 0);
        put(MARS_BAR, 0);
        put(NEPTUNE_CRYSTAL, 0);
        put(EARTH_CACTUS, 0);
    }};


    @ExtendWith(MockitoExtension.class)
    public void start(Stage stage) {
        YearOfPlentyController yearOfPlentyController = new YearOfPlentyController(gameStorage.getId(), pioneersService, null);
        yearOfPlentyController.render();
    }

    @Test
    void adjustResourcesTest() {
        // Resource 1 - earth cactus UI elements
        Label amountEarthCactus = lookup("#" + EARTH_CACTUS).query();
        Button incResource1Btn = lookup("#increment_" + EARTH_CACTUS).query();
        Button decResource1Btn = lookup("#decrement_" + EARTH_CACTUS).query();

        // Resource 2 - mars bar UI elements
        Label amountMarsBar = lookup("#" + MARS_BAR).query();
        Button incResource2Btn = lookup("#increment_" + MARS_BAR).query();
        Button decResource2Btn = lookup("#decrement_" + MARS_BAR).query();

        // Resource 3 - moon rock UI elements
        Label amountMoonRock = lookup("#" + MOON_ROCK).query();
        Button incResource3Btn = lookup("#increment_" + MOON_ROCK).query();
        Button decResource3Btn = lookup("#decrement_" + MOON_ROCK).query();

        // Resource 4 - neptune crystal UI elements
        Label amountNeptuneCrystal = lookup("#" + NEPTUNE_CRYSTAL).query();
        Button incResource4Btn = lookup("#increment_" + NEPTUNE_CRYSTAL).query();
        Button decResource4Btn = lookup("#decrement_" + NEPTUNE_CRYSTAL).query();

        // Resource 5 - venus grain UI elements
        Label amountVenusGrain = lookup("#" + VENUS_GRAIN).query();
        Button incResource5Btn = lookup("#increment_grain").query();
        Button decResource5Btn = lookup("#decrement_grain").query();

        Button discardButton = lookup("#getResourcesButton").query();

        // Assert discard button
        Assertions.assertEquals(discardButton.getText(), "Get 0/2");
        Assertions.assertTrue(discardButton.isDisabled());

        // Assert earth cactus UI elements
        Assertions.assertEquals(amountEarthCactus.getText(), "0");
        Assertions.assertFalse(incResource1Btn.isDisabled());
        Assertions.assertFalse(decResource1Btn.isDisabled());

        // Assert mars bar UI elements
        Assertions.assertEquals(amountMarsBar.getText(), "0");
        Assertions.assertFalse(incResource2Btn.isDisabled());
        Assertions.assertFalse(decResource2Btn.isDisabled());

        // Assert moon rock UI elements
        Assertions.assertEquals(amountMoonRock.getText(), "0");
        Assertions.assertFalse(incResource3Btn.isDisabled());
        Assertions.assertFalse(decResource3Btn.isDisabled());

        // Assert neptune crystal UI elements
        Assertions.assertEquals(amountNeptuneCrystal.getText(), "0");
        Assertions.assertFalse(incResource4Btn.isDisabled());
        Assertions.assertFalse(decResource4Btn.isDisabled());

        // Assert venus grain UI elements
        Assertions.assertEquals(amountVenusGrain.getText(), "0");
        Assertions.assertFalse(incResource5Btn.isDisabled());
        Assertions.assertFalse(decResource5Btn.isDisabled());
    }

    @Test
    void adjustResourceTest() {
        // Resource 1 - earth cactus UI elements
        Label amountEarthCactus = lookup("#" + EARTH_CACTUS).query();
        Button incResource1Btn = lookup("#increment_" + EARTH_CACTUS).query();
        Button decResource1Btn = lookup("#decrement_" + EARTH_CACTUS).query();

        Button discardButton = lookup("#getResourcesButton").query();

        // change amount of earth cactus
        clickOn(incResource1Btn);

        Assertions.assertFalse(incResource1Btn.isDisabled());
        Assertions.assertFalse(decResource1Btn.isDisabled());
        Assertions.assertEquals(amountEarthCactus.getText(), "1");
        Assertions.assertEquals(discardButton.getText(), "Get 1/2");

        clickOn(decResource1Btn);

        Assertions.assertEquals(amountEarthCactus.getText(), "0");
        Assertions.assertFalse(incResource1Btn.isDisabled());
        Assertions.assertFalse(decResource1Btn.isDisabled());
        Assertions.assertEquals(discardButton.getText(), "Get 0/2");
    }

    @Test
    void getResources() {
        this.resources.put(EARTH_CACTUS, 2);
        this.resources.put(NEPTUNE_CRYSTAL, 0);
        this.resources.put(MOON_ROCK, 0);
        this.resources.put(VENUS_GRAIN, 0);
        this.resources.put(MARS_BAR, 0);

        when(pioneersService.yearOfPlentyCard(any(), any()))
                .thenReturn(Observable.just(new Move("createdAt", "_id", "gameId",
                        "userId", "action", 5, null, null, this.resources, null, null)));

        Button incResource1Btn = lookup("#increment_" + EARTH_CACTUS).query();
        Button GetButton = lookup("#getResourcesButton").query();

        clickOn(incResource1Btn);
        clickOn(incResource1Btn);

        Assertions.assertFalse(GetButton.isDisabled());

        clickOn(GetButton);

        verify(pioneersService).yearOfPlentyCard(null, this.resources);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        resources.clear();
        pioneersService = null;
    }
}
