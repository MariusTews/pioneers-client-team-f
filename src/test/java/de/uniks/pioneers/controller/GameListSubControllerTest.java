package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.Game;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;

public class GameListSubControllerTest extends ApplicationTest {
	private Stage stage;
	private App app;

	private Game game = new Game(null,null,"1234","TestGame","TestOwner",1);

	public void start(Stage stage) {
		// start application
		this.stage = stage;
		this.app = new App(new GameListSubController(app,game));
		this.app.start(stage);
	}

	@Test
	public void testViewParameters() {
		Label gameName = lookup("#gameNameLabel").query();
		Button button = lookup("#joinButton").query();
		Assertions.assertThat(gameName.getText()).isEqualTo(game.name());
		Assertions.assertThat(button.getText()).isEqualTo("join");
	}
}
