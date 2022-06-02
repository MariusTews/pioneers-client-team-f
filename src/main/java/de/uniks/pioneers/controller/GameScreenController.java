package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;

import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.dto.ErrorResponse;
import de.uniks.pioneers.model.ExpectedMove;
import de.uniks.pioneers.model.State;
import de.uniks.pioneers.service.*;

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

public class GameScreenController implements Controller {

    @FXML
    public Pane mapPane;
    @FXML
    public Pane chatPane;
    @FXML
    public Label diceSumLabel;

    private final App app;
    private final GameIDStorage gameIDStorage;
    private final PioneersService pioneersService;
    private final EventListener eventListener;
    private final MemberIDStorage memberIDStorage;
    private final UserService userService;
    private final MessageService messageService;
    private final MemberService memberService;

    private GameFieldSubController gameFieldSubController;
    private MessageViewSubController messageViewSubController;

    @Inject
    public GameScreenController(App app,
                                GameIDStorage gameIDStorage,
                                PioneersService pioneersService,
                                EventListener eventListener,
                                MemberIDStorage memberIDStorage,
                                UserService userService,
                                MessageService messageService,
                                MemberService memberService) {
        this.app = app;
        this.gameIDStorage = gameIDStorage;
        this.pioneersService = pioneersService;
        this.eventListener = eventListener;
        this.userService = userService;
        this.messageService = messageService;
        this.memberIDStorage = memberIDStorage;
        this.memberService = memberService;
    }

    @Override
    public void init() {
        // Initialize sub controller for ingame chat, add listener and load all messages
        this.messageViewSubController = new MessageViewSubController(eventListener, gameIDStorage,
                userService, messageService, memberIDStorage, memberService);
        messageViewSubController.init();
    }

    @Override
    public void destroy() {
        if (this.messageViewSubController != null) {
            this.messageViewSubController.destroy();
        }
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

        return parent;
    }

    public void onMouseClicked(MouseEvent mouseEvent) {
        State state = pioneersService.findOneState(gameIDStorage.getId()).blockingFirst();
        List<ExpectedMove> expectedMove = state.expectedMoves();
        ExpectedMove currentExpectedMove = expectedMove.get(0);
        String action = currentExpectedMove.action();
        List<String> players = currentExpectedMove.players();

        if (action.endsWith("roll")) {
            pioneersService.move(gameIDStorage.getId(), action, null, null, null, null, null)
                    .observeOn(FX_SCHEDULER)
                    .subscribe(result -> {
                        diceSumLabel.setText(Integer.toString(result.roll()));
                    }, onError -> {
                        onError.printStackTrace();
                    });
        }
    }
}
