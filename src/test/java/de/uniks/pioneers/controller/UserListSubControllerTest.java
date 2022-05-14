package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.User;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;

@ExtendWith(MockitoExtension.class)
public class UserListSubControllerTest extends ApplicationTest {

	@Mock
	LobbyController lobbyController;
	@InjectMocks
	UserListSubController userListSubController;
	private Stage stage;
	private App app;

	@ExtendWith(MockitoExtension.class)
	public void start(Stage stage) {
		// start application
		this.stage = stage;
		this.app = new App(userListSubController);
		this.app.start(stage);
	}

	@Test
	public void testUserOnline() {

		Label usernameLabel = lookup("#userNameLabel").query();
		Circle statusCircle = lookup("#userStatusCircle").query();
		Button chatButton = lookup("#chatButton").query();

		Assertions.assertThat(usernameLabel.getText()).isEqualTo("username");
		Assertions.assertThat(statusCircle.getFill()).isEqualTo(Color.WHITE);
		Assertions.assertThat(chatButton.getText()).isEqualTo("chat");
	}
}
