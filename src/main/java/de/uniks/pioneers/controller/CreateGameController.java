package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.service.GameService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.Objects;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class CreateGameController implements Controller {
    private final Provider<GameLobbyController> gameLobbyController;
    private final Provider<LobbyController> lobbyController;
    private final GameService gameService;
    @FXML
    public TextField gameNameTextField;

    @FXML
    public Button backToLobbyButton;
    @FXML
    public Button createGameButton;
    @FXML
    public PasswordField passwordTextField;
    private final App app;

    @Inject
    public CreateGameController(App app,
                                GameService gameService,
                                Provider<LobbyController> lobbyController,
                                Provider<GameLobbyController> gameLobbyController) {
        this.app = app;
        this.gameLobbyController = gameLobbyController;
        this.lobbyController = lobbyController;
        this.gameService = gameService;
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
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return parent;
    }

    public void backToLobbyButtonPressed(ActionEvent event) {
        final LobbyController controller = lobbyController.get();
        this.app.show(controller);
    }

    public void createGameButtonPressed(ActionEvent event) {

        if (gameNameTextField.getText().length() > 32 || gameNameTextField.getText().length() < 1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "the name of the game must be \nbetween 1 and 32 characters!");
            // set style of information
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
            alert.showAndWait();
        } else if (passwordTextField.getText().length() < 1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "password can't be empty!");
            // set style of information
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
            alert.showAndWait();
        } else {
            gameService.create(gameNameTextField.getText(), passwordTextField.getText())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(onSuccess -> app.show(gameLobbyController.get()), onError -> {
                    });
        }
    }
}
