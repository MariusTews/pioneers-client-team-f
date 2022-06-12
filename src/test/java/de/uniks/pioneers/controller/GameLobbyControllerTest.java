package de.uniks.pioneers.controller;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

@ExtendWith(MockitoExtension.class)
class GameLobbyControllerTest extends ApplicationTest {
    // TODO: fix test

    /*@Mock
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
        when(userService.findOne(any())).thenReturn(Observable.empty());
        when(eventListener.listen(any(), any())).thenReturn(Observable.empty());
        when(memberService.getAllGameMembers("id")).thenReturn(Observable.just(List.of(new Member("0:00",
                "0:30", "id", "4", false, "#000000"))));
        when(gameService.findOneGame(any())).thenReturn(Observable.just(new Game("0:00", "0:30",
                                                        "id", "name", "4", 2, false)));
        when(gameIDStorage.getId()).thenReturn("id");


        // start application
        final App app = new App(gameLobbyController);
        app.start(stage);

    }


    @Test
    void leave() {

        when(idStorage.getID()).thenReturn("4");
        when(gameIDStorage.getId()).thenReturn("id");
        when(memberService.leave("id", "4")).thenReturn(Observable.just(new Member("0:00",
                "0:30", "id", "4", false, "#000000")));

        write("\t\t\t\t");
        type(KeyCode.SPACE);

        verify(memberService).leave("id", "4");
    }

    @Test
    void leaveLastMember() {
        when(idStorage.getID()).thenReturn("4");
        when(gameIDStorage.getId()).thenReturn("id");
        when(gameService.findOneGame("id")).thenReturn(Observable.just(new Game("0:00", "0:30",
                                                    "id", "name", "owner", 1, false)));
        when(gameService.deleteGame("id")).thenReturn(Observable.just(new Game("0:00", "0:30",
                                                        "id", "name", "owner", 1, false)));

        write("\t\t\t\t");
        type(KeyCode.SPACE);

        verify(gameService).deleteGame("id");


    }*/
}
