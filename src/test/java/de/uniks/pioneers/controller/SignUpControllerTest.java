package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignUpControllerTest extends ApplicationTest {

    @Mock
    UserService userService;

    @InjectMocks
    SignUpController signUpController;

    @Override
    public void start(Stage stage) {
        new App(signUpController).start(stage);
    }

    @Test
    void register() {
        when(userService.register("Bob", null, "bob bob111")).thenReturn(Observable
                                .just(new User("1234","12345","123", "Bob", "status", null, new ArrayList<>())));

        write("Bob\t");
        write("bob111\t");

        FxAssert.verifyThat("#errorLabel", LabeledMatchers.hasText("Passwords do not match"));
        FxAssert.verifyThat("#signUpButton", NodeMatchers.isDisabled());

        write("bob111\t");

        FxAssert.verifyThat("#errorLabel", LabeledMatchers.hasText("Passwords is too short"));
        FxAssert.verifyThat("#signUpButton", NodeMatchers.isDisabled());

        type(KeyCode.TAB);

        write("Bob\t");
        write("bob bob111\t");
        write("bob bob111\t");

        FxAssert.verifyThat("#signUpButton", NodeMatchers.isEnabled());

        type(KeyCode.SPACE);

        verify(userService).register("Bob", null, "bob bob111");
    }

    @Test
    void usernameAlreadyTaken() {
        when(userService.register("Bob", null, "bob bob111")).thenReturn(Observable.error(new Throwable("HTTP 409 ")));
        write("Bob\t");
        write("bob bob111\t");
        write("bob bob111\t");
        type(KeyCode.SPACE);
        clickOn("OK");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        signUpController = null;
        userService = null;
    }

}
