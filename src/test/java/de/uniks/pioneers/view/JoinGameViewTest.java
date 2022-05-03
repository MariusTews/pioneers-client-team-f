package de.uniks.pioneers.view;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.JoinGameController;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;

public class JoinGameViewTest extends ApplicationTest {

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
    public void testViewParameters()
    {
        //opens the JoinGame screen to test the View
        app.show(new JoinGameController());

        Button backButtonJoinGame = lookup("#backButtonJoinGame").query();
        Button joinButton = lookup("#joinButton").query();

        TextField passwordTextField = lookup("#passwordTextField").query();

        Assertions.assertThat(backButtonJoinGame.getText()).isEqualTo("Back");
        Assertions.assertThat(joinButton.getText()).isEqualTo("Join");

        Assertions.assertThat(passwordTextField.getText()).isEqualTo("");

        clickOn(passwordTextField);
        write("test");
        Assertions.assertThat(passwordTextField.getText()).isEqualTo("test");

        clickOn(backButtonJoinGame);
        clickOn(joinButton);
    }
}
