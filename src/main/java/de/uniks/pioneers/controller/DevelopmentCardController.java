package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.DevelopmentCard;
import de.uniks.pioneers.service.GameStorage;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.PioneersService;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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

    private final Window owner;

    @Inject
    public DevelopmentCardController(Window owner, GameStorage gameStorage, IDStorage idStorage, PioneersService pioneersService) {
        this.owner = owner;
        this.gameStorage = gameStorage;
        this.idStorage = idStorage;
        this.pioneersService = pioneersService;
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

    public void onYearOfPlentyClick(ActionEvent event) {
        //TODO:implement action when year of plenty card is clicked
    }

    public void onRoadBuildingClick(ActionEvent event) {
        //TODO:implement action when roadBuilding card is played
    }

    public void onKnightClick(ActionEvent event) {
        //TODO :implement action Knight card is played
    }

    public void onMonopolyClick(ActionEvent event) {
        //TODO : implement action when Monopoly card is played
    }
}


