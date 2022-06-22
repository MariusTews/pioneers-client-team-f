package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.GameSettings;
import de.uniks.pioneers.service.GameService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;

@ExtendWith(MockitoExtension.class)
class CreateGameControllerTest extends ApplicationTest {

	@Mock
	GameService gameService;

	@InjectMocks
	CreateGameController createGameController;

	@ExtendWith(MockitoExtension.class)
	public void start(Stage stage) {
		// start application
		App app = new App(createGameController);
		app.start(stage);
	}


	@Test
	void createGameButtonPressed() {
		when(gameService.create(anyString(), anyString(), anyInt(), anyInt())).thenReturn(Observable.just(new Game("0:00", "now", "01", "Test game", "Alice", 1, false, new GameSettings(2, 10))));

		write("\t\t\t\t\t\t\t");
		type(KeyCode.SPACE);

		verifyThat("OK", NodeMatchers.isVisible());
		Node dialogPane = lookup(".dialog-pane").query();
		from(dialogPane).lookup((Text t) -> t.getText().startsWith("the name"));

		type(KeyCode.SPACE);

		write("\tTest game\t\t\t\t\t\t\t");
		type(KeyCode.SPACE);

		verifyThat("OK", NodeMatchers.isVisible());
		dialogPane = lookup(".dialog-pane").query();
		from(dialogPane).lookup((Text t) -> t.getText().startsWith("the password"));

		type(KeyCode.SPACE);
		write("\t\t123\t\t\t\t\t\t");
		type(KeyCode.SPACE);

		verify(gameService).create("Test game", "123", 2, 10);
	}

	@Test
	public void mapSizeButtons() {
		Label mapSize = lookup("#mapSizeLabel").query();
		Button minus = lookup("#mapSizeMinusButton").query();
		Button plus = lookup("#mapSizePlusButton").query();

		Assertions.assertEquals(mapSize.getText(), "2");
		Assertions.assertTrue(minus.isVisible());
		Assertions.assertTrue(plus.isVisible());

		clickOn(minus);
		clickOn(minus);

		Assertions.assertFalse(minus.isVisible());
		clickOn(plus);
		Assertions.assertTrue(minus.isVisible());

		clickOn(plus);
		clickOn(plus);
		clickOn(plus);
		clickOn(plus);
		clickOn(plus);
		clickOn(plus);
		clickOn(plus);
		clickOn(plus);
		clickOn(plus);

		Assertions.assertFalse(plus.isVisible());
		clickOn(minus);
		Assertions.assertTrue(plus.isVisible());
	}

	@Test
	public void victoryPointButtons() {
		Label victoryPoints = lookup("#victoryPointsLabel").query();
		Button minus = lookup("#victoryPointsMinusButton").query();
		Button plus = lookup("#victoryPointsPlusButton").query();

		Assertions.assertEquals(victoryPoints.getText(), "10");
		Assertions.assertTrue(minus.isVisible());
		Assertions.assertTrue(plus.isVisible());

		clickOn(plus);
		clickOn(plus);
		clickOn(plus);
		clickOn(plus);
		clickOn(plus);

		Assertions.assertFalse(plus.isVisible());
		clickOn(minus);
		Assertions.assertTrue(plus.isVisible());

		clickOn(minus);
		clickOn(minus);
		clickOn(minus);
		clickOn(minus);
		clickOn(minus);
		clickOn(minus);
		clickOn(minus);
		clickOn(minus);
		clickOn(minus);
		clickOn(minus);
		clickOn(minus);

		Assertions.assertFalse(minus.isVisible());
		clickOn(plus);
		Assertions.assertTrue(minus.isVisible());
	}
}
