package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;

import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.service.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import java.io.IOException;

public class GameScreenController implements Controller {

    @FXML
    public Pane mapPane;
    @FXML
    public VBox chatView;

    private App app;
    private GameIDStorage gameIDStorage;
    private PioneersService pioneersService;
    private final EventListener eventListener;
    private final MemberIDStorage memberIDStorage;
    private final UserService userService;
    private final MessageService messageService;

    private GameFieldSubController gameFieldSubController;
    private MessageViewSubController messageViewSubController;

    @Inject
    public GameScreenController(App app,
                                GameIDStorage gameIDStorage,
                                PioneersService pioneersService,
                                EventListener eventListener,
                                MemberIDStorage memberIDStorage,
                                UserService userService,
                                MessageService messageService){
        this.app = app;
        this.gameIDStorage = gameIDStorage;
        this.pioneersService = pioneersService;
        this.eventListener = eventListener;
        this.userService = userService;
        this.messageService = messageService;
        this.memberIDStorage = memberIDStorage;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {
        this.messageViewSubController.destroy();
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

        this.messageViewSubController = new MessageViewSubController(eventListener, gameIDStorage,
                userService, messageService, memberIDStorage);
        messageViewSubController.init();
        chatView.getChildren().setAll(messageViewSubController.render());

        return parent;
    }
}
