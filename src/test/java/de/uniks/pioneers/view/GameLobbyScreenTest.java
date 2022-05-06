package de.uniks.pioneers.view;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.GameLobbyController;
import de.uniks.pioneers.service.GameLobbyService;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class GameLobbyScreenTest extends ApplicationTest {

    private Stage stage;
    private App app;

    @Override
    public void start(Stage stage) {
        // start application
        this.stage = stage;
        this.app = new App();
        this.app.start(stage);
    }

    @Test
    public void testGameLobbyScreen() {
        // show GameLobbyScreen
        app.show(new GameLobbyController());

        // buttons
        Button leaveButton = lookup("#idLeaveButton").query();
        Button sendButton = lookup("#idSendButton").query();
        Button readyButton = lookup("#idReadyButton").query();
        Button startGameButton = lookup("#idStartGameButton").query();

        // label
        Label titleLabel = lookup("#idTitleLabel").query();

        // textfield
        TextField messageField = lookup("#idMessageField").query();

        // assertions buttons
        Assertions.assertEquals(leaveButton.getText(), "Leave");
        Assertions.assertEquals(sendButton.getText(), "Send");
        Assertions.assertEquals(readyButton.getText(), "Ready");
        Assertions.assertEquals(startGameButton.getText(), "Start Game");

        // assertion title
        Assertions.assertEquals(titleLabel.getText(), "Welcome to -the game-");

        // assertion message field
        clickOn(messageField);
        write("something");
        Assertions.assertEquals(messageField.getText(), "something");

    }
}
