package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.service.GameStorage;
import de.uniks.pioneers.service.PioneersService;
import de.uniks.pioneers.websocket.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TradingSubControllerTest extends ApplicationTest {

    @Mock
    PioneersService pioneersService;

    @Mock
    EventListener eventListener;

    @Spy
    GameStorage gameStorage;

    @InjectMocks
    TradingSubController tradingSubController;

    @ExtendWith(MockitoExtension.class)
    public void start(Stage stage) {
        when(pioneersService.findAllPlayers(any())).thenReturn(Observable.empty());
        when(pioneersService.findAllTiles(any())).thenReturn(Observable.empty());
        when(eventListener.listen(any(), any())).thenReturn(Observable.empty());
        when(gameStorage.getId()).thenReturn("g1");

        // start application
        App app = new App(tradingSubController);
        app.start(stage);
    }

    @Test
    public void testButtons() {
        Button plus = lookup("#giveCactusPlusButton").queryButton();
        Assertions.assertEquals(plus.getText(), "+");
    }
}
