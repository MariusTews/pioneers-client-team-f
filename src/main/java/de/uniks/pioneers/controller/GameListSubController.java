package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Game;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import javax.inject.Inject;
import java.io.IOException;

public class GameListSubController implements Controller {
    private String id;
    @FXML
    public Label gameNameLabel;
    @FXML
    public Button joinButton;
    private final Game game;
    private final LobbyController lobbyController;

    private Parent parent;

    @Inject
    public GameListSubController(Game game, LobbyController lobbyController) {
        this.game = game;
        this.lobbyController = lobbyController;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/GameListSubView.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (game != null) {
            this.gameNameLabel.setText(this.game.name() );//+ " (" + game.members() + "/" + MAX_MEMBERS + ")");
            this.id = game._id();

            if (game.started()) { //|| (int) game.members() == MAX_MEMBERS) {
                HBox box = (HBox) this.joinButton.getParent();
                box.getChildren().removeIf(node -> node.equals(joinButton));
            }
        }

        this.parent = parent;
        return parent;
    }

    public void joinButtonPressed() {
        //if ((int) game.members() < MAX_MEMBERS) {
            this.lobbyController.joinGame(this.game);
        //}
    }

    public String getId() {
        return id;
    }

    public Parent getParent() {
        return parent;
    }
}

