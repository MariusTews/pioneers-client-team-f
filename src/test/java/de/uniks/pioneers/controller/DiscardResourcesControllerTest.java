package de.uniks.pioneers.controller;

import de.uniks.pioneers.model.Move;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.service.PioneersService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.HashMap;

import static de.uniks.pioneers.Constants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscardResourcesControllerTest extends ApplicationTest {

    @Mock
    PioneersService pioneersService;

     final HashMap<String, Integer> resources = new HashMap<>() {{
        put(VENUS_GRAIN, 1);
        put(MOON_ROCK, 2);
        put(MARS_BAR, 3);
        put(NEPTUNE_CRYSTAL, 4);
        put(EARTH_CACTUS, 1);
    }};

    @ExtendWith(MockitoExtension.class)
    public void start(Stage stage) {
        // open new window therefore starting via app is not needed (discard window has another root)
        DiscardResourcesController discardResourcesController = new DiscardResourcesController(new Player("01", "00",
                Color.DARKORCHID.toString(), true,6, resources, null,2,
                2,null), "01", pioneersService, null);
        discardResourcesController.render();
    }

    // Check if the buttons are disabled correctly
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

        Button discardButton = lookup("#discardButton").query();

        // Assert discard button
        Assertions.assertEquals(discardButton.getText(), "Discard 0/5");
        Assertions.assertTrue(discardButton.isDisabled());

        // Assert earth cactus UI elements
        Assertions.assertEquals(amountEarthCactus.getText(), "0");
        Assertions.assertFalse(incResource1Btn.isDisabled());
        Assertions.assertTrue(decResource1Btn.isDisabled());

        // Assert mars bar UI elements
        Assertions.assertEquals(amountMarsBar.getText(), "0");
        Assertions.assertFalse(incResource2Btn.isDisabled());
        Assertions.assertTrue(decResource2Btn.isDisabled());

        // Assert moon rock UI elements
        Assertions.assertEquals(amountMoonRock.getText(), "0");
        Assertions.assertFalse(incResource3Btn.isDisabled());
        Assertions.assertTrue(decResource3Btn.isDisabled());

        // Assert neptune crystal UI elements
        Assertions.assertEquals(amountNeptuneCrystal.getText(), "0");
        Assertions.assertFalse(incResource4Btn.isDisabled());
        Assertions.assertTrue(decResource4Btn.isDisabled());

        // Assert venus grain UI elements
        Assertions.assertEquals(amountVenusGrain.getText(), "0");
        Assertions.assertFalse(incResource5Btn.isDisabled());
        Assertions.assertTrue(decResource5Btn.isDisabled());
    }

    // Check for every resource if the discard button computes the right amount
    @Test
    void adjustResource1Test() {
        // Resource 1 - earth cactus UI elements
        Label amountEarthCactus = lookup("#" + EARTH_CACTUS).query();
        Button incResource1Btn = lookup("#increment_" + EARTH_CACTUS).query();
        Button decResource1Btn = lookup("#decrement_" + EARTH_CACTUS).query();

        Button discardButton = lookup("#discardButton").query();

        // change amount of earth cactus and assert that disable property and amount label changes
        clickOn(incResource1Btn);

        Assertions.assertTrue(incResource1Btn.isDisabled());
        Assertions.assertFalse(decResource1Btn.isDisabled());
        Assertions.assertEquals(amountEarthCactus.getText(), "1");
        Assertions.assertEquals(discardButton.getText(), "Discard 1/5");

        clickOn(decResource1Btn);

        Assertions.assertEquals(amountEarthCactus.getText(), "0");
        Assertions.assertFalse(incResource1Btn.isDisabled());
        Assertions.assertTrue(decResource1Btn.isDisabled());
        Assertions.assertEquals(discardButton.getText(), "Discard 0/5");
    }

    @Test
    void adjustResource2Test() {
        // Resource 2 - mars bar UI elements
        Label amountMarsBar = lookup("#" + MARS_BAR).query();
        Button incResource2Btn = lookup("#increment_" + MARS_BAR).query();
        Button decResource2Btn = lookup("#decrement_" + MARS_BAR).query();

        Button discardButton = lookup("#discardButton").query();

        // change amount of marsBar and assert that disable property and amount label changes
        clickOn(incResource2Btn);
        clickOn(incResource2Btn);
        clickOn(incResource2Btn);

        Assertions.assertTrue(incResource2Btn.isDisabled());
        Assertions.assertFalse(decResource2Btn.isDisabled());
        Assertions.assertEquals(amountMarsBar.getText(), "3");
        Assertions.assertEquals(discardButton.getText(), "Discard 3/5");

        clickOn(decResource2Btn);
        clickOn(decResource2Btn);
        clickOn(decResource2Btn);

        Assertions.assertEquals(amountMarsBar.getText(), "0");
        Assertions.assertFalse(incResource2Btn.isDisabled());
        Assertions.assertTrue(decResource2Btn.isDisabled());
        Assertions.assertEquals(discardButton.getText(), "Discard 0/5");
    }

    @Test
    void adjustResource3Test() {
        // Resource 3 - moon rock UI elements
        Label amountMoonRock = lookup("#" + MOON_ROCK).query();
        Button incResource3Btn = lookup("#increment_" + MOON_ROCK).query();
        Button decResource3Btn = lookup("#decrement_" + MOON_ROCK).query();

        Button discardButton = lookup("#discardButton").query();

        // change amount of moon rock and assert that disable property and amount label changes
        clickOn(incResource3Btn);
        clickOn(incResource3Btn);

        Assertions.assertTrue(incResource3Btn.isDisabled());
        Assertions.assertFalse(decResource3Btn.isDisabled());
        Assertions.assertEquals(amountMoonRock.getText(), "2");
        Assertions.assertEquals(discardButton.getText(), "Discard 2/5");

        clickOn(decResource3Btn);
        clickOn(decResource3Btn);

        Assertions.assertEquals(amountMoonRock.getText(), "0");
        Assertions.assertFalse(incResource3Btn.isDisabled());
        Assertions.assertTrue(decResource3Btn.isDisabled());
        Assertions.assertEquals(discardButton.getText(), "Discard 0/5");
    }

    @Test
    void adjustResource4Test() {
        // Resource 4 - neptune crystal UI elements
        Label amountNeptuneCrystal = lookup("#" + NEPTUNE_CRYSTAL).query();
        Button incResource4Btn = lookup("#increment_" + NEPTUNE_CRYSTAL).query();
        Button decResource4Btn = lookup("#decrement_" + NEPTUNE_CRYSTAL).query();

        Button discardButton = lookup("#discardButton").query();

        // change amount of neptune crystal and assert buttons + amount
        clickOn(incResource4Btn);
        clickOn(incResource4Btn);
        clickOn(incResource4Btn);
        clickOn(incResource4Btn);

        Assertions.assertTrue(incResource4Btn.isDisabled());
        Assertions.assertFalse(decResource4Btn.isDisabled());
        Assertions.assertEquals(amountNeptuneCrystal.getText(), "4");
        Assertions.assertEquals(discardButton.getText(), "Discard 4/5");

        clickOn(decResource4Btn);
        clickOn(decResource4Btn);
        clickOn(decResource4Btn);
        clickOn(decResource4Btn);

        Assertions.assertEquals(amountNeptuneCrystal.getText(), "0");
        Assertions.assertFalse(incResource4Btn.isDisabled());
        Assertions.assertTrue(decResource4Btn.isDisabled());
        Assertions.assertEquals(discardButton.getText(), "Discard 0/5");
    }
    @Test
    void adjustResource5Test() {
        // Resource 5 - venus grain UI elements
        Label amountVenusGrain = lookup("#" + VENUS_GRAIN).query();
        Button incResource5Btn = lookup("#increment_" + VENUS_GRAIN).query();
        Button decResource5Btn = lookup("#decrement_" + VENUS_GRAIN).query();

        Button discardButton = lookup("#discardButton").query();

        // change amount of venus grain and assert that disable property and amount label changes
        clickOn(incResource5Btn);

        Assertions.assertTrue(incResource5Btn.isDisabled());
        Assertions.assertFalse(decResource5Btn.isDisabled());
        Assertions.assertEquals(amountVenusGrain.getText(), "1");
        Assertions.assertEquals(discardButton.getText(), "Discard 1/5");

        clickOn(decResource5Btn);

        Assertions.assertEquals(amountVenusGrain.getText(), "0");
        Assertions.assertFalse(incResource5Btn.isDisabled());
        Assertions.assertTrue(decResource5Btn.isDisabled());
        Assertions.assertEquals(discardButton.getText(), "Discard 0/5");
    }

    @Test
    void discardResourcesTest() {
        this.resources.put(EARTH_CACTUS, -1);
        this.resources.put(NEPTUNE_CRYSTAL, -4);
        this.resources.put(MOON_ROCK, 0);
        this.resources.put(VENUS_GRAIN, 0);
        this.resources.put(MARS_BAR, 0);

        when(pioneersService.move(any(), any(), any(), any(), any(),any(), any(), any(), any()))
                .thenReturn(Observable.just(new Move("4", "10", "01", "00", DROP_ACTION,
                        7, null, null, this.resources, null)));

        Button incResource4Btn = lookup("#increment_" + NEPTUNE_CRYSTAL).query();
        Button incResource1Btn = lookup("#increment_" + EARTH_CACTUS).query();
        Button discardButton = lookup("#discardButton").query();

        clickOn(incResource1Btn);
        clickOn(incResource4Btn);
        clickOn(incResource4Btn);
        clickOn(incResource4Btn);
        clickOn(incResource4Btn);

        Assertions.assertFalse(discardButton.isDisabled());

        clickOn(discardButton);

        verify(pioneersService).move("01", DROP_ACTION, null, null, null, null, null, null, this.resources);

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        resources.clear();
        pioneersService = null;
    }
}
