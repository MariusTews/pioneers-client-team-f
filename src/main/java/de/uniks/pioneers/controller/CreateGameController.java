package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

public class CreateGameController implements Controller{
    private final Provider<GameLobbyController> gameLobbyController;
    private final Provider<LobbyController> lobbyController;
    @FXML public TextField gameNameTextField;

    @FXML public Button BackToLobbyButton;
    @FXML public Button createGameButton;
    @FXML public PasswordField passwordTextField;
    private final App app;

    @Inject
    public CreateGameController(App app,

                                Provider<LobbyController> lobbyController,
                                Provider<GameLobbyController> gameLobbyController) {
        this.app = app;
        this.gameLobbyController = gameLobbyController;
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
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/CreateGame.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try{
            parent = loader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return parent;
    }

    public void BackToLobbyButtonPressed(ActionEvent actionEvent) {
        final LobbyController controller = lobbyController.get();
        this.app.show(controller);
    }

    public void createGameButtonPressed(ActionEvent actionEvent) {
        final GameLobbyController controller = gameLobbyController.get();
        this.app.show(controller);
    }
}
