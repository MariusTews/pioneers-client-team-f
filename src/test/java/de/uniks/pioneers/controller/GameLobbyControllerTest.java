package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.service.*;
import de.uniks.pioneers.websocket.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
class GameLobbyControllerTest extends ApplicationTest {

    @Mock
    UserService userService;

    @Mock
    MemberService memberService;

    @Mock
    MessageService messageService;

    @Mock
    GameService gameService;


    @Mock
    EventListener eventListener;

    @Mock
    App app;

    @Spy
	GameStorage gameStorage;

    @Spy
    IDStorage idStorage;

    @InjectMocks
    GameLobbyController gameLobbyController;
    private Subject<Event<Member>> memberSubject;
    private Subject<Event<Message>> messageSubject;

    @ExtendWith(MockitoExtension.class)
    public void start(Stage stage) {
        memberSubject = PublishSubject.create();
        messageSubject = PublishSubject.create();


        List<Member> members = new ArrayList<>();
        members.add(new Member("2022-11-30T18:35:24.00Z","1:00","87","7",true,"ffa500",false));
        when(memberService.getAllGameMembers(any())).thenReturn(Observable.just(members));
        List<User> userList = new ArrayList<>();
        userList.add(new User("2022-11-30T18:35:24.00Z","1:00","7","Bob","online",null,null));

        List<Message> messages = new ArrayList<>();
        messages.add(new Message("2022-11-30T18:35:24.00Z","1:00","78","7","first message"));
        when(messageService.getAllMessages(any(),any())).thenReturn(Observable.just(messages));

        when(userService.findAllUsers()).thenReturn(Observable.just(userList));
        //when(userService.findOne(any())).thenReturn(Observable.just(new User("2022-11-30T18:35:24.00Z","1:00","8","Alice","online",null,null)));
        when(gameService.findOneGame(any())).thenReturn(Observable.just(new Game("2022-11-30T18:35:24.00Z", "00:30",
                "id", "name", "owner", 2, false,new GameSettings(2,10))));
        when(gameStorage.getId()).thenReturn("id");

        when(eventListener.listen("games.id.*.*",Message.class)).thenReturn(messageSubject);
        when(eventListener.listen("games.id.members.*.*",Member.class)).thenReturn(memberSubject);
        when(eventListener.listen("games.id.messages.*.*",Message.class)).thenReturn(messageSubject);
        when(app.getStage()).thenReturn(new Stage());

        // start application
        App app = new App(gameLobbyController);
        app.start(stage);

    }


    @Test
    void leave() {

        when(idStorage.getID()).thenReturn("4");
        when(memberService.leave("id", "4")).thenReturn(Observable.just(new Member("0:00",
                "0:30", "id", "4", false, "#000000",false)));

        write("\t\t\t\t\t\t");
        type(KeyCode.SPACE);

        verify(memberService).leave("id", "4");
    }

    @Test
    void leaveLastMember() {
        when(gameService.findOneGame(any())).thenReturn(Observable.just(new Game("0:00", "0:30",
                                                    "id", "name", "owner", 1, false,new GameSettings(2,10))));
        when(gameService.deleteGame("id")).thenReturn(Observable.just(new Game("0:00", "0:30",
                                                        "id", "name", "owner", 1, false,new GameSettings(2,10))));

        write("\t\t\t\t\t\t");
        type(KeyCode.SPACE);

        verify(gameService).deleteGame("id");
    }

    @Test
    void eventListenerTest(){
        when(userService.findOne("8")).thenReturn(Observable.just(new User("2022-11-30T18:35:24.00Z","1:00","7","Bob","online",null,null)));
        messageSubject.onNext(new Event<>(".created",new Message("2022-11-30T18:35:24.00Z","1","14","7","test")));
        messageSubject.onNext(new Event<>(".created",new Message("2022-11-30T18:35:24.00Z","1","14","7","test 123")));

        waitForFxEvents();

        memberSubject.onNext(new Event<>(".created",new Member("02022-11-30T19:35:24.00Z","7","01","8",false,null,false)));
        memberSubject.onNext(new Event<>(".updated",new Member("2022-11-30T19:35:24.00Z","7","01","8",true,"#ff0000",false)));
        waitForFxEvents();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        gameLobbyController=null;
        app = null;
        messageSubject.onComplete();
        memberSubject.onComplete();
    }

}
