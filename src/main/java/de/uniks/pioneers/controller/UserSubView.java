package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.service.GameIDStorage;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.PioneersService;
import de.uniks.pioneers.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class UserSubView implements Controller {

    private final GameIDStorage gameIDStorage;
    private final UserService userService;
    private final IDStorage idStorage;
    private final PioneersService pioneersService;
    public Label name;
    public Label victoryPoints;
    public Label item1;
    public Label item2;
    public Label item3;
    public Label item4;
    public Label item5;

    @Inject
    public UserSubView(GameIDStorage gameIDStorage, UserService userService, IDStorage idStorage, PioneersService pioneersService) {

        this.gameIDStorage = gameIDStorage;
        this.userService = userService;
        this.idStorage = idStorage;
        this.pioneersService = pioneersService;
    }

    @Override
    public void init() {
        pioneersService.findOnePlayer(gameIDStorage.getId(),idStorage.getID())
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                    this.attachTOSubview(result.userId(),result.color(),result.resources(),result.remainingBuildings());
                    //VictoryPoins needs to be handled
                });
    }

    private void attachTOSubview(String userId, String color, HashMap<String, Integer> resources, HashMap<String, Integer> remainingBuildings) {
        userService.findOne(userId).observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                    this.attachName(result.name(),color);
                    this.attachResources(resources);
                    //TODO:Builidings needs to be calculated
                });
    }

    //resources are taken out of Hashmap
    //attach to the label
    private void attachResources(HashMap<String, Integer> resources) {
        if (resources.isEmpty()){
            item1.setText("0");
            item2.setText("0");
            item3.setText("0");
            item4.setText("0");
            item5.setText("0");
        } else {
            int i = 1;
            for (Integer object:resources.values()) {
                if (i == 1) {
                    //TODO :unknown
                    //this needs to be handled as it is named unknown
                } else if(i == 2) {
                    item1.setText(object.toString());
                    i++;
                } else if(i == 3){
                    item2.setText(object.toString());
                    i++;
                } else if(i==4){
                    item3.setText(object.toString());
                    i++;
                } else if (i==5){
                    item4.setText(object.toString());
                    i++;
                } else {
                    item5.setText(object.toString());
                }
            }
        }
    }

    //name is set to namelabel and color aswell
    private void attachName(String n, String color) {
        name.setText(n);
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

        return parent;
    }

    public void onSett(ActionEvent actionEvent) {
    }

    public void onRoad(ActionEvent actionEvent) {
    }

    public void onCity(ActionEvent actionEvent) {
    }
}
