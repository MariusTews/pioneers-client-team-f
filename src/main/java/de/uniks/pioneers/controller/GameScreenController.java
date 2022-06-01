package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;

import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.Message;
import de.uniks.pioneers.service.GameIDStorage;
import de.uniks.pioneers.service.PioneersService;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import javax.inject.Inject;

import java.io.IOException;

import static de.uniks.pioneers.Constants.CREATED;
import static de.uniks.pioneers.Constants.FX_SCHEDULER;


public class GameScreenController implements Controller {




    @FXML
    public Pane mapPane;
    public Button rollButton;
    public Label diceLabel;
    private App app;
    private GameIDStorage gameIDStorage;
    private PioneersService pioneersService;

    private GameFieldSubController gameFieldSubController;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final EventListener eventListener;


    @Inject
    public GameScreenController(App app,
                                GameIDStorage gameIDStorage,
                                PioneersService pioneersService,
                                EventListener eventListener){
        this.app = app;
        this.gameIDStorage = gameIDStorage;
        this.pioneersService = pioneersService;
        this.eventListener = eventListener;
    }


    @Override
    public void init() {
        disposable.add(eventListener
                .listen("games." + this.gameIDStorage.getId() + ".*.*", Message.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event->{
                    final Message message = event.data();
                    System.out.println(event.event());
                }));

    }




    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/GameScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        this.gameFieldSubController = new GameFieldSubController(app, gameIDStorage, pioneersService);
        mapPane.getChildren().setAll(gameFieldSubController.render());
        System.out.println(gameIDStorage.getId());

        return parent;
    }


    public void rollDice(ActionEvent event) {
        pioneersService.move(gameIDStorage.getId(),"founding-roll",0,0,0,0,"settlement")
                .observeOn(FX_SCHEDULER)
                .subscribe(roll-> this.diceLabel.setText("" + roll.roll()));
    }
}
