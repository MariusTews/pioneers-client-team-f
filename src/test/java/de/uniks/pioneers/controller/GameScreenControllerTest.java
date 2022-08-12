package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.computation.RandomAction;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.dto.RobDto;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.service.*;
import de.uniks.pioneers.websocket.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static de.uniks.pioneers.Constants.RENAME_FOUNDING_SET2;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
class GameScreenControllerTest extends ApplicationTest {

    @Mock
    UserService userService;
    @Mock
    PioneersService pioneersService;
    @Mock
    MessageService messageService;
    @Mock
    MemberService memberService;
    @Mock
    EventListener eventListener;
    @Mock
    UserStorage userStorage;

    @Mock
    App app;

    @Mock
    AchievementsService achievementsService;

    @Spy
    GameStorage gameStorage;
    @Spy
    IDStorage idStorage;

    @InjectMocks
    GameScreenController gameScreenController;
    @InjectMocks
    RandomAction randomAction;

    Subject<Event<State>> stateSubject;
    Subject<Event<Move>> moveSubject;
    Subject<Event<Player>> playerSubject;
    Subject<Event<Building>> buildingSubject;
    Subject<Event<Message>> messageSubject;
    Subject<Event<Member>> memberSubject;


    @ExtendWith(MockitoExtension.class)
    public void start(Stage stage) {
        stateSubject = PublishSubject.create();
        moveSubject = PublishSubject.create();
        playerSubject = PublishSubject.create();
        buildingSubject = PublishSubject.create();
        messageSubject = PublishSubject.create();
        memberSubject = PublishSubject.create();

        when(eventListener.listen("games.02.state.*", State.class)).thenReturn(stateSubject);
        when(eventListener.listen("games.02.moves.*.created", Move.class)).thenReturn(moveSubject);
        when(eventListener.listen("games.02.players.*.*", Player.class)).thenReturn(playerSubject);
        when(eventListener.listen("games.02.buildings.*.*", Building.class)).thenReturn(buildingSubject);
        when(eventListener.listen("games.02.messages.*.*", Message.class)).thenReturn(messageSubject);
        when(eventListener.listen("games.02.members.*.*", Member.class)).thenReturn(memberSubject);

        List<User> users = new ArrayList<>();
        users.add(new User("0", "1", "01", "Bob", "online", null, null));

        when(userService.findAllUsers()).thenReturn(Observable.just(users));
        when(messageService.getAllMessages(any(), any())).thenReturn(Observable.just(Collections.singletonList(null)));

        when(gameStorage.getId()).thenReturn("02");
        when(gameStorage.getSize()).thenReturn(1);
        when(idStorage.getID()).thenReturn("01");

        //findPlayer fo OneDev-card
        List<DevelopmentCard> devCards1 = new ArrayList<>();
        DevelopmentCard d1 = new DevelopmentCard("knight", true, false);
        devCards1.add(d1);

        Player player1 = new Player("id", "3", "#223", true, 2, null, null,
                4, 5, null, devCards1);
        when(pioneersService.findOnePlayer("02", "01")).thenReturn(Observable.just(player1));

        when(memberService.getAllGameMembers(any())).thenReturn(Observable.empty());
        List<Player> players = new ArrayList<>();
        players.add(new Player("02", "01", "ffff00", true, 3, null, null, 2, 2, null, null));
        when(pioneersService.findAllPlayers(any())).thenReturn(Observable.just(players));
        Map map = new Map("02", createMap(), createHarbors());
        when(pioneersService.findAllTiles(any())).thenReturn(Observable.just(map));
        when(app.getStage()).thenReturn(new Stage());
        when(userStorage.getUserList()).thenReturn(users);
        when(achievementsService.initUserAchievements()).thenReturn(Observable.just(List.of(new Achievement("", "", "1", "first-road", null, 0))));

        App app = new App(gameScreenController);
        app.start(stage);
        gameScreenController.nextMoveLabel.setText(RENAME_FOUNDING_SET2);
        gameScreenController.getGameFieldSubController().loadMap(map);
        HashMap<String, Integer> remain = new HashMap<>();
        remain.put("settlement", 3);
        remain.put("city", 3);
        remain.put("road", 3);
        Player player = new Player("01", "02", "#00ffff", true, 4, null, remain, 2, 2, null, null);
        gameScreenController.getGameFieldSubController().getPlayers().add(player);
    }

    List<Tile> createMap() {
        List<Tile> titles = new ArrayList<>();
        titles.add(new Tile(-1, 1, 0, "forest", 5));
        titles.add(new Tile(-1, 0, 1, "pasture", 5));
        titles.add(new Tile(0, 1, -1, "desert", 7));
        titles.add(new Tile(0, 0, 0, "hills", 5));
        titles.add(new Tile(0, -1, 1, "fields", 6));
        titles.add(new Tile(1, 0, -1, "fields", 8));
        titles.add(new Tile(1, -1, 0, "mountain", 5));

        return titles;
    }

    List<Harbor> createHarbors() {
        List<Harbor> harbors = new ArrayList<>();
        harbors.add(new Harbor(1, 0, -1, null, 1));
        // create Resource Harbors

        harbors.add(new Harbor(1, 0, -1, "grain", 3));
        harbors.add(new Harbor(0, -1, 1, "brick", 5));
        harbors.add(new Harbor(0, -1, 1, "ore", 7));
        harbors.add(new Harbor(-1, 0, 1, "lumber", 9));
        harbors.add(new Harbor(-1, 1, 0, "wool", 11));

        return harbors;
    }

    @Test
    void error() {
        List<ExpectedMove> moves = new ArrayList<>();
        moves.add(new ExpectedMove("founding-settlement-1", null));
        when(pioneersService.findOneState(any())).thenReturn(Observable.just(new State("0", "02", moves, null)));
        when(pioneersService.move(any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(Observable.error(new Throwable()));
        clickOn("#x0y0z0_6");
        verify(pioneersService).move("02", "founding-settlement-1", 0, 0, 0, 6, "settlement", null, null);

        moves.clear();
        moves.add(new ExpectedMove("founding-road-1", null));
        when(pioneersService.findOneState(any())).thenReturn(Observable.just(new State("0", "02", moves, null)));

        clickOn("#x0y0z0_7");
        verify(pioneersService).move("02", "founding-road-1", 0, 0, 0, 7, "road", null, null);
    }

    @Test
    void foundingPhase() {
        List<ExpectedMove> moves = new ArrayList<>();
        moves.add(new ExpectedMove("founding-settlement-1", null));
        when(pioneersService.findOneState(any())).thenReturn(Observable.just(new State("0", "02", moves, null)));
        when(pioneersService.move(any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(Observable.empty());

        clickOn("#x0y0z0_6");
        verify(pioneersService).move("02", "founding-settlement-1", 0, 0, 0, 6, "settlement", null, null);

        moves.clear();
        moves.add(new ExpectedMove("founding-road-1", null));
        when(pioneersService.findOneState(any())).thenReturn(Observable.just(new State("0", "02", moves, null)));

        clickOn("#x0y0z0_7");
        verify(pioneersService).move("02", "founding-road-1", 0, 0, 0, 7, "road", null, null);
    }

    @Test
    void notYourTurn() {
        when(idStorage.getID()).thenReturn("02");
        clickOn("#x0y0z0_6");
        verifyThat("OK", NodeMatchers.isVisible());
        type(KeyCode.SPACE);
    }

    @Test
    void finishTurn() {
        when(gameStorage.getId()).thenReturn("02");
        when(pioneersService.move("02", "build", null, null, null, null, null, null, null)).thenReturn(Observable.empty());

        type(KeyCode.TAB);
        type(KeyCode.TAB);
        type(KeyCode.TAB);
        type(KeyCode.SPACE);
        verify(pioneersService).move("02", "build", null, null, null, null, null, null, null);
    }

    @Test
    void loadValidPositions() {
        List<String> pos = randomAction.getAllValidPositions();
        Assertions.assertEquals(pos.size(), 24);
    }

    @Test
    void possibleRoadPlacements() {
        List<String> possiblePlacesTop = randomAction.getPossibleRoadPlacements(0, 0, 0, 0);
        Assertions.assertEquals(possiblePlacesTop.size(), 3);
        List<String> possiblePlacesBottom = randomAction.getPossibleRoadPlacements(0, 0, 0, 6);
        Assertions.assertEquals(possiblePlacesBottom.size(), 3);
    }

    @Test
    void loadInvalidPositions() {
        when(pioneersService.findAllBuildings(any())).thenReturn(Observable.just(List.of(
                new Building(0, 0, 0, "id", 0, "settlement", "123", "321"),
                new Building(0, 0, 0, "id", 6, "settlement", "123", "321"))));

        List<String> invalidPos = randomAction.getAllInvalidSettlementCoordinates();
        Assertions.assertEquals(invalidPos.size(), 8);
    }

    @Test
    void eventListenerTest() {

        ExpectedMove ex = new ExpectedMove("founding-settlement-1", Collections.singletonList("01"));
        stateSubject.onNext(new Event<>(".updated", new State("0", "02", Collections.singletonList(ex), null)));
        moveSubject.onNext(new Event<>(".created", new Move("0", "1", "02", "01", "roll", 8, null, null, null, null, null)));
        moveSubject.onNext(new Event<>(".created", new Move("0", "1", "02", "01", "build", 8, null, null, null, null, null)));

        playerSubject.onNext(new Event<>(".updated", new Player("02", "01", "ffff00", true, 3, null, null, 2, 2, null, null)));
        waitForFxEvents();

        stateSubject.onNext(new Event<>(".updated", new State("0", "02", Collections.singletonList(ex), null)));
        moveSubject.onNext(new Event<>(".created", new Move("0", "1", "02", "01", "rob", 8, null, new RobDto(0, 0, 0, null), null, null, null)));
        waitForFxEvents();
    }

    @Test
    void onClickDevLabel() {
        Pane showCards = lookup("#devCardsPane").query();
        clickOn(showCards);

        Label devCardsAmountLabel = lookup("#devCardsAmountLabel").query();
        org.testfx.assertions.api.Assertions.assertThat(devCardsAmountLabel.getText()).isEqualTo("1");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        gameScreenController = null;
        randomAction = null;
        app = null;
        userService = null;
        pioneersService = null;
        messageService = null;
        memberService = null;
        eventListener = null;
        gameStorage = null;
        idStorage = null;
        stateSubject.onComplete();
        moveSubject.onComplete();
        playerSubject.onComplete();
        buildingSubject.onComplete();
        messageSubject.onComplete();
        memberSubject.onComplete();
    }
}
