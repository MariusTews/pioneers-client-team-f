package de.uniks.pioneers.view;

import de.uniks.pioneers.App;
import de.uniks.pioneers.controller.LobbyController;
import de.uniks.pioneers.controller.RulesScreenController;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Provider;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RulesScreenViewTest extends ApplicationTest {
    private Stage stage;
    private App app;

    @Spy
    Provider <LobbyController> lobbyController;

    @InjectMocks
    RulesScreenController rulesScreenController;

    @Override
    public void start(Stage stage) {
        // start application
        this.stage = stage;
        this.app = new App(rulesScreenController);
        this.app.start(stage);
    }

    @Test
    public void viewAllTest(){
        rulesScreenController = Mockito.spy(new RulesScreenController(app,lobbyController));

        rulesScreenController.render();
        verify(rulesScreenController,times(1)).render();

    }

}
