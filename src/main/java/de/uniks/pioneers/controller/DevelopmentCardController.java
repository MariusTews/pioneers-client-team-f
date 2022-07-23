package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
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
import java.util.concurrent.atomic.AtomicInteger;

import static de.uniks.pioneers.Constants.*;

public class DevelopmentCardController implements Controller {

    public HBox mainHboxId;
    public Pane paneForLumberId;
    public VBox vBoxForLumberId;
    public HBox hBoxForLumberId;
    public Label labelForLumberId;
    public ImageView lumberImageId;
    //label for first Development card
    public Label currentLumberDevId;
    public ImageView roadImageId;

    //label for roadDevelopment card
    public Label labelForRoadId;
    public ImageView rocketImageId;
    //label for rocketDevelopment card
    public Label rocketLabelDevId;
    public ImageView threeUsersImageId;
    public ImageView rockImageId;
    public ImageView arrowImageId;
    //label for fourth Development card
    public Label threeImagesDevId;
    private Stage primaryStage;

    private final GameStorage gameStorage;

    private final IDStorage idStorage;

    private final App app;
    private PioneersService pioneersService;

    private final Window owner;

    private String allTheDevelopmentCard;

    @Inject
    public DevelopmentCardController(Window owner, GameStorage gameStorage, IDStorage idStorage,
                                     App app, PioneersService pioneersService){
        this.owner = owner;
        this.gameStorage =gameStorage;
        this.idStorage = idStorage;
        this.app = app;
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
            scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("view/stylesheets/Development.css")).toString());
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

        calculateALlOwnedCards();

        lumberImageId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/earth_cactus.png"))));
        roadImageId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/roadDev.png"))));
        rocketImageId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/rocket.png"))));
        arrowImageId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/rocket.png"))));
        threeUsersImageId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/threeUserIcon.png"))));
        arrowImageId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/arrow.png"))));
        rockImageId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/moon_rock.png"))));

        return root;

    }

    private void calculateALlOwnedCards() {
        AtomicInteger year_of_plenty = new AtomicInteger();
        AtomicInteger road_building = new AtomicInteger();
        AtomicInteger knight = new AtomicInteger();
        AtomicInteger monopoly = new AtomicInteger();

        pioneersService.findOnePlayer(this.gameStorage.getId(),this.idStorage.getID())
                .observeOn(FX_SCHEDULER).subscribe(e ->{
                    for (DevelopmentCard card: e.developmentCards()) {
                        if(card.type().equals(KNIGHT)){
                            knight.set(knight.get() + 1);
                        }
                        if(card.type().equals(YEAR_OF_PLENTY)){
                            year_of_plenty.set(year_of_plenty.get()+1);
                        }
                        if(card.type().equals(ROAD_BUILDING)){
                            road_building.set(road_building.get()+1);
                        }
                        if (card.type().equals(MONOPOLY)){
                            monopoly.set(monopoly.get()+1);
                        }
                    }

                    currentLumberDevId.setText(String.valueOf(year_of_plenty.get()));
                    labelForRoadId.setText(String.valueOf(road_building.get()));
                    rocketLabelDevId.setText(String.valueOf(knight.get()));
                    threeImagesDevId.setText(String.valueOf(monopoly.get()));

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


