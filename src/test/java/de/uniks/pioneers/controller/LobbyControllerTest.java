package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.dto.ErrorResponse;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.*;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LobbyControllerTest extends ApplicationTest {

    @Mock
    UserService userService;

    @Mock
    MemberService memberService;

    @Mock
    GameService gameService;

    @Mock
    GroupService groupService;

    @Mock
    AuthService authService;


    @Mock
    EventListener eventListener;


    @Spy
    IDStorage idStorage;

    @InjectMocks
    LobbyController lobbyController;

    @ExtendWith(MockitoExtension.class)
    public void start(Stage stage) {
        // empty init
        when(groupService.getAll()).thenReturn(Observable.empty());
        when(userService.findAllUsers()).thenReturn(Observable.empty());
        when(gameService.findAllGames()).thenReturn(Observable.empty());
        when(eventListener.listen(any(), any())).thenReturn(Observable.empty());


        // start application
        final App app = new App(lobbyController);
        app.start(stage);

    }


    @Test
    void logout() {
        when(idStorage.getID()).thenReturn("4");
        when(userService.statusUpdate("4", "offline")).thenReturn(Observable.just(new User("id","name", "status", "avatar")));
        when(authService.logout()).thenReturn(Observable.just(new ErrorResponse(123, "error", "message")));


        write("\t");
        type(KeyCode.SPACE);

        verify(userService).statusUpdate("4", "offline");
        verify(authService).logout();
    }
}
