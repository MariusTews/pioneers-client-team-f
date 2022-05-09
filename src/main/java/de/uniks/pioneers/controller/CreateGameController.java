package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.service.GameService;
import io.reactivex.rxjava3.core.Observable;
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

        Observable<Game> gameObservable = gameService.create(gameNameTextField.getText(), passwordTextField.getText());

        gameObservable.observeOn(FX_SCHEDULER)
                .subscribe(result -> app.show(gameLobbyController.get()));
    }
}
