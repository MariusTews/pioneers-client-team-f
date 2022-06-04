package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;

import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.model.ExpectedMove;
import de.uniks.pioneers.model.Move;
import de.uniks.pioneers.model.State;
import de.uniks.pioneers.service.*;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class GameScreenController implements Controller {

    @FXML
    public Pane mapPane;
    @FXML
    public Pane chatPane;
    @FXML
    public Label diceSumLabel;

    private final App app;

    private final GameIDStorage gameIDStorage;

    private final IDStorage idStorage;

    private final PioneersService pioneersService;
    private final EventListener eventListener;
    private final MemberIDStorage memberIDStorage;
    private final UserService userService;
    private final MessageService messageService;
    private final MemberService memberService;
    public Pane userPaneId;

    private GameFieldSubController gameFieldSubController;
    private MessageViewSubController messageViewSubController;
    private final CompositeDisposable disposable = new CompositeDisposable();



    private UserSubView userSubView;

    @Inject
    public GameScreenController(App app,
                                GameIDStorage gameIDStorage,
                                IDStorage idStorage,
                                PioneersService pioneersService,
                                EventListener eventListener,
                                MemberIDStorage memberIDStorage,
                                UserService userService,
                                MessageService messageService,
                                MemberService memberService) {
        this.app = app;
        this.gameIDStorage = gameIDStorage;
        this.idStorage = idStorage;
        this.pioneersService = pioneersService;
        this.eventListener = eventListener;
        this.userService = userService;
        this.messageService = messageService;
        this.memberIDStorage = memberIDStorage;
        this.memberService = memberService;
    }

    @Override
    public void init() {

        disposable.add(eventListener
                .listen("games." + this.gameIDStorage.getId() + ".moves.*." + "created", Move.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    Move move = event.data();
                    if (move.action().endsWith("roll")) {
                        diceSumLabel.setText(Integer.toString(move.roll()));
                    }
                }));


        //event Lister for Resources

        // Initialize sub controller for ingame chat, add listener and load all messages
        this.messageViewSubController = new MessageViewSubController(eventListener, gameIDStorage,
                userService, messageService, memberIDStorage, memberService);
        messageViewSubController.init();

        this.userSubView = new UserSubView(gameIDStorage,userService,idStorage,pioneersService);
        userSubView.init();
    }


    @Override
    public void destroy() {
        if (this.messageViewSubController != null) {
            this.messageViewSubController.destroy();
        }
        disposable.dispose();
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/GameScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        this.gameFieldSubController = new GameFieldSubController(app, gameIDStorage, pioneersService);
        mapPane.getChildren().setAll(gameFieldSubController.render());

        // Show chat and load the messages
        chatPane.getChildren().setAll(messageViewSubController.render());

        userPaneId.getChildren().setAll(userSubView.render());

        return parent;
    }

    public void onMouseClicked(MouseEvent mouseEvent) {
        diceRoll();
    }

    public void diceRoll() {
        State state = pioneersService.findOneState(gameIDStorage.getId()).blockingFirst();
        List<ExpectedMove> expectedMove = state.expectedMoves();
        ExpectedMove currentExpectedMove = expectedMove.get(0);
        String action = currentExpectedMove.action();

        if (action.endsWith("roll")) {
            pioneersService.move(gameIDStorage.getId(), action, 0, 0, 0, 0, "settlement")
                    .observeOn(FX_SCHEDULER)
                    .subscribe(result -> {
                    }, Throwable::printStackTrace);
        }
    }
}
