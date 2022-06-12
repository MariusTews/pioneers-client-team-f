package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameLobbyControllerTest extends ApplicationTest {
    // TODO: fix test
    /*
    @Mock
    UserService userService;

    @Mock
    MessageService messageService;

    @Mock
    MemberService memberService;

    @Mock
    GameService gameService;


    @Mock
    EventListener eventListener;

    @Spy
    GameIDStorage gameIDStorage;

    @Spy
    IDStorage idStorage;

    @Spy
    MemberIDStorage memberIDStorage;

    @InjectMocks
    LobbyController controller;

    @InjectMocks
    GameLobbyController gameLobbyController;

    @ExtendWith(MockitoExtension.class)
    public void start(Stage stage) {

        when(userService.findAllUsers()).thenReturn(Observable.empty());
        when(memberService.getAllGameMembers(any())).thenReturn(Observable.just(List.of(new Member("0:00",
                "0:30", "id", "4", false, "#000000"))));
        when(eventListener.listen(any(), any())).thenReturn(Observable.empty());
        when(gameService.findOneGame("id")).thenReturn(Observable.just(new Game("0:00", "0:30",
                                                        "id", "name", "4", 2, false)));


        // start application
        final App app = new App(gameLobbyController);
        app.start(stage);

    }


    @Test
    void leave() {

        when(idStorage.getID()).thenReturn("2");
        when(gameIDStorage.getId()).thenReturn("id");
        when(memberService.leave("id", "2")).thenReturn(Observable.just(new Member("0:00",
                "0:30", "id", "2", false, "#000000")));

        write("\t\t\t\t");
        type(KeyCode.SPACE);

        verify(memberService).leave("id", "2");
    }

    @Test
    void leaveLastMember() {
        when(idStorage.getID()).thenReturn("4");
        when(gameIDStorage.getId()).thenReturn("id");
        when(gameService.deleteGame("id")).thenReturn(Observable.just(new Game("0:00", "0:30",
                                                        "id", "name", "owner", 1, false)));

        write("\t\t\t\t");
        type(KeyCode.SPACE);

        verify(gameService).deleteGame("id");


    }*/
}
