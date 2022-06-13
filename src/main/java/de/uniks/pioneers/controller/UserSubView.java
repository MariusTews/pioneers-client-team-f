package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.GameIDStorage;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class UserSubView implements Controller {

    private final CompositeDisposable disposable = new CompositeDisposable();

    private final ObservableList<Player> players = FXCollections.observableArrayList();

    private final ArrayList<User> users = new ArrayList<>();

    private final GameIDStorage gameIDStorage;
    private final IDStorage idStorage;
    private final UserService userService;
    private final GameFieldSubController gameFieldSubController;
    private final EventListener eventListener;
    //private final PioneersService pioneersService;
    private final Player player;
    private final int vicPoints;

    public Label name;
    public Label victoryPoints;
    public Label item1;
    public Label item2;
    public Label item3;
    public Label item4;
    public Label item5;
    public ImageView image1;
    public ImageView image2;
    public ImageView image3;
    public ImageView image4;
    public ImageView image5;
    public Button sett;
    public Button road;
    public Button city;
    private Parent parent;

    @Inject
    public UserSubView(GameIDStorage gameIDStorage, IDStorage idStorage, UserService userService, EventListener eventListener, Player player, int victoryPoints, GameFieldSubController gameFieldSubController) {

        this.gameIDStorage = gameIDStorage;
        this.idStorage = idStorage;
        this.userService = userService;
        this.eventListener = eventListener;
        this.player = player;
        this.vicPoints = victoryPoints;
        this.gameFieldSubController = gameFieldSubController;
    }


    @Override
    public void init() {
        userService.findAllUsers().observeOn(FX_SCHEDULER)
                .subscribe(col -> {
                    for (User user : col) {
                        this.users.add(user);
                    }
                    attachTOSubview();
                });

    }

    private void attachTOSubview() {
        for (User user : this.users) {
            if (player.userId().equals(this.idStorage.getID()) && user._id().equals(this.idStorage.getID())) {
                this.attachName(user.name(), player.color());
                this.attachResources(player.resources());
                this.victoryPoints.setText(Integer.toString(vicPoints) + "/10");
                //TODO:Builidings needs to be calculated
            }
        }
    }

    //resources are taken out of Hashmap
    //attach to the label
    private void attachResources(HashMap<String, Integer> resources) {
        for (String value: RESOURCES) {
            switch (value){
                case "lumber":
                    if(!resources.containsKey(value)) {
                        item1.setText("0");
                    }
                    break;
                case "brick":
                    if(!resources.containsKey(value)) {
                        item2.setText("0");
                    }
                    break;
                case "ore":
                    if(!resources.containsKey(value)) {
                        item3.setText("0");
                    }
                    break;
                case "wool":
                    if(!resources.containsKey(value)) {
                        item4.setText("0");
                    }
                    break;
                case "grain":
                    if(!resources.containsKey(value)) {
                        item5.setText("0");
                    }
                    break;
                default:
                    break;
            }
        }
        for (Map.Entry<String,Integer> set: resources.entrySet()) {
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

    //name is set to namelabel and color aswell
    //and attach picture
    private void attachName(String n, String color) {
        name.setText(n + " (YOU)");
        name.setTextFill(Color.web(color));
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
        this.parent = parent;
        this.road.disableProperty().set(true);
        this.sett.disableProperty().set(true);
        this.city.disableProperty().set(true);
        return parent;
    }

    public Parent getParent() {
        return parent;

    }

    public void onSett(ActionEvent actionEvent) {
        gameFieldSubController.build("settlement");
    }

    public void onRoad(ActionEvent actionEvent) {
        gameFieldSubController.build("road");
    }

    public void onCity(ActionEvent actionEvent) {
        gameFieldSubController.build("city");
    }
}
