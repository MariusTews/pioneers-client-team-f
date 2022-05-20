package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.service.GameService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.Stage;
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
        when(gameService.create(anyString(),anyString())).thenReturn(Observable.just(new Game("0:00","now", "01", "Test game","Alice",1)));

        write("\t\t \t");
        type(KeyCode.SPACE);

        verifyThat("OK", NodeMatchers.isVisible());
        Node dialogPane = lookup(".dialog-pane").query();
        from(dialogPane).lookup((Text t) -> t.getText().startsWith("the name"));

        type(KeyCode.SPACE);

        write("\tTest game\t\t\t");
        type(KeyCode.SPACE);

        verifyThat("OK", NodeMatchers.isVisible());
        dialogPane = lookup(".dialog-pane").query();
        from(dialogPane).lookup((Text t) -> t.getText().startsWith("the password"));

        type(KeyCode.SPACE);
        write("\t\t123\t\t");
        type(KeyCode.SPACE);

        verify(gameService).create("Test game", "123");
    }
}
