package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.dto.ErrorResponse;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.service.*;
import de.uniks.pioneers.websocket.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
class LobbyControllerTest extends ApplicationTest {

    @Mock
    UserService userService;

    @Mock
    GameService gameService;

    @Mock
    MessageService messageService;

    @Mock
    MemberService memberService;

    @Mock
    GroupService groupService;

    @Mock
    AuthService authService;


    @Mock
    EventListener eventListener;


    @Spy
    IDStorage idStorage;

    @SuppressWarnings("unused")
    @Spy
	GameStorage gameStorage;

     @Spy
     App app;

    @InjectMocks
    LobbyController lobbyController;

    @InjectMocks
    EditUserController editUserController;

    private Subject<Event<Game>> gameSubject;
    private Subject<Event<Message>> messageSubject;

    private Subject<Event<Group>> groupSubject;

    private Subject<Event<User>> userSubject;

    @ExtendWith(MockitoExtension.class)
    public void start(Stage stage) {
        /*File file = new File(Objects.requireNonNull(Main.class.getResource("defaultPicture.png")).getFile());

        String avatar = editUserController.encodeFileToBase64Binary(file);*/

        userSubject = PublishSubject.create();
        gameSubject = PublishSubject.create();
        messageSubject = PublishSubject.create();
        groupSubject = PublishSubject.create();

        List<Member> memberList =  new ArrayList<>();
        memberList.add(new Member("0","2","id","3",true,"#00012f",false));
        when(memberService.getAllGameMembers(any())).thenReturn(Observable.just(memberList));

        List<Game> gameList = new ArrayList<>();
        gameList.add(new Game("0:00", "0:30",
                "id", "name", "owner", 2, false,new GameSettings(2,10)));
        when(gameService.findAllGames()).thenReturn(Observable.just(gameList));

        List<User> userList = new ArrayList<>();
        userList.add(new User("0","1", "7", "Bob", "online",null, null));
        when(userService.findAllUsers()).thenReturn(Observable.just(userList));

        List<Message> messages = new ArrayList<>();
        messages.add(new Message("0","1", "78", "7", "first message"));
        when(messageService.getAllMessages(any(),any())).thenReturn(Observable.just(messages));

        List<Group> groupList = new ArrayList<>();
        groupList.add(new Group("0","2","uid","Tom",new ArrayList<>()));
        when(groupService.getAll()).thenReturn(Observable.just(groupList));
        when(gameStorage.getId()).thenReturn("627cf3c93496bc00158f3859");

        when(app.getStage()).thenReturn(new Stage());

        when(eventListener.listen("users.*.*",User.class)).thenReturn(userSubject);
        when(eventListener.listen("games.*.*",Game.class)).thenReturn(gameSubject);
        when(eventListener.listen("group.*.*",Group.class)).thenReturn(groupSubject);
        when(eventListener.listen("global.627cf3c93496bc00158f3859.messages.*.*",Message.class)).thenReturn(messageSubject);


        // start application
        app = new App(lobbyController);
        app.start(stage);
    }


    @Test
    void logout() {
        when(idStorage.getID()).thenReturn("4");
        when(userService.statusUpdate("4", "offline")).thenReturn(Observable.just(new User("1234","12345","id",
                                                    "name", "status", "avatar", new ArrayList<>())));
        when(authService.logout()).thenReturn(Observable.just(new ErrorResponse(123, "error", "message")));


        write("\t");
        type(KeyCode.SPACE);

        verify(userService).statusUpdate("4", "offline");
        verify(authService).logout();
    }

    @Test
    void lobbyEventListenerTest(){

        /*File file = new File(Objects.requireNonNull(Main.class.getResource("defaultPicture.png")).getFile());

        String avatar = editUserController.encodeFileToBase64Binary(file);*/
        userSubject.onNext(new Event<>(".created",new User("0","1","8","Tom","online",null,
                new ArrayList<>())));
        userSubject.onNext(new Event<>(".updated",new User("0","1","8","Tom","online",null,
                new ArrayList<>())));
        waitForFxEvents();

        gameSubject.onNext(new Event<>(".created", new Game("0","1","2", "ert","2",0,false,
                new GameSettings(2,10))));
        gameSubject.onNext(new Event<>(".updated", new Game("0","1","2", "ert","2",0,true,
                new GameSettings(2,10))));
        waitForFxEvents();


        //members for Group
        List<String> memberForGroup = new ArrayList<>();
        groupSubject.onNext(new Event<>(".created",new Group("1","2","id","ert",memberForGroup)));
        memberForGroup.add("Tom");
        groupSubject.onNext(new Event<>(".updated",new Group("1","2","id","ert",memberForGroup)));
        waitForFxEvents();

        messageSubject.onNext(new Event<>(".created", new Message("1","2",
                "627cf3c93496bc00158f3859","7","Hello!!")));
        messageSubject.onNext(new Event<>(".updated", new Message("1","2",
                "627cf3c93496bc00158f3859","7","no!!")));
        waitForFxEvents();


    }

    @Test
    void createGameTest(){
        when(gameStorage.getId()).thenReturn("id");
        when(idStorage.getID()).thenReturn("3");
        List<Member> memberList =  new ArrayList<>();
        memberList.add(new Member("0","2","id","3",true,"#00012f",false));
        when(memberService.getAllGameMembers("id")).thenReturn(Observable.just(memberList));

        lobbyController.createGameButtonPressed();

    }

    @Test
    void joinGameTest(){
        when(gameStorage.getId()).thenReturn("id");
        when(idStorage.getID()).thenReturn("3");
        List<Member> memberList =  new ArrayList<>();
        memberList.add(new Member("0","2","id","3",true,"#00012f",false));
        when(memberService.getAllGameMembers("id")).thenReturn(Observable.just(memberList));
        //when(memberService.join("id","00000")).thenReturn(Observable.empty());
        Game game = new Game("0","1","2", "ert","2",0,true,
                new GameSettings(2,10));
        lobbyController.joinGame(game);

        verify(memberService).getAllGameMembers("id");

    }

    @Test
    void joinGameNullTest() {
        when(gameStorage.getId()).thenReturn(null);
        Game game = new Game("0", "1", "2", "ert", "2", 0, true,
                new GameSettings(2, 10));

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lobbyController.joinGame(game);
            }
        });

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        lobbyController =null;
        editUserController = null;
        app = null;
        messageSubject.onComplete();
        userSubject.onComplete();
        gameSubject.onComplete();
        groupSubject.onComplete();
    }
}
