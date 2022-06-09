package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.GameIDStorage;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.PioneersService;
import de.uniks.pioneers.service.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static de.uniks.pioneers.Constants.*;

public class UserSubView implements Controller {

    private final CompositeDisposable disposable = new CompositeDisposable();

    private final ObservableList<Player> players = FXCollections.observableArrayList();

    private final ArrayList<User> users= new ArrayList<>();

    private final GameIDStorage gameIDStorage;
    private final IDStorage idStorage;
    private final UserService userService;
    private final EventListener eventListener;
    private final PioneersService pioneersService;
    public Label name;
    public Label victoryPoints;
    public Label item1;
    public Label item2;
    public Label item3;
    public Label item4;
    public Label item5;

    @Inject
    public UserSubView(GameIDStorage gameIDStorage, IDStorage idStorage, UserService userService, EventListener eventListener, PioneersService pioneersService) {

        this.gameIDStorage = gameIDStorage;
        this.idStorage = idStorage;
        this.userService = userService;
        this.eventListener = eventListener;
        this.pioneersService = pioneersService;
    }


    @Override
    public void init() {
        userService.findAllUsers().observeOn(FX_SCHEDULER)
                .subscribe( col -> {
                    for (User user: col) {
                        this.users.add(user);
                    }
                    pioneersService.findAllPlayers(this.gameIDStorage.getId()).observeOn(FX_SCHEDULER)
                            .subscribe(result -> {this.players.setAll(result);
                                this.attachTOSubview();
                            });
                });


            disposable.add(eventListener.
                listen("games." + this.gameIDStorage.getId() + ".players.*.*", Player.class)
                    .observeOn(FX_SCHEDULER).
                    subscribe(event -> {
                        Player p = event.data();
                        if(event.event().endsWith(UPDATED)){
                            //this needs to be done
                            this.players.add(p);
                            this.attachTOSubview();
                        }
                    }));
    }

    private void attachTOSubview() {
        for (Player player: this.players) {
            for(User user:this.users) {
                if (player.userId().equals(this.idStorage.getID()) && user._id().equals(this.idStorage.getID())) {
                    System.out.println(user._id());
                    this.attachName(user.name(), player.color());
                    this.attachResources(player.resources());
                    //TODO:Builidings needs to be calculated
                }
            }
        }
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
