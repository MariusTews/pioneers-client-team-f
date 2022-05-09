package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;

@ExtendWith(MockitoExtension.class)
public class GameListSubControllerTest extends ApplicationTest {

	@InjectMocks
	GameListSubController gameListSubController;
	private Stage stage;
	private App app;

	public void start(Stage stage) {
		// start application
		this.stage = stage;
		this.app = new App(gameListSubController);
		this.app.start(stage);
	}

	@Test
	public void testViewParameters() {
		Label gameName = lookup("#gameNameLabel").query();
		Button joinButton = lookup("#joinButton").query();
		Assertions.assertThat(gameName.getText()).isEqualTo("gamename");
		Assertions.assertThat(joinButton.getText()).isEqualTo("join");
	}
}
