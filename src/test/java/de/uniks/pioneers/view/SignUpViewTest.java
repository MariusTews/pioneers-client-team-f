package de.uniks.pioneers.view;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.SignUpController;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;

class SignUpViewTest extends ApplicationTest {
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
    public void testViewParameters() {
        // open signUp directly
        app.show(new SignUpController());

        TextField username = lookup("#usernameTextField").query();
        clickOn(username);
        write("test");
        Assertions.assertThat(username.getText()).isEqualTo("test");

        Button login = lookup("#signUpButton").query();
        Assertions.assertThat(login.getText()).isEqualTo("Sign up");

        PasswordField passwordField = lookup("#passwordField").query();
        clickOn(passwordField);
        write("test");
        Assertions.assertThat(passwordField.getText()).isEqualTo("test");

        PasswordField repeatPasswordField = lookup("#repeatPasswordField").query();
        clickOn(repeatPasswordField);
        write("test");
        Assertions.assertThat(repeatPasswordField.getText()).isEqualTo("test");

    }
}
