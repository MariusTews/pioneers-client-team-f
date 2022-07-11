package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.UserService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
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
    private final UserService userService;
    private final GameFieldSubController gameFieldSubController;
    private final Player player;
    private final int vicPoints;

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
    public Pane settlementPane;
    public Pane roadPane;
    public Pane cityPane;

    @Inject
    public UserSubView(IDStorage idStorage, UserService userService, Player player, int victoryPoints, GameFieldSubController gameFieldSubController) {
        this.idStorage = idStorage;
        this.userService = userService;
        this.player = player;
        this.gameFieldSubController = gameFieldSubController;
        this.vicPoints = victoryPoints;
    }

    @Override
    public void init() {
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
                this.victoryPoints.setText(vicPoints + "/10");
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

        Tooltip.install(this.roadPane, new Tooltip("1 Earth cactus, \n1 Mars bar "));
        Tooltip.install(this.settlementPane, new Tooltip("1 Earth cactus, \n1 Mars bar, \n1 Neptun crystals, \n1 Venus grain "));
        Tooltip.install(this.cityPane, new Tooltip("3 Moon rock, \n2 Venus grain "));
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

}
