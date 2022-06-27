package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.service.GameIDStorage;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.PioneersService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;

public class TradingSubController implements Controller{
    @FXML
    public Button giveCactusMinusButton;
    @FXML
    public Label giveCactusLabel;
    @FXML
    public Button giveCactusPlusButton;
    @FXML
    public Button giveMarsMinusButton;
    @FXML
    public Label giveMarsLabel;
    @FXML
    public Button giveMarsPlusButton;
    @FXML
    public Button giveMoonMinusButton;
    @FXML
    public Label giveMoonLabel;
    @FXML
    public Button giveMoonPlusButton;
    @FXML
    public Button giveNeptunMinusButton;
    @FXML
    public Label giveNeptunLabel;
    @FXML
    public Button giveNeptunPlusButton;
    @FXML
    public Button giveVenusMinusButton;
    @FXML
    public Label giveVenusLabel;
    @FXML
    public Button giveVenusPlusButton;
    @FXML
    public Button receiveCactusMinusButton;
    @FXML
    public Label receiveCactusLabel;
    @FXML
    public Button receiveCactusPlusButton;
    @FXML
    public Button receiveMarsMinusButton;
    @FXML
    public Label receiveMarsLabel;
    @FXML
    public Button receiveMarsPlusButton;
    @FXML
    public Button receiveMoonMinusButton;
    @FXML
    public Label receiveMoonLabel;
    @FXML
    public Button receiveMoonPlusButton;
    @FXML
    public Button receiveNeptunMinusButton;
    @FXML
    public Label receiveNeptunLabel;
    @FXML
    public Button receiveNeptunPlusButton;
    @FXML
    public Button receiveVenusMinusButton;
    @FXML
    public Label receiveVenusLabel;
    @FXML
    public Button receiveVenusPlusButton;
    @FXML
    public Button offerPlayerButton;
    @FXML
    public Button offerBankButton;

    private Parent parent;
    private final App app;
    private final GameIDStorage gameIDStorage;
    private final PioneersService pioneersService;
    private final IDStorage idStorage;
    private final EventListener eventListener;
    private final Player player;

    // hashMaps for resources
    private final HashMap<String, Integer> giveResources = new HashMap<>();
    private final HashMap<String, Integer> receiveResources = new HashMap<>();


    @Inject
    public TradingSubController(App app,
                                GameIDStorage gameIDStorage,
                                PioneersService pioneersService,
                                IDStorage idStorage,
                                EventListener eventListener,
                                Player player) {
        this.app = app;
        this.gameIDStorage = gameIDStorage;
        this.pioneersService = pioneersService;
        this.idStorage = idStorage;
        this.eventListener = eventListener;
        this.player = player;
    }

    @Override
    public void init() {
        // init hashMaps
        this.giveResources.put("cactus", 0);
        this.giveResources.put("mars", 0);
        this.giveResources.put("moon", 0);
        this.giveResources.put("neptun", 0);
        this.giveResources.put("venus", 0);

        this.receiveResources.put("cactus", 0);
        this.receiveResources.put("mars", 0);
        this.receiveResources.put("moon", 0);
        this.receiveResources.put("neptun", 0);
        this.receiveResources.put("venus", 0);
    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/TradingSubView.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        this.parent = parent;
        return parent;
    }

    public void giveCactusMinusButtonPressed(ActionEvent event) {
        if (Integer.parseInt(this.giveCactusLabel.getText()) > 0) {
            this.giveResources.put("cactus", this.giveResources.get("cactus") - 1);
            this.giveCactusLabel.setText(String.valueOf(this.giveResources.get("cactus")));
        }
    }

    public void giveCactusPlusButtonPressed(ActionEvent event) {
        this.giveResources.put("cactus", this.giveResources.get("cactus") + 1);
        this.giveCactusLabel.setText(String.valueOf(this.giveResources.get("cactus")));
    }

    public void giveMarsMinusButtonPressed(ActionEvent event) {
        if (Integer.parseInt(this.giveMarsLabel.getText()) > 0) {
            this.giveResources.put("mars", this.giveResources.get("mars") - 1);
            this.giveMarsLabel.setText(String.valueOf(this.giveResources.get("mars")));
        }
    }

    public void giveMarsPlusButtonPressed(ActionEvent event) {
        this.giveResources.put("mars", this.giveResources.get("mars") + 1);
        this.giveMarsLabel.setText(String.valueOf(this.giveResources.get("mars")));
    }

    public void giveMoonMinusButtonPressed(ActionEvent event) {
        if (Integer.parseInt(this.giveMoonLabel.getText()) > 0) {
            this.giveResources.put("moon", this.giveResources.get("moon") - 1);
            this.giveMoonLabel.setText(String.valueOf(this.giveResources.get("moon")));
        }
    }

    public void giveMoonPlusButtonPressed(ActionEvent event) {
        this.giveResources.put("moon", this.giveResources.get("moon") + 1);
        this.giveMoonLabel.setText(String.valueOf(this.giveResources.get("moon")));
    }

    public void giveNeptunMinusButtonPressed(ActionEvent event) {
        if (Integer.parseInt(this.giveNeptunLabel.getText()) > 0) {
            this.giveResources.put("neptun", this.giveResources.get("neptun") - 1);
            this.giveNeptunLabel.setText(String.valueOf(this.giveResources.get("neptun")));
        }
    }

    public void giveNeptunPlusButtonPressed(ActionEvent event) {
        this.giveResources.put("neptun", this.giveResources.get("neptun") + 1);
        this.giveNeptunLabel.setText(String.valueOf(this.giveResources.get("neptun")));
    }

    public void giveVenusMinusButtonPressed(ActionEvent event) {
        if (Integer.parseInt(this.giveVenusLabel.getText()) > 0) {
            this.giveResources.put("venus", this.giveResources.get("venus") - 1);
            this.giveVenusLabel.setText(String.valueOf(this.giveResources.get("venus")));
        }
    }

    public void giveVenusPlusButtonPressed(ActionEvent event) {
        this.giveResources.put("venus", this.giveResources.get("venus") + 1);
        this.giveVenusLabel.setText(String.valueOf(this.giveResources.get("venus")));
    }

    public void receiveCactusMinusButtonPressed(ActionEvent event) {
        if (Integer.parseInt(this.receiveCactusLabel.getText()) > 0) {
            this.receiveResources.put("cactus", this.receiveResources.get("cactus") - 1);
            this.receiveCactusLabel.setText(String.valueOf(this.receiveResources.get("cactus")));
        }
    }

    public void receiveCactusPlusButtonPressed(ActionEvent event) {
        this.receiveResources.put("cactus", this.receiveResources.get("cactus") + 1);
        this.receiveCactusLabel.setText(String.valueOf(this.receiveResources.get("cactus")));
    }

    public void receiveMarsMinusButtonPressed(ActionEvent event) {
        if (Integer.parseInt(this.receiveMarsLabel.getText()) > 0) {
            this.receiveResources.put("mars", this.receiveResources.get("mars") - 1);
            this.receiveMarsLabel.setText(String.valueOf(this.receiveResources.get("mars")));
        }
    }

    public void receiveMarsPlusButtonPressed(ActionEvent event) {
        this.receiveResources.put("mars", this.receiveResources.get("mars") - 1);
        this.receiveMarsLabel.setText(String.valueOf(this.receiveResources.get("mars")));
    }

    public void receiveMoonMinusButtonPressed(ActionEvent event) {
        if (Integer.parseInt(this.receiveMoonLabel.getText()) > 0) {
            this.receiveResources.put("moon", this.receiveResources.get("moon") - 1);
            this.receiveMoonLabel.setText(String.valueOf(this.receiveResources.get("moon")));
        }
    }

    public void receiveMoonPlusButtonPressed(ActionEvent event) {
        this.receiveResources.put("moon", this.receiveResources.get("moon") - 1);
        this.receiveMoonLabel.setText(String.valueOf(this.receiveResources.get("moon")));
    }

    public void receiveNeptunMinusButtonPressed(ActionEvent event) {
        if (Integer.parseInt(this.receiveNeptunLabel.getText()) > 0) {
            this.receiveResources.put("neptun", this.receiveResources.get("neptun") - 1);
            this.receiveNeptunLabel.setText(String.valueOf(this.receiveResources.get("neptun")));
        }
    }

    public void receiveNeptunPlusButtonPressed(ActionEvent event) {
        this.receiveResources.put("neptun", this.receiveResources.get("neptun") - 1);
        this.receiveNeptunLabel.setText(String.valueOf(this.receiveResources.get("neptun")));
    }

    public void receiveVenusMinusButtonPressed(ActionEvent event) {
        if (Integer.parseInt(this.receiveVenusLabel.getText()) > 0) {
            this.receiveResources.put("venus", this.receiveResources.get("venus") - 1);
            this.receiveVenusLabel.setText(String.valueOf(this.receiveResources.get("venus")));
        }
    }

    public void receiveVenusPlusButtonPressed(ActionEvent event) {
        this.receiveResources.put("venus", this.receiveResources.get("venus") - 1);
        this.receiveVenusLabel.setText(String.valueOf(this.receiveResources.get("venus")));
    }

    public void offerPlayerButtonPressed(ActionEvent event) {
    }

    public void offerBankButtonPressed(ActionEvent event) {
    }
}
