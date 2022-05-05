package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

public class RulesScreenController implements Controller {
    @FXML
    public Button backButton;
    private App app;
    private Provider<LobbyController> lobbyController;

    @Inject
    public RulesScreenController(App app,
                                 Provider<LobbyController> lobbyController) {
        this.app = app;
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
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/RulesScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return parent;
    }

    public void backButtonPressed(ActionEvent actionEvent) {
        final LobbyController controller = lobbyController.get();
        app.show(controller);
    }
}
