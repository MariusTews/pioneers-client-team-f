package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

@ExtendWith(MockitoExtension.class)
public class MapEditorControllerTest extends ApplicationTest {
    @InjectMocks
    MapEditorController mapEditorController;

    @ExtendWith(MockitoExtension.class)
    public void start(Stage stage) {
        App app = new App(mapEditorController);
        app.start(stage);
    }

    @Test
    public void UIElementsTest() {
        Label mapSizeLabel = lookup("#mapSizeLabel").query();

        clickOn("#mapSizeMinusButton");
        clickOn("#mapSizePlusButton");
        clickOn("#saveButton");

        Assertions.assertEquals("0", mapSizeLabel.getText());
    }
}
