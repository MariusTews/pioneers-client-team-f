package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.User;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;

public class UserListSubControllerTest extends ApplicationTest {
	private Stage stage;
	private App app;

	private User user1 = new User("1234", "test1User", "online",null);

	private User user2 = new User("1234", "test2User", "offline",null);

	public void start(Stage stage) {
		// start application
		this.stage = stage;
		this.app = new App(new UserListSubController(app,user1));
		this.app.start(stage);
	}

	@Test
	public void testUserOnline() {
		Label usernameLabel = lookup("#userNameLabel").query();
		Circle statusCircle = lookup("#userStatusCircle").query();
		Button chatButton = lookup("#chatButton").query();

		Assertions.assertThat(usernameLabel.getText()).isEqualTo(user1.name());
		Assertions.assertThat(statusCircle.getFill()).isEqualTo(Color.GREEN);
		Assertions.assertThat(chatButton.getText()).isEqualTo("chat");
	}

	@Test
	public void testUserOffline() {
		app.show(new UserListSubController(app,user2));

		Label usernameLabel = lookup("#userNameLabel").query();
		Circle statusCircle = lookup("#userStatusCircle").query();
		Button chatButton = lookup("#chatButton").query();

		Assertions.assertThat(usernameLabel.getText()).isEqualTo(user2.name());
		Assertions.assertThat(statusCircle.getFill()).isEqualTo(Color.RED);
		Assertions.assertThat(chatButton.getText()).isEqualTo("chat");
	}
}
