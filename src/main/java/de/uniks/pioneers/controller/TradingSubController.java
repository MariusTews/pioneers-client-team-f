package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.service.*;
import de.uniks.pioneers.Websocket.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static de.uniks.pioneers.Constants.*;

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
    private final AchievementsService achievementsService;
    private final IDStorage idStorage;
    private final EventListener eventListener;
    private Player player;

    // hashMaps for resources
    private final HashMap<String, Integer> giveResources = new HashMap<>();
    private final HashMap<String, Integer> receiveResources = new HashMap<>();
    private final HashMap<Point3D, Building> playersBuildingsZero = new HashMap<>();
    private final HashMap<Point3D, Building> playersBuildingsSix = new HashMap<>();
    private final HashMap<String, Boolean> harborHashCheck = new HashMap<>();

    // lists
    private final List<Harbor> harbors = new ArrayList<>();
    private final List<Building> buildings = new ArrayList<>();

    private final CompositeDisposable disposable = new CompositeDisposable();

    // check variables for chosen resources
    private boolean sameType = false;
    private int checkGiveRes = 0;
    private int checkReceiveRes = 0;

    @Inject
    public TradingSubController(GameStorage gameStorage,
                                PioneersService pioneersService,
                                AchievementsService achievementsService,
                                IDStorage idStorage,
                                EventListener eventListener) {
        this.gameStorage = gameStorage;
        this.pioneersService = pioneersService;
        this.achievementsService = achievementsService;
        this.idStorage = idStorage;
        this.eventListener = eventListener;
    }

    @Override
    public void init() {
        // init hashMaps
        for (String res : RESOURCES) {
            this.giveResources.put(res, 0);
            this.receiveResources.put(res, 0);
        }

        pioneersService
                .findAllPlayers(this.gameStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(players -> {
                    for (Player player : players) {
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

        disposable.add(eventListener
                .listen("games." + gameStorage.getId() + ".buildings.*.*", Building.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(buildingEvent -> {
                    if (buildingEvent.event().endsWith(CREATED)) {
                        buildings.add(buildingEvent.data());
                    }
                }));
    }

    @Override
    public void destroy() {
        disposable.clear();
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

        makeButtonVisible(giveCactusMinusButton, false);
        makeButtonVisible(giveMarsMinusButton, false);
        makeButtonVisible(giveMoonMinusButton, false);
        makeButtonVisible(giveNeptunMinusButton, false);
        makeButtonVisible(giveVenusMinusButton, false);
        makeButtonVisible(receiveCactusMinusButton, false);
        makeButtonVisible(receiveMarsMinusButton, false);
        makeButtonVisible(receiveMoonMinusButton, false);
        makeButtonVisible(receiveNeptunMinusButton, false);
        makeButtonVisible(receiveVenusMinusButton, false);

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

    /*
     * initialize tmp hashMap for resources to give and to receive
     * check quantity and equality of resources
     * put right values into the tmp hashMap
     * make initial trade call
     * */
    public void offerPlayerButtonPressed() {
        HashMap<String, Integer> tmp = new HashMap<>();
        for (String res : RESOURCES) {
            tmp.put(res, 0);
        }

        // check chosen amount of resources, so no 2:0 or other trade can be made
        checkResources();

        if (this.sameType) {
            alert("You cannot trade the same type of resource!");
        } else if (this.checkGiveRes == 0 || this.checkReceiveRes == 0) {
            alert("Amount to give or to receive is 0!");
        } else {
            for (String res : RESOURCES) {
                if (this.giveResources.get(res) > 0) {
                    tmp.put(res, (-1) * this.giveResources.get(res));
                }
                if (this.receiveResources.get(res) > 0) {
                    tmp.put(res, this.receiveResources.get(res));
                }
            }

            pioneersService
                    .tradePlayer(gameStorage.getId(), "build", null, tmp)
                    .observeOn(FX_SCHEDULER)
                    .subscribe();
        }
        resetButtonsAndLabels();
    }

    public void offerBankButtonPressed() {
        updateHarbors();
        checkResources();

        if (this.sameType) {
            alert("You cannot trade the same type of resource!");
        } else {
            if (this.checkGiveRes > 1 || this.checkReceiveRes > 1) {
                alert("You can only select one type of resource when trading with the bank");
            } else if (this.checkGiveRes == 0 || this.checkReceiveRes == 0) {
                alert("Amount to give or to receive is 0!");
            } else {
                // different conditions for 4:1, 3:1 and 2:1 trades
                for (String giveRes : RESOURCES) {
                    switch (this.giveResources.get(giveRes)) {
                        case 2 -> {
                            if (harborHashCheck.get(giveRes)) {
                                tradeWithBank(giveRes, 2);
                            } else {
                                alert("Trade was not successful. You don't have a building on a matching 2:1 harbor!");
                            }
                        }
                        case 3 -> {
                            if (harborHashCheck.get(null)) {
                                tradeWithBank(giveRes, 3);
                            } else {
                                alert("Trade was not successful. You don't have a building on a 3:1 harbor!");
                            }
                        }
                        case 4 -> tradeWithBank(giveRes, 4);
                        default -> {
                        }
                    }
                }
            }
        }

        // reset all buttons to initial settings
        resetButtonsAndLabels();
    }

    // Additional methods

    private void alert(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, text);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
                .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
        alert.showAndWait();
    }

    /*
     * check for mixed resources
     * increase count for every label greater 0
     * when counter is greater than 1, more than one resource were selected
     * */
    private void checkResources() {
        for (String res : RESOURCES) {
            if (this.giveResources.get(res) > 0 && this.receiveResources.get(res) > 0) {
                sameType = true;
            }
            if (this.giveResources.get(res) > 0) {
                checkGiveRes++;
            }
            if (this.receiveResources.get(res) > 0) {
                checkReceiveRes++;
            }
        }
    }

    /*
     * trade a given resource
     * check, if a receiving resource was selected and make move
     * */
    private void tradeWithBank(String giveRes, int amount) {
        // initialize temporary hashMap for move
        HashMap<String, Integer> tmp = new HashMap<>();
        for (String res : RESOURCES) {
            tmp.put(res, 0);
        }

        for (String receiveRes : RESOURCES) {
            if (this.receiveResources.get(receiveRes) == 1) {
                // put the chosen values in hashMap
                tmp.put(giveRes, -amount);
                tmp.put(receiveRes, 1);

                // trade move
                this.pioneersService
                        .tradeBank(this.gameStorage.getId(), tmp)
                        .observeOn(FX_SCHEDULER)
                        .subscribe(result -> {
                                    achievementsService.putOrUpdateAchievement(TRADE_BANK, 1).blockingFirst();
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
                                }
                        );
            }
        }
    }

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
            makeButtonVisible((Button) event.getSource(), false);
        }
        if (plusButton.disableProperty().get()) {
            makeButtonVisible(plusButton, true);
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
            this.receiveResources.put(resource, this.receiveResources.get(resource) + 1);
            label.setText(String.valueOf(this.receiveResources.get(resource)));
        }

        if (minusButton.disableProperty().get() && Integer.parseInt(label.getText()) > 0) {
            makeButtonVisible(minusButton, true);
        }
        if (!checkAmount(label, resource, give)) {
            makeButtonVisible((Button) event.getSource(), false);
        }
    }

    private void makeButtonVisible(Button button, boolean visible) {
        button.setVisible(visible);
        button.disableProperty().set(!visible);
    }

    private void resetButtonsAndLabels() {
        // clear hashes
        this.giveResources.replaceAll((k, v) -> v = 0);
        this.receiveResources.replaceAll((k, v) -> v = 0);

        // set labels to zero
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

        // reset buttons
        makeButtonVisible(this.giveCactusMinusButton, false);
        makeButtonVisible(this.giveMarsMinusButton, false);
        makeButtonVisible(this.giveMoonMinusButton, false);
        makeButtonVisible(this.giveNeptunMinusButton, false);
        makeButtonVisible(this.giveVenusMinusButton, false);
        makeButtonVisible(this.receiveCactusMinusButton, false);
        makeButtonVisible(this.receiveMarsMinusButton, false);
        makeButtonVisible(this.receiveMoonMinusButton, false);
        makeButtonVisible(this.receiveNeptunMinusButton, false);
        makeButtonVisible(this.receiveVenusMinusButton, false);

        makeButtonVisible(this.giveCactusPlusButton, true);
        makeButtonVisible(this.giveMarsPlusButton, true);
        makeButtonVisible(this.giveMoonPlusButton, true);
        makeButtonVisible(this.giveNeptunPlusButton, true);
        makeButtonVisible(this.giveVenusPlusButton, true);
        makeButtonVisible(this.receiveCactusPlusButton, true);
        makeButtonVisible(this.receiveMarsPlusButton, true);
        makeButtonVisible(this.receiveMoonPlusButton, true);
        makeButtonVisible(this.receiveNeptunPlusButton, true);
        makeButtonVisible(this.receiveVenusPlusButton, true);

        // reset check variables
        this.sameType = false;
        this.checkGiveRes = 0;
        this.checkReceiveRes = 0;
    }

    // check amount of resource to give or to receive
    private boolean checkAmount(Label label, String resource, boolean give) {
        if (give) {
            if (player.resources().get(resource) != null) {
                return Integer.parseInt(label.getText()) < player.resources().get(resource);
            }
        }
        return true;
    }

    private void updateHarbors() {
        // get or update all building with sides 0 and 6
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

        /*
         * update all harbors
         * check for a building on a harbors position
         * set boolean of that harbor to true
         * reposition coordinates of harbor to the coordinates of building
         * every side needs different repositioning of the coordinates
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

    // do the coordinates match on position 0 or 6
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
