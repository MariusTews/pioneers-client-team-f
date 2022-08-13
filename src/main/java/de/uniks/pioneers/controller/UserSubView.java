package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.DevelopmentCard;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static de.uniks.pioneers.Constants.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class UserSubView implements Controller {

    private final ArrayList<User> users = new ArrayList<>();
    private final IDStorage idStorage;
    private final GameStorage gameStorage;
    private final UserService userService;
    private final GameFieldSubController gameFieldSubController;
    private final Player player;
    public Label name;
    public Label victoryPoints;
    public Label item1;
    public Label item2;
    public Label item3;
    public Label item4;
    public Label item5;
    public Button sett;
    public Button road;
    public Button city;
    public final int maxVictoryPoints;
    private final PioneersService pioneersService;
    @FXML
    public ImageView largestFleetIconDisplay;
    @FXML
    public Label fleetLabel;
    @FXML
    public ImageView longestRoadIconDisplay;
    @FXML
    public Button developmentBuyIdButton;


    @Inject
    public UserSubView(IDStorage idStorage, GameStorage gameStorage, UserService userService, Player player, GameFieldSubController gameFieldSubController,
                       int maxVictoryPoints, PioneersService pioneersService) {
        this.idStorage = idStorage;
        this.gameStorage = gameStorage;
        this.userService = userService;
        this.player = player;
        this.gameFieldSubController = gameFieldSubController;
        this.maxVictoryPoints = maxVictoryPoints;
        this.pioneersService = pioneersService;
    }

    @Override
    public void init() {
        //ld = new RoadAndFleet();
        userService.findAllUsers().observeOn(FX_SCHEDULER)
                .subscribe(col -> {
                    this.users.addAll(col);
                    attachTOSubview();
                });
    }

    private void attachTOSubview() {
        for (User user : this.users) {
            if (player.userId().equals(this.idStorage.getID()) && user._id().equals(this.idStorage.getID())) {
                this.attachName(user.name(), player.color());
                this.attachResources(player.resources());
                this.victoryPoints.setText(player.victoryPoints() + "/" + maxVictoryPoints);
                if (player.developmentCards() != null) {
                    int knight = 0;
                    for (DevelopmentCard dc : player.developmentCards()) {
                        if (dc.type().equals(KNIGHT) && dc.revealed()) {
                            knight += 1;
                        }
                    }
                    this.fleetLabel.setText(":" + knight);
                }

                if (player.hasLargestArmy()) {
                    this.largestFleetIconDisplay.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/largestFleetIcon.png"))));
                }
                if (player.hasLongestRoad()) {
                    this.longestRoadIconDisplay.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/longestRoadIcon.png"))));
                }
            }
        }
    }

    //resources are taken out of Hashmap
    //attach to the label
    private void attachResources(HashMap<String, Integer> resources) {
        for (String value : RESOURCES) {
            switch (value) {
                case "lumber":
                    if (!resources.containsKey(value)) {
                        item1.setText("0");
                    }
                    break;
                case "brick":
                    if (!resources.containsKey(value)) {
                        item2.setText("0");
                    }
                    break;
                case "ore":
                    if (!resources.containsKey(value)) {
                        item3.setText("0");
                    }
                    break;
                case "wool":
                    if (!resources.containsKey(value)) {
                        item4.setText("0");
                    }
                    break;
                case "grain":
                    if (!resources.containsKey(value)) {
                        item5.setText("0");
                    }
                    break;
                default:
                    break;
            }
        }
        for (Map.Entry<String, Integer> set : resources.entrySet()) {
            switch (set.getKey()) {
                case "lumber" -> item1.setText(String.valueOf(set.getValue()));
                case "brick" -> item2.setText(String.valueOf(set.getValue()));
                case "ore" -> item3.setText(String.valueOf(set.getValue()));
                case "wool" -> item4.setText(String.valueOf(set.getValue()));
                case "grain" -> item5.setText(String.valueOf(set.getValue()));
                default -> {
                }
            }
        }
        updateButtons(item1.getText(), item2.getText(), item3.getText(), item4.getText(), item5.getText());
    }

    private void updateButtons(String lumber, String brick, String ore, String wool, String grain) {
        if (Integer.parseInt(lumber) > 0 && Integer.parseInt(brick) > 0) {
            road.disableProperty().set(false);
        }

        if (Integer.parseInt(lumber) > 0 && Integer.parseInt(brick) > 0 && Integer.parseInt(wool) > 0 && Integer.parseInt(grain) > 0) {
            sett.disableProperty().set(false);
        }

        if (Integer.parseInt(ore) > 2 && Integer.parseInt(grain) > 1) {
            city.disableProperty().set(false);
        }

        //enable if grain, wool, and ore are present
        if (Integer.parseInt(ore) > 0 && Integer.parseInt(grain) > 0 && Integer.parseInt(wool) > 0) {
            developmentBuyIdButton.disableProperty().set(false);
        }
    }

    //name is set to nameLabel and color as well
    //and attach picture
    private void attachName(String n, String color) {
        this.name.setText(n + " (YOU)");
        this.name.setTextFill(Color.web(color));
    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/UserSubView.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        // Disable buttons, because resource are not available yet and rename buttons
        this.road.disableProperty().set(true);
        this.road.setText(RENAME_ROAD);
        this.sett.disableProperty().set(true);
        this.sett.setText(RENAME_SETTLEMENT);
        this.city.disableProperty().set(true);
        this.city.setText(RENAME_CITY);
        this.developmentBuyIdButton.disableProperty().set(true);


        Tooltip.install(this.developmentBuyIdButton, new Tooltip("1 Venus grain, \n1 Moon rock, \n1 Neptune Crystal"));
        Tooltip.install(this.road, new Tooltip("1 Earth cactus, \n1 Mars bar "));
        Tooltip.install(this.sett, new Tooltip("1 Earth cactus, \n1 Mars bar, \n1 Neptune crystals, \n1 Venus grain "));
        Tooltip.install(this.city, new Tooltip("3 Moon rock, \n2 Venus grain "));

        //roadAndFleet();

        return parent;
    }


    public void onSett() {
        gameFieldSubController.build("settlement");
    }

    public void onRoad() {
        gameFieldSubController.build("road");
    }

    public void onCity() {
        gameFieldSubController.build("city");
    }

    public void onDev() {
        AlertService alertService = new AlertService();
        pioneersService.findOneState(this.gameStorage.getId())
                .observeOn(FX_SCHEDULER).subscribe(e -> {
                    if (e.expectedMoves().get(0).action().equals(BUILD) && e.expectedMoves().get(0).players().contains(this.idStorage.getID())) {
                        pioneersService.findOnePlayer(this.gameStorage.getId(), this.idStorage.getID())
                                .observeOn(FX_SCHEDULER).subscribe(p -> pioneersService.move(this.gameStorage.getId(), e.expectedMoves().get(0).action(),
                                                0, 0, 0, 0, NEW, null, null)
                                        .observeOn(FX_SCHEDULER).subscribe(est -> {
                                        }));
                    } else if (e.expectedMoves().get(0).action().equals(BUILD) &&
                            !e.expectedMoves().get(0).players().contains(this.idStorage.getID())) {
                        alertService.showAlert("Not Your Turn");
                    } else {
                        alertService.showAlert("Wrong Action Sequence");
                    }
                });
    }
}
