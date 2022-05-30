package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import java.io.IOException;

public class GameScreenController implements Controller {

    private GameFieldSubController gameFieldSubController;
    private MessageViewSubController messageViewSubController;

    @FXML
    public Pane mapPane;
    @FXML
    public VBox chatView;

    @Inject
    public GameScreenController() {
    }


    @Override
    public void init() {

    }

    @Override
    public void destroy() {

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

        this.gameFieldSubController = new GameFieldSubController();
        mapPane.getChildren().setAll(gameFieldSubController.render());

        // Include ingame chat
        this.messageViewSubController = new MessageViewSubController();
        chatView.getChildren().setAll(messageViewSubController.render());

        return parent;
    }
}
