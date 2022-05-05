package de.uniks.pioneers.view;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.LoginController;
import de.uniks.pioneers.controller.SignUpController;
import de.uniks.pioneers.service.UserService;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;

@ExtendWith(MockitoExtension.class)
class SignUpViewTest extends ApplicationTest {
    @Mock
    LoginController loginController;

    @InjectMocks
    SignUpController signUpController;
    private Stage stage;
    private App app;

    @Override
    public void start(Stage stage) {
        // start application
        this.stage = stage;
        this.app = new App(signUpController);
        this.app.start(stage);
    }

    @Test
    public void testViewParameters() {
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
