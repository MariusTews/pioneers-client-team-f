package de.uniks.pioneers.controller;

import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.AchievementsService;
import de.uniks.pioneers.service.GameStorage;
import de.uniks.pioneers.service.PioneersService;
import de.uniks.pioneers.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.HashMap;

import static de.uniks.pioneers.Constants.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeAcceptSubcontrollerTest extends ApplicationTest {
    User user = new User("0", "0", "u1", "Bob", "s", "a", null);

    @Mock
    UserService userService;

    @Mock
    PioneersService pioneersService;

    @Mock
    AchievementsService achievementsService;

    @Spy
    GameStorage gameStorage;

    User tradePartner;

    @ExtendWith(MockitoExtension.class)
    public void start(Stage stage) {
        when(userService.findAllUsers()).thenReturn(Observable.just(user).buffer(1));

        TradeAcceptSubcontroller tradeAcceptSubcontroller = new TradeAcceptSubcontroller(userService, pioneersService, achievementsService, gameStorage);
        tradeAcceptSubcontroller.init();
        tradeAcceptSubcontroller.addUser(user);
        tradeAcceptSubcontroller.render();
    }

    @Test
    public void tradeTestFailure() {
        Button tradeButton = lookup("#tradeButton").queryButton();
        clickOn(tradeButton);

        assertNull(tradePartner);
    }
}
