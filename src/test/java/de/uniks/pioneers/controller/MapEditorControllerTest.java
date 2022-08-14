package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

@ExtendWith(MockitoExtension.class)
public class MapEditorControllerTest extends ApplicationTest {
    @Mock
    App app;

    @InjectMocks
    MapEditorController mapEditorController;

    @ExtendWith(MockitoExtension.class)
    public void start(Stage stage) {
        app = new App(mapEditorController);
        app.start(stage);
    }

    @Test
    public void UIElementsTest() {
        Label mapSizeLabel = lookup("#mapSizeLabel").query();

        clickOn("#mapSizePlusButton");
        clickOn("#mapSizeMinusButton");

        Assertions.assertEquals("0", mapSizeLabel.getText());
    }

    @Test
    public void tileButtonTest() {
        clickOn("#x0y0z0_tileButton");

        Button cancel = lookup("#x0y0z0_cancelButton").queryButton();
        TextField numberToken = lookup("#x0y0z0_numberField").query();
        doubleClickOn(numberToken);
        
        type(KeyCode.DIGIT6);
        Assertions.assertEquals("x", cancel.getText());
        Assertions.assertEquals("6", numberToken.getText());
    }
    @Test
    public void harborButtonTest() {
        clickOn("#mapSizePlusButton");
        clickOn("#x0y0z0_tileButton");
        clickOn("#x1yM1z0_harborButton");

        Button cancel = lookup("#x1yM1z0_cancelButton").queryButton();

        Assertions.assertEquals("x", cancel.getText());
    }

    @Test
    public void cancelButtonTest() {
        // init tile
        clickOn("#x0y0z0_tileButton");

        // lookup cancel button and click
        Button cancel = lookup("#x0y0z0_cancelButton").queryButton();

        clickOn(cancel);

        // check if the empty template is visible
        Button addTile = lookup("#x0y0z0_tileButton").queryButton();
        Assertions.assertTrue(addTile.isVisible());
    }
}
