package de.uniks.pioneers.view;

import de.uniks.pioneers.App;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;

public class LoginViewTest extends ApplicationTest {

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
        TextField username = lookup("#usernameTextField").query();
        clickOn(username);
        write("test");
        Assertions.assertThat(username.getText()).isEqualTo("test");

        Button login = lookup("#loginButton").query();
        Assertions.assertThat(login.getText()).isEqualTo("Login");

        CheckBox checkbox = lookup("#rememberMeCheckBox").query();
        Assertions.assertThat(checkbox.getText()).isEqualTo("Remember me");
        Assertions.assertThat(checkbox.isSelected()).isFalse();

        clickOn(checkbox);
        Assertions.assertThat(checkbox.isSelected()).isTrue();

        PasswordField passwordField = lookup("#userPasswordField").query();
        clickOn(passwordField);
        write("test");
        Assertions.assertThat(passwordField.getText()).isEqualTo("test");
    }
}
