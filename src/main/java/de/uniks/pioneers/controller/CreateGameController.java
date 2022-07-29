package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.Objects;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class CreateGameController implements Controller {
    private final Provider<GameLobbyController> gameLobbyController;
    private final Provider<LobbyController> lobbyController;
    private final Provider<MapTemplatesScreenController> mapTemplatesScreenController;
    private final GameService gameService;

    private String mapTemplateName;
    private String mapTemplateId;
    private int mapSize;
    private int victoryPoints;

    @FXML
    public TextField gameNameTextField;
    @FXML
    public Button backToLobbyButton;
    @FXML
    public Button createGameButton;
    @FXML
    public PasswordField passwordTextField;
    @FXML
    public Button mapSizeMinusButton;
    @FXML
    public Button mapSizePlusButton;
    @FXML
    public Button victoryPointsMinusButton;
    @FXML
    public Button victoryPointsPlusButton;
    @FXML
    public Button mapsButton;
    @FXML
    public Label mapSizeLabel;
    @FXML
    public Label victoryPointsLabel;
    @FXML
    public Label mapTemplateLabel;

    private final App app;

    @Inject
    public CreateGameController(App app,
                                GameService gameService,
                                Provider<LobbyController> lobbyController,
                                Provider<GameLobbyController> gameLobbyController,
                                Provider<MapTemplatesScreenController> mapTemplatesScreenController) {
        this.app = app;
        this.gameService = gameService;
        this.gameLobbyController = gameLobbyController;
        this.lobbyController = lobbyController;
        this.mapTemplatesScreenController = mapTemplatesScreenController;
    }

    @Override
    public void init() {
        this.mapSize = 2;
        this.victoryPoints = 10;
        if (mapTemplateName == null) {
            mapTemplateName = "Default";
        }
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
        mapSizeLabel.setText("" + mapSize);
        victoryPointsLabel.setText("" + victoryPoints);
        mapTemplateLabel.setText("Map Template: " + mapTemplateName);

        return parent;
    }

    public void backToLobbyButtonPressed() {
        final LobbyController controller = lobbyController.get();
        this.app.show(controller);
    }

    public void mapsButtonPressed() {
        final MapTemplatesScreenController controller = mapTemplatesScreenController.get();
        this.app.show(controller);
    }

    public void createGameButtonPressed() {
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
            gameService.create(gameNameTextField.getText(), passwordTextField.getText(), mapSize, victoryPoints, mapTemplateId, false, 0)
                    .observeOn(FX_SCHEDULER)
                    .subscribe(onSuccess -> app.show(gameLobbyController.get()), onError -> {
                    });
        }
    }

    public void mapSizeMinusButtonPressed() {
        //reduces mapSize by One if - Button is Pressed
        if (mapSize > 0) {
            this.mapSizeLabel.setText("" + (--mapSize));
        }
        //disables and hides - Button at Minimum
        if (mapSize == 0) {
            this.mapSizeMinusButton.disableProperty().set(true);
            this.mapSizeMinusButton.setVisible(false);
        }
        //makes + Button visible and clickable if - Button is Pressed
        if (this.mapSizePlusButton.disableProperty().get()) {
            this.mapSizePlusButton.disableProperty().set(false);
            this.mapSizePlusButton.setVisible(true);
        }
    }

    public void mapSizePlusButtonPressed() {
        //increases mapSize by One if + Button is Pressed
        if (mapSize < 10) {
            this.mapSizeLabel.setText("" + (++mapSize));
        }
        //disables and hides + Button at Maximum
        if (mapSize == 10) {
            this.mapSizePlusButton.disableProperty().set(true);
            this.mapSizePlusButton.setVisible(false);
        }
        //makes - Button visible and clickable if + Button is Pressed
        if (this.mapSizeMinusButton.disableProperty().get()) {
            this.mapSizeMinusButton.disableProperty().set(false);
            this.mapSizeMinusButton.setVisible(true);
        }
    }

    public void victoryPointsMinusButtonPressed() {
        //reduces victoryPoints by One if - Button is Pressed
        if (victoryPoints > 3) {
            this.victoryPointsLabel.setText("" + (--victoryPoints));
        }
        //disables and hides - Button at Minimum
        if (victoryPoints == 3) {
            this.victoryPointsMinusButton.disableProperty().set(true);
            this.victoryPointsMinusButton.setVisible(false);
        }
        //makes + Button visible and clickable if - Button is Pressed
        if (this.victoryPointsPlusButton.disableProperty().get()) {
            this.victoryPointsPlusButton.disableProperty().set(false);
            this.victoryPointsPlusButton.setVisible(true);
        }
    }

    public void victoryPointsPlusButtonPressed() {
        //increases victoryPoints by One if + Button is Pressed
        if (victoryPoints < 15) {
            this.victoryPointsLabel.setText("" + (++victoryPoints));
        }
        //disables and hides + Button at Maximum
        if (victoryPoints == 15) {
            this.victoryPointsPlusButton.disableProperty().set(true);
            this.victoryPointsPlusButton.setVisible(false);
        }
        //makes - Button visible and clickable if + Button is Pressed
        if (this.victoryPointsMinusButton.disableProperty().get()) {
            this.victoryPointsMinusButton.disableProperty().set(false);
            this.victoryPointsMinusButton.setVisible(true);
        }
    }

    public void setMapTemplate(String mapTemplateName, String mapTemplateId) {
        this.mapTemplateName = mapTemplateName;
        this.mapTemplateId = mapTemplateId;
    }
}
