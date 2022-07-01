package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.service.GameStorage;
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

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.Constants.RESOURCES;

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

    private final GameStorage gameStorage;
    private final PioneersService pioneersService;
    private final IDStorage idStorage;
    private Player player;

    // hashMaps for resources
    private final HashMap<String, Integer> giveResources = new HashMap<>();
    private final HashMap<String, Integer> receiveResources = new HashMap<>();

    @Inject
    public TradingSubController(GameStorage gameStorage,
                                PioneersService pioneersService,
                                IDStorage idStorage) {
        this.gameStorage = gameStorage;
        this.pioneersService = pioneersService;
        this.idStorage = idStorage;
    }

    @Override
    public void init() {
        // init hashMaps
        this.giveResources.put("lumber", 0);
        this.giveResources.put("brick", 0);
        this.giveResources.put("ore", 0);
        this.giveResources.put("wool", 0);
        this.giveResources.put("grain", 0);

        this.receiveResources.put("lumber", 0);
        this.receiveResources.put("brick", 0);
        this.receiveResources.put("ore", 0);
        this.receiveResources.put("wool", 0);
        this.receiveResources.put("grain", 0);

        pioneersService
                .findAllPlayers(this.gameStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(c -> {
                        for (Player player : c) {
                            if (player.userId().equals(idStorage.getID())){
                                this.player = player;
                            }
                        }
                });
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

        makeButtonInvisible(giveCactusMinusButton);
        makeButtonInvisible(giveMarsMinusButton);
        makeButtonInvisible(giveMoonMinusButton);
        makeButtonInvisible(giveNeptunMinusButton);
        makeButtonInvisible(giveVenusMinusButton);
        makeButtonInvisible(receiveCactusMinusButton);
        makeButtonInvisible(receiveMarsMinusButton);
        makeButtonInvisible(receiveMoonMinusButton);
        makeButtonInvisible(receiveNeptunMinusButton);
        makeButtonInvisible(receiveVenusMinusButton);

        return parent;
    }



    public void giveCactusMinusButtonPressed(ActionEvent event) {
        if (Integer.parseInt(this.giveCactusLabel.getText()) > 0) {
            this.giveResources.put("lumber", this.giveResources.get("lumber") - 1);
            this.giveCactusLabel.setText(String.valueOf(this.giveResources.get("lumber")));
        }
        if (this.giveCactusLabel.getText().equals("0")) {
            makeButtonInvisible((Button) event.getSource());
        }
    }

    public void giveCactusPlusButtonPressed(ActionEvent event) {
        this.giveResources.put("lumber", this.giveResources.get("lumber") + 1);
        this.giveCactusLabel.setText(String.valueOf(this.giveResources.get("lumber")));
        if (this.giveCactusMinusButton.disableProperty().get()) {
            makeButtonVisible(this.giveCactusMinusButton);
        }
    }

    public void giveMarsMinusButtonPressed(ActionEvent event) {
        if (Integer.parseInt(this.giveMarsLabel.getText()) > 0) {
            this.giveResources.put("brick", this.giveResources.get("brick") - 1);
            this.giveMarsLabel.setText(String.valueOf(this.giveResources.get("brick")));
        }
        if (this.giveMarsLabel.getText().equals("0")) {
            makeButtonInvisible((Button) event.getSource());
        }
    }

    public void giveMarsPlusButtonPressed(ActionEvent event) {
        this.giveResources.put("brick", this.giveResources.get("brick") + 1);
        this.giveMarsLabel.setText(String.valueOf(this.giveResources.get("brick")));
        if (this.giveMarsMinusButton.disableProperty().get()) {
            makeButtonVisible(this.giveMarsMinusButton);
        }
    }

    public void giveMoonMinusButtonPressed(ActionEvent event) {
        if (Integer.parseInt(this.giveMoonLabel.getText()) > 0) {
            this.giveResources.put("ore", this.giveResources.get("ore") - 1);
            this.giveMoonLabel.setText(String.valueOf(this.giveResources.get("ore")));
        }
        if (this.giveMoonLabel.getText().equals("0")) {
            makeButtonInvisible((Button) event.getSource());
        }
    }

    public void giveMoonPlusButtonPressed(ActionEvent event) {
        this.giveResources.put("ore", this.giveResources.get("ore") + 1);
        this.giveMoonLabel.setText(String.valueOf(this.giveResources.get("ore")));
        if (this.giveMoonMinusButton.disableProperty().get()) {
            makeButtonVisible(this.giveMoonMinusButton);
        }
    }

    public void giveNeptunMinusButtonPressed(ActionEvent event) {
        if (Integer.parseInt(this.giveNeptunLabel.getText()) > 0) {
            this.giveResources.put("wool", this.giveResources.get("wool") - 1);
            this.giveNeptunLabel.setText(String.valueOf(this.giveResources.get("wool")));
        }
        if (this.giveNeptunLabel.getText().equals("0")) {
            makeButtonInvisible((Button) event.getSource());
        }
    }

    public void giveNeptunPlusButtonPressed(ActionEvent event) {
        this.giveResources.put("wool", this.giveResources.get("wool") + 1);
        this.giveNeptunLabel.setText(String.valueOf(this.giveResources.get("wool")));
        if (this.giveNeptunMinusButton.disableProperty().get()) {
            makeButtonVisible(this.giveNeptunMinusButton);
        }
    }

    public void giveVenusMinusButtonPressed(ActionEvent event) {
        if (Integer.parseInt(this.giveVenusLabel.getText()) > 0) {
            this.giveResources.put("grain", this.giveResources.get("grain") - 1);
            this.giveVenusLabel.setText(String.valueOf(this.giveResources.get("grain")));
        }
        if (this.giveVenusLabel.getText().equals("0")) {
            makeButtonInvisible((Button) event.getSource());
        }
    }

    public void giveVenusPlusButtonPressed(ActionEvent event) {
        this.giveResources.put("grain", this.giveResources.get("grain") + 1);
        this.giveVenusLabel.setText(String.valueOf(this.giveResources.get("grain")));
        if (this.giveVenusMinusButton.disableProperty().get()) {
            makeButtonVisible(this.giveVenusMinusButton);
        }
    }

    public void receiveCactusMinusButtonPressed(ActionEvent event) {
        if (Integer.parseInt(this.receiveCactusLabel.getText()) > 0) {
            this.receiveResources.put("lumber", this.receiveResources.get("lumber") - 1);
            this.receiveCactusLabel.setText(String.valueOf(this.receiveResources.get("lumber")));
        }
        if (this.receiveCactusLabel.getText().equals("0")) {
            makeButtonInvisible((Button) event.getSource());
        }
    }

    public void receiveCactusPlusButtonPressed(ActionEvent event) {
        this.receiveResources.put("lumber", this.receiveResources.get("lumber") + 1);
        this.receiveCactusLabel.setText(String.valueOf(this.receiveResources.get("lumber")));
        if (this.receiveCactusMinusButton.disableProperty().get()) {
            makeButtonVisible(this.receiveCactusMinusButton);
        }
    }

    public void receiveMarsMinusButtonPressed(ActionEvent event) {
        if (Integer.parseInt(this.receiveMarsLabel.getText()) > 0) {
            this.receiveResources.put("brick", this.receiveResources.get("brick") - 1);
            this.receiveMarsLabel.setText(String.valueOf(this.receiveResources.get("brick")));
        }
        if (this.receiveMarsLabel.getText().equals("0")) {
            makeButtonInvisible((Button) event.getSource());
        }
    }

    public void receiveMarsPlusButtonPressed(ActionEvent event) {
        this.receiveResources.put("brick", this.receiveResources.get("brick") + 1);
        this.receiveMarsLabel.setText(String.valueOf(this.receiveResources.get("brick")));
        if (this.receiveMarsMinusButton.disableProperty().get()) {
            makeButtonVisible(this.receiveMarsMinusButton);
        }
    }

    public void receiveMoonMinusButtonPressed(ActionEvent event) {
        if (Integer.parseInt(this.receiveMoonLabel.getText()) > 0) {
            this.receiveResources.put("ore", this.receiveResources.get("ore") - 1);
            this.receiveMoonLabel.setText(String.valueOf(this.receiveResources.get("ore")));
        }
        if (this.receiveMoonLabel.getText().equals("0")) {
            makeButtonInvisible((Button) event.getSource());
        }
    }

    public void receiveMoonPlusButtonPressed(ActionEvent event) {
        this.receiveResources.put("ore", this.receiveResources.get("ore") + 1);
        this.receiveMoonLabel.setText(String.valueOf(this.receiveResources.get("ore")));
        if (this.receiveMoonMinusButton.disableProperty().get()) {
            makeButtonVisible(this.receiveMoonMinusButton);
        }
    }

    public void receiveNeptunMinusButtonPressed(ActionEvent event) {
        if (Integer.parseInt(this.receiveNeptunLabel.getText()) > 0) {
            this.receiveResources.put("wool", this.receiveResources.get("wool") - 1);
            this.receiveNeptunLabel.setText(String.valueOf(this.receiveResources.get("wool")));
        }
        if (this.receiveNeptunLabel.getText().equals("0")) {
            makeButtonInvisible((Button) event.getSource());
        }
    }

    public void receiveNeptunPlusButtonPressed(ActionEvent event) {
        this.receiveResources.put("wool", this.receiveResources.get("wool") + 1);
        this.receiveNeptunLabel.setText(String.valueOf(this.receiveResources.get("wool")));
        if (this.receiveNeptunMinusButton.disableProperty().get()) {
            makeButtonVisible(this.receiveNeptunMinusButton);
        }
    }

    public void receiveVenusMinusButtonPressed(ActionEvent event) {
        if (Integer.parseInt(this.receiveVenusLabel.getText()) > 0) {
            this.receiveResources.put("grain", this.receiveResources.get("grain") - 1);
            this.receiveVenusLabel.setText(String.valueOf(this.receiveResources.get("grain")));
        }
        if (this.receiveVenusLabel.getText().equals("0")) {
            makeButtonInvisible((Button) event.getSource());
        }
    }

    public void receiveVenusPlusButtonPressed(ActionEvent event) {
        this.receiveResources.put("grain", this.receiveResources.get("grain") + 1);
        this.receiveVenusLabel.setText(String.valueOf(this.receiveResources.get("grain")));
        if (this.receiveVenusMinusButton.disableProperty().get()) {
            makeButtonVisible(this.receiveVenusMinusButton);
        }
    }

    private void makeButtonVisible(Button button) {
        button.setVisible(true);
        button.disableProperty().set(false);
    }

    private void makeButtonInvisible(Button button) {
        button.disableProperty().set(true);
        button.setVisible(false);
    }

    public void offerPlayerButtonPressed(ActionEvent event) {
    }

    public void offerBankButtonPressed(ActionEvent event) {
        // 4:1
        // check for every resource available, if amount to give is 4 and to receive is 1
        for (String giveRes : RESOURCES) {
            if (this.giveResources.get(giveRes) == 4) {
                for (String receiveRes : RESOURCES) {
                    if (this.receiveResources.get(receiveRes) == 1) {
                        // create hashMap for move: positive val are given, negative val are taken
                        HashMap<String, Integer> tmp = new HashMap<>();
                        tmp.put("grain", 0);
                        tmp.put("brick", 0);
                        tmp.put("ore", 0);
                        tmp.put("lumber", 0);
                        tmp.put("wool", 0);
                        // put the chosen values
                        tmp.put(giveRes, -4);
                        tmp.put(receiveRes, 1);

                        // trade move
                        this.pioneersService.tradeBank(this.gameStorage.getId(), tmp)
                                .observeOn(FX_SCHEDULER)
                                .subscribe(result -> {
                                    pioneersService
                                            .findAllPlayers(this.gameStorage.getId())
                                            .observeOn(FX_SCHEDULER)
                                            .subscribe(c -> {
                                                for (Player player : c) {
                                                    if (player.userId().equals(idStorage.getID())){
                                                        this.player = player;
                                                    }
                                                }
                                            });
                                });

                        this.giveResources.replaceAll((k, v) -> v = 0);
                        this.receiveResources.replaceAll((k, v) -> v = 0);
                    }
                }
            }
        }

        this.giveCactusLabel.setText("0");
        this.giveMarsLabel.setText("0");
        this.giveMoonLabel.setText("0");
        this.giveNeptunLabel.setText("0");
        this.giveVenusLabel.setText("0");
        this.receiveCactusLabel.setText("0");
        this.receiveMarsLabel.setText("0");
        this.receiveMoonLabel.setText("0");
        this.receiveNeptunLabel.setText("0");
        this.receiveVenusLabel.setText("0");
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
