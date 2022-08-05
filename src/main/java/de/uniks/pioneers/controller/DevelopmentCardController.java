package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.DevelopmentCard;
import de.uniks.pioneers.model.ExpectedMove;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.GameStorage;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.PioneersService;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import static de.uniks.pioneers.Constants.*;

public class DevelopmentCardController implements Controller {

    public HBox mainHBoxId;
    public Pane paneForLumberId;
    public VBox vBoxForLumberId;
    public HBox hBoxForLumberId;
    public Label labelForLumberId;
    public ImageView lumberImageId;
    public ImageView roadImageId;

    public ImageView rocketImageId;
    public ImageView threeUsersImageId;
    public ImageView rockImageId;
    public ImageView arrowImageId;
    public Label yearOfPlentyCard;
    public Label roadBuildingCard;
    public Label knightCard;
    public Label monoPolyCard;
    private Stage primaryStage;

    private final GameStorage gameStorage;

    private final IDStorage idStorage;
    private final PioneersService pioneersService;

    private final GameScreenController gameScreenController;
    private final Window owner;
    private final ExpectedMove nextMove;
    private final HashMap<String, User> userHash;
    private final Player player;

    @Inject
    public DevelopmentCardController(GameScreenController gameScreenController, Window owner, GameStorage gameStorage, IDStorage idStorage, PioneersService pioneersService, ExpectedMove nextMove, HashMap<String, User> userHash, Player player) {
        this.gameScreenController = gameScreenController;
        this.owner = owner;
        this.gameStorage = gameStorage;
        this.idStorage = idStorage;
        this.pioneersService = pioneersService;
        this.nextMove = nextMove;
        this.userHash = userHash;
        this.player = player;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/Development.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent root;
        try {
            root = loader.load();
            this.primaryStage = new Stage();
            // Set to UNDECORATED or TRANSPARENT (without white background) to remove minimize, maximize and close button of stage
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("view/stylesheets/DevelopmentCardStyle.css")).toString());
            primaryStage.setScene(scene);
            primaryStage.setTitle("");
            // Specify modality of the new window: interactions are only possible on the second window
            primaryStage.initModality(Modality.WINDOW_MODAL);
            primaryStage.initOwner(this.owner);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        calculateAllOwnedCards();

        lumberImageId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/earth_cactus.png"))));
        roadImageId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/roadDev.png"))));
        rocketImageId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/rocket.png"))));
        arrowImageId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/rocket.png"))));
        threeUsersImageId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/threeUserIcon.png"))));
        arrowImageId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/arrow.png"))));
        rockImageId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/moon_rock.png"))));

        return root;

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void calculateAllOwnedCards() {
        pioneersService.findOnePlayer(this.gameStorage.getId(), this.idStorage.getID()).observeOn(FX_SCHEDULER).subscribe(e -> {
            int year_of_plenty = 0;
            int road_building = 0;
            int knight = 0;
            int monopoly = 0;
            for (DevelopmentCard card : e.developmentCards()) {
                if (card.type().equals(KNIGHT)) {
                    knight++;
                }
                if (card.type().equals(YEAR_OF_PLENTY)) {
                    year_of_plenty++;
                }
                if (card.type().equals(ROAD_BUILDING)) {
                    road_building++;
                }
                if (card.type().equals(MONOPOLY)) {
                    monopoly++;
                }

            }
            yearOfPlentyCard.setText(String.valueOf(year_of_plenty));
            roadBuildingCard.setText(String.valueOf(road_building));
            knightCard.setText(String.valueOf(knight));
            monoPolyCard.setText(String.valueOf(monopoly));
        });
    }

    public void onClickCancel() {
        primaryStage.close();
    }

    public void onYearOfPlentyClick() {
        if (!gameScreenController.currentPlayerLabel.getText().equals(userHash.get(idStorage.getID()).name()) ||
                !nextMove.action().equals("build") || yearOfPlentyCard.getText().equals("0")) {
            showAlert();
        } else {
            pioneersService
                    .playDevCard(gameStorage.getId(), "year-of-plenty")
                    .observeOn(FX_SCHEDULER)
                    .doOnError(error -> AlertSameRound())
                    .subscribe(onSuccess -> {
                        primaryStage.close();
                        YearOfPlentyController yearOfPlentyController = new YearOfPlentyController(player, gameStorage.getId(), pioneersService, owner);
                        yearOfPlentyController.render();
                    });
        }
    }

    public void onRoadBuildingClick() {
        if (!gameScreenController.currentPlayerLabel.getText().equals(userHash.get(idStorage.getID()).name()) ||
                !nextMove.action().equals("build") || roadBuildingCard.getText().equals("0")) {
            showAlert();
        } else {
            pioneersService
                    .playDevCard(gameStorage.getId(), "road-building")
                    .observeOn(FX_SCHEDULER)
                    .doOnError(error -> AlertSameRound())
                    .subscribe(onSuccess -> {
                        primaryStage.close();
                    });

        }
    }

    public void onKnightClick() {
        if (!gameScreenController.currentPlayerLabel.getText().equals(userHash.get(idStorage.getID()).name()) ||
                !nextMove.action().equals("build") || knightCard.getText().equals("0")) {
            showAlert();
        } else {
            pioneersService
                    .playDevCard(gameStorage.getId(), "knight")
                    .observeOn(FX_SCHEDULER)
                    .doOnError(error -> AlertSameRound())
                    .subscribe(onSuccess -> {
                        primaryStage.close();
                    });
        }
    }

    public void onMonopolyClick() {
        //if it's not your turn
        if (!gameScreenController.currentPlayerLabel.getText().equals(userHash.get(idStorage.getID()).name()) ||
                !nextMove.action().equals("build") || monoPolyCard.getText().equals("0")) {
            showAlert();
        } else {
            pioneersService
                    .playDevCard(gameStorage.getId(), "monopoly")
                    .observeOn(FX_SCHEDULER)
                    .doOnError(error -> AlertSameRound())
                    .subscribe(onSuccess -> {
                        primaryStage.close();
                        String[] types = {"VENUS_GRAIN", "MARS_BAR", "MOON_ROCK", "EARTH_CACTUS", "NEPTUNE_CRYSTAL"};
                        ChoiceDialog<String> choosingResource = new ChoiceDialog<>(types[1], types);
                        choosingResource.getDialogPane().lookupButton(ButtonType.CANCEL).setVisible(false);
                        // set stylesheet
                        choosingResource.getDialogPane().getStylesheets().add(Objects.requireNonNull(Main.class
                                .getResource("view/stylesheets/ChoiceDialogRob.css")).toExternalForm());
                        choosingResource.setHeaderText("Rob resource");
                        // remove close/maximize/minimize button
                        choosingResource.initStyle(StageStyle.UNDECORATED);
                        // Get the chosen target and make a server request with this target
                        choosingResource.showAndWait();
                        String resource = choosingResource.getSelectedItem();
                        HashMap<String, String> ResourceMap = new HashMap<>() {{
                            put("VENUS_GRAIN", VENUS_GRAIN);
                            put("MARS_BAR", MARS_BAR);
                            put("MOON_ROCK", MOON_ROCK);
                            put("EARTH_CACTUS", EARTH_CACTUS);
                            put("NEPTUNE_CRYSTAL", NEPTUNE_CRYSTAL);
                        }};
                        String selectedResource = ResourceMap.get(resource);
                        HashMap<String, Integer> resourcesMap = new HashMap<>() {{
                            put(selectedResource, 0);
                        }};

                        pioneersService
                                .monopolyCard(gameStorage.getId(), resourcesMap)
                                .observeOn(FX_SCHEDULER)
                                .subscribe();
                    });
        }
    }

    public void showAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Playing not possible!");
        // set style
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
                .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
        alert.showAndWait();
    }

    private void AlertSameRound() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "You just bought the card this round!");
        // Set style
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
                .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
        alert.showAndWait();
    }

}


