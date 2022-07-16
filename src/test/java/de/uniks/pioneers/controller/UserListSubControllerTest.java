package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;

@ExtendWith(MockitoExtension.class)
public class UserListSubControllerTest extends ApplicationTest {

	@InjectMocks
	UserListSubController userListSubController;

	@ExtendWith(MockitoExtension.class)
	public void start(Stage stage) {
		// start application
		App app = new App(userListSubController);
		app.start(stage);
	}

	@Test
	public void testUserOnline() {
		Label usernameLabel = lookup("#userNameLabel").query();
		Circle statusCircle = lookup("#userStatusCircle").query();

		Assertions.assertThat(usernameLabel.getText()).isEqualTo("username");
		Assertions.assertThat(statusCircle.getFill()).isEqualTo(Color.WHITE);

	}

	@Override
	public void stop() throws Exception {
		super.stop();
		userListSubController =null;
	}
}
