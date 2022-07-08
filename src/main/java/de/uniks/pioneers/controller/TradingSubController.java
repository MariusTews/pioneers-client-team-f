package de.uniks.pioneers.controller;

import com.sun.jdi.BooleanValue;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.dto.CreateBuildingDto;
import de.uniks.pioneers.model.Point3D;
import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.model.Harbor;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.Tile;
import de.uniks.pioneers.service.GameStorage;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.PioneersService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.Constants.RESOURCES;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class TradingSubController implements Controller {
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
    private HashMap<String, Boolean> harborHashCheck = new HashMap<>();
    private final List<Harbor> harbors = new ArrayList<>();

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
                        if (player.userId().equals(idStorage.getID())) {
                            this.player = player;
                        }
                    }
                });

        // get all harbors and init harborHashCheck
        pioneersService
                .findAllTiles(gameStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(tiles -> {
                    for (Harbor harbor : tiles.harbors()) {
                        this.harbors.add(harbor);
                        harborHashCheck.put(harbor.type(), false);
                    }
                });

        //TODO: experiment
        pioneersService
                .findAllTiles(gameStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                    for (Harbor harbor : result.harbors()) {
                        System.out.println("Harbor: " + harbor.type());
                        System.out.println("(" + harbor.x() + ", " + harbor.y() + ", " + harbor.z() + ")");
                        System.out.println("Side: " + harbor.side());
                        System.out.println("");
                    }
                    System.out.println("----------------------------------------------------------------------");
                    for (Tile tile : result.tiles()) {
                        System.out.println("Tile: " + tile.type());
                        System.out.println("(" + tile.x() + ", " + tile.y() + ", " + tile.z() + ")");
                        System.out.println("");
                    }
                    System.out.println("----------------------------------------------------------------------");
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

    /*
     * Every minus button:
     *   check, if amount of resource is above 0
     *   decrease amount, if pressed
     *   make button invisible, if amount equals 0
     * Every plus button:
     *   increase amount of resource until value of available resource
     *   make minus button visible, if value is increased
     * */

    public void giveCactusMinusButtonPressed(ActionEvent event) {
        minusAction(event, "lumber", giveCactusLabel, true, giveCactusPlusButton);
    }

    public void giveCactusPlusButtonPressed(ActionEvent event) {
        plusAction(event, "lumber", giveCactusLabel, true, giveCactusMinusButton);
    }

    public void giveMarsMinusButtonPressed(ActionEvent event) {
        minusAction(event, "brick", giveMarsLabel, true, giveMarsPlusButton);
    }

    public void giveMarsPlusButtonPressed(ActionEvent event) {
        plusAction(event, "brick", giveMarsLabel, true, giveMarsMinusButton);
    }

    public void giveMoonMinusButtonPressed(ActionEvent event) {
        minusAction(event, "ore", giveMoonLabel, true, giveMoonPlusButton);
    }

    public void giveMoonPlusButtonPressed(ActionEvent event) {
        plusAction(event, "ore", giveMoonLabel, true, giveMoonMinusButton);
    }

    public void giveNeptunMinusButtonPressed(ActionEvent event) {
        minusAction(event, "wool", giveNeptunLabel, true, giveNeptunPlusButton);
    }

    public void giveNeptunPlusButtonPressed(ActionEvent event) {
        plusAction(event, "wool", giveNeptunLabel, true, giveNeptunMinusButton);
    }

    public void giveVenusMinusButtonPressed(ActionEvent event) {
        minusAction(event, "grain", giveVenusLabel, true, giveVenusPlusButton);
    }

    public void giveVenusPlusButtonPressed(ActionEvent event) {
        plusAction(event, "grain", giveVenusLabel, true, giveVenusMinusButton);
    }

    public void receiveCactusMinusButtonPressed(ActionEvent event) {
        minusAction(event, "lumber", receiveCactusLabel, false, receiveCactusPlusButton);
    }

    public void receiveCactusPlusButtonPressed(ActionEvent event) {
        plusAction(event, "lumber", receiveCactusLabel, false, receiveCactusMinusButton);
    }

    public void receiveMarsMinusButtonPressed(ActionEvent event) {
        minusAction(event, "brick", receiveMarsLabel, false, receiveMarsPlusButton);
    }

    public void receiveMarsPlusButtonPressed(ActionEvent event) {
        plusAction(event, "brick", receiveMarsLabel, false, receiveMarsMinusButton);
    }

    public void receiveMoonMinusButtonPressed(ActionEvent event) {
        minusAction(event, "ore", receiveMoonLabel, false, receiveMoonPlusButton);
    }

    public void receiveMoonPlusButtonPressed(ActionEvent event) {
        plusAction(event, "ore", receiveMoonLabel, false, receiveMoonMinusButton);
    }

    public void receiveNeptunMinusButtonPressed(ActionEvent event) {
        minusAction(event, "wool", receiveNeptunLabel, false, receiveNeptunPlusButton);
    }

    public void receiveNeptunPlusButtonPressed(ActionEvent event) {
        plusAction(event, "wool", receiveNeptunLabel, false, receiveNeptunMinusButton);
    }

    public void receiveVenusMinusButtonPressed(ActionEvent event) {
        minusAction(event, "grain", receiveVenusLabel, false, receiveVenusPlusButton);
    }

    public void receiveVenusPlusButtonPressed(ActionEvent event) {
        plusAction(event, "grain", receiveVenusLabel, false, receiveVenusMinusButton);
    }

    public void offerPlayerButtonPressed() {
    }

    public void offerBankButtonPressed() {

        pioneersService
                .findAllBuildings(gameStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe();

        updateHarbors();

        // create hashMap for move: positive val are given, negative val are taken
        HashMap<String, Integer> tmp = new HashMap<>();
        tmp.put("grain", 0);
        tmp.put("brick", 0);
        tmp.put("ore", 0);
        tmp.put("lumber", 0);
        tmp.put("wool", 0);

        // check for mixed resources
        int checkGiveRes = 0;
        int checkReceiveRes = 0;

        for (String res : RESOURCES) {
            if (this.giveResources.get(res) > 0) {
                checkGiveRes++;
            }
            if (this.receiveResources.get(res) > 0) {
                checkReceiveRes++;
            }
        }

        if (checkGiveRes > 1 || checkReceiveRes > 1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You can only select one type of resource when trading with the bank");
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
                    .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
            alert.showAndWait();
        } else {
            // 4:1 trade with bank
            // check for every resource available, if amount to give is 4 and to receive is 1
            for (String giveRes : RESOURCES) {
                if (this.giveResources.get(giveRes) == 4) {
                    for (String receiveRes : RESOURCES) {
                        if (this.receiveResources.get(receiveRes) == 1) {
                            // put the chosen values
                            tmp.put(giveRes, -4);
                            tmp.put(receiveRes, 1);

                            // trade move
                            this.pioneersService.tradeBank(this.gameStorage.getId(), tmp)
                                    .observeOn(FX_SCHEDULER)
                                    .subscribe(result -> pioneersService
                                            .findAllPlayers(this.gameStorage.getId())
                                            .observeOn(FX_SCHEDULER)
                                            .subscribe(c -> {
                                                for (Player player : c) {
                                                    if (player.userId().equals(idStorage.getID())) {
                                                        this.player = player;
                                                    }
                                                }
                                            }));
                        }
                    }
                }
            }
        }

        //TODO: experiment
        pioneersService
                .findAllBuildings(gameStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                    for (Building building : result) {
                        System.out.println("Koord.: " + "(" + building.x() + ", " + building.y() + ", " + building.z() + ")");
                        System.out.println("Side: " + building.side());
                        System.out.println("Owner: " + building.owner());
                        System.out.println("Type: " + building.type());
                        System.out.println("");
                    }
                });

        this.giveResources.replaceAll((k, v) -> v = 0);
        this.receiveResources.replaceAll((k, v) -> v = 0);

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

        //TODO: remove
        for (String res : RESOURCES) {
            System.out.println(res + " = " + harborHashCheck.get(res));
        }
        System.out.println(harborHashCheck.get(null));
    }

    // Additional methods

    /*
     * decrease the label
     * differentiate between giving and receiving resource labels
     * enable plus button or disable the minus button, when certain limits are reached
     * */
    private void minusAction(ActionEvent event, String resource, Label label, boolean give, Button plusButton) {
        if (Integer.parseInt(label.getText()) > 0) {
            if (give) {
                this.giveResources.put(resource, this.giveResources.get(resource) - 1);
                label.setText(String.valueOf(this.giveResources.get(resource)));
            } else {
                this.receiveResources.put(resource, this.receiveResources.get(resource) - 1);
                label.setText(String.valueOf(this.receiveResources.get(resource)));
            }
        }
        if (label.getText().equals("0")) {
            makeButtonInvisible((Button) event.getSource());
        }
        if (plusButton.disableProperty().get()) {
            makeButtonVisible(plusButton);
        }
    }

    /*
     *  increase the label
     *  check amount of resources, so it can't get greater and disable the plus button if so
     * */
    private void plusAction(ActionEvent event, String resource, Label label, boolean give, Button minusButton) {
        if (give) {
            // player not null prevents exceptions, when the button is pressed and the player hasn't any resources
            if (player.resources().get(resource) != null) {
                if (checkAmount(label, resource, true)) {
                    this.giveResources.put(resource, this.giveResources.get(resource) + 1);
                    label.setText(String.valueOf(this.giveResources.get(resource)));
                }
            }
        } else {
            if (checkAmount(label, resource, false)) {
                this.receiveResources.put(resource, this.receiveResources.get(resource) + 1);
                label.setText(String.valueOf(this.receiveResources.get(resource)));
            }
        }

        if (minusButton.disableProperty().get()) {
            makeButtonVisible(minusButton);
        }
        if (!checkAmount(label, resource, give)) {
            makeButtonInvisible((Button) event.getSource());
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

    private boolean checkAmount(Label label, String resource, boolean give) {
        if (give) {
            return Integer.parseInt(label.getText()) < player.resources().get(resource);
        }
        return Integer.parseInt(label.getText()) < 1;
    }

    //TODO: position hashes to the top
    private HashMap<Point3D, Building> playersBuildingsZero = new HashMap<>();
    private HashMap<Point3D, Building> playersBuildingsSix = new HashMap<>();

    private void updateHarbors() {
        // get or update all building with sides 0 and 6
        pioneersService
                .findAllBuildings(gameStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(buildings -> {
                    /* search for buildings in the game
                     * put the buildings that belong to the player in to different hashMaps
                     * one map contains all buildings with side 0 and one with side 6*/
                    for (Building building : buildings) {
                        if (building.owner().equals(idStorage.getID()) &&
                                !playersBuildingsZero.containsValue(building) &&
                                !playersBuildingsSix.containsValue(building)) {
                            if (building.side().intValue() == 0) {
                                playersBuildingsZero.put(new Point3D(building.x(), building.y(), building.z()), building);
                            }
                            if (building.side().intValue() == 6) {
                                playersBuildingsSix.put(new Point3D(building.x(), building.y(), building.z()), building);
                            }
                        }
                    }
                });

        /*
         * update all harbors
         * check for a building on a harbors position
         * set boolean of that harbor to true
         * */
        for (Harbor harbor : harbors) {
            switch (harbor.side().intValue()) {
                case 1 -> {
                    checkHarborPosZero(new Point3D(harbor.x(), harbor.y(), harbor.z()), harbor.type());
                    checkHarborPosSix(new Point3D(harbor.x().intValue() + 1, harbor.y(), harbor.z().intValue() - 1), harbor.type());
                }
                case 3 -> {
                    checkHarborPosZero(new Point3D(harbor.x(), harbor.y().intValue() - 1, harbor.z().intValue() + 1), harbor.type());
                    checkHarborPosSix(new Point3D(harbor.x().intValue() + 1, harbor.y(), harbor.z().intValue() - 1), harbor.type());
                }
                case 5 -> {
                    checkHarborPosZero(new Point3D(harbor.x(), harbor.y().intValue() - 1, harbor.z().intValue() + 1), harbor.type());
                    checkHarborPosSix(new Point3D(harbor.x(), harbor.y(), harbor.z()), harbor.type());
                }
                case 7 -> {
                    checkHarborPosZero(new Point3D(harbor.x().intValue() - 1, harbor.y(), harbor.z().intValue() + 1), harbor.type());
                    checkHarborPosSix(new Point3D(harbor.x(), harbor.y(), harbor.z()), harbor.type());
                }
                case 9 -> {
                    checkHarborPosZero(new Point3D(harbor.x().intValue() - 1, harbor.y(), harbor.z().intValue() + 1), harbor.type());
                    checkHarborPosSix(new Point3D(harbor.x(), harbor.y().intValue() + 1, harbor.z().intValue() - 1), harbor.type());
                }
                case 11 -> {
                    checkHarborPosZero(new Point3D(harbor.x(), harbor.y(), harbor.z()), harbor.type());
                    checkHarborPosSix(new Point3D(harbor.x(), harbor.y().intValue() + 1, harbor.z().intValue() - 1), harbor.type());
                }
                default -> {
                }
            }
        }
    }

    private void checkHarborPosZero(Point3D coordinates, String type) {
        if (playersBuildingsZero.containsKey(coordinates)) {
            harborHashCheck.put(type, true);
        }
    }

    private void checkHarborPosSix(Point3D coordinates, String type) {
        if (playersBuildingsSix.containsKey(coordinates)) {
            harborHashCheck.put(type, true);
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
