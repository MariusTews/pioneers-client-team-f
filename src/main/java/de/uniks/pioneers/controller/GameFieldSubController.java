package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.model.Map;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.Tile;
import de.uniks.pioneers.service.GameIDStorage;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.PioneersService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import javax.inject.Inject;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class GameFieldSubController implements Controller {

    private final ObservableList<Tile> tiles = FXCollections.observableArrayList();
    private final ObservableList<Player> players = FXCollections.observableArrayList();

    private final IDStorage idStorage;

    private Parent parent;
    private final App app;
    private final GameIDStorage gameIDStorage;
    private final PioneersService pioneersService;
    private final EventListener eventListener;
    private final CompositeDisposable disposable = new CompositeDisposable();


    // This variable is needed for the starting/stopping of the field controllers
    private List<HexSubController> hexSubControllers = new ArrayList<>();
    private final List<CircleSubController> circleSubControllers = new ArrayList<>();

    @Inject
    public GameFieldSubController(App app,
                                  GameIDStorage gameIDStorage,
                                  PioneersService pioneersService,
                                  IDStorage idStorage,
                                  EventListener eventListener) {
        this.app = app;
        this.gameIDStorage = gameIDStorage;
        this.pioneersService = pioneersService;
        this.idStorage = idStorage;
        this.eventListener = eventListener;
    }

    @Override
    public void init() {
        disposable.add(eventListener
                .listen("games." + this.gameIDStorage.getId() + ".buildings.*.*", Building.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    Building building = event.data();
                    this.updateBuildings((int) building.x(), (int) building.y(), (int) building.z(), (int) building.side(), building.owner(), building.type());
                }));

        pioneersService.findAllPlayers(gameIDStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(this.players::addAll);

    }


    @Override
    public void destroy() {
        hexSubControllers.forEach(HexSubController::destroy);
        hexSubControllers.clear();
        circleSubControllers.forEach(CircleSubController::destroy);
        circleSubControllers.clear();
        disposable.dispose();
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/GameFieldSubView.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


        this.parent = parent;

        pioneersService.findAllTiles(gameIDStorage.getId()).observeOn(FX_SCHEDULER).subscribe(this::loadMap);

        return parent;
    }

    private void loadMap(Map map) {
        List<String> waterTilesCircles = Arrays.asList("x1y2zM3_7", "x1y2zM3_6", "x2y1zM3_7", "x2y1zM3_6", "x3y0zM3_7",
                "x3y0zM3_6", "x3yM1zM2_7", "x3yM1zM2_6", "x3yM2zM1_7", "x3yM2zM1_6", "x2yM3z1_0", "x2yM3z1_11", "x1yM3z2_0",
                "x1yM3z2_11", "x0yM3z3_0", "x0yM3z3_11", "xM1yM2z3_0", "xM1yM2z3_11", "xM2yM1z3_0", "xM2yM1z3_11", "xM3y0z3_0",
                "xM3y1z2_3", "xM3y1z2_0", "xM3y2z1_3", "xM3y2z1_0", "xM3y3z0_3", "xM2y3zM1_6", "xM2y3zM1_3", "xM1y3zM2_6",
                "xM1y3zM2_3", "x0y3zM3_6");
        List<String> hexaCoords = new ArrayList<>();
        List<Integer> cirleCoords = new ArrayList<>();
        cirleCoords.add(0);
        cirleCoords.add(3);
        cirleCoords.add(6);
        cirleCoords.add(7);
        cirleCoords.add(11);
        for (Tile tile : map.tiles()) {
            int x = (int) tile.x();
            int y = (int) tile.y();
            int z = (int) tile.z();
            String stringX = "x" + x;
            String stringY = "y" + y;
            String stringZ = "z" + z;
            if (x < 0) {
                stringX = "xM" + Math.abs(x);
            }
            if (y < 0) {
                stringY = "yM" + Math.abs(y);
            }
            if (z < 0) {
                stringZ = "zM" + Math.abs(z);
            }
            HexSubController hexSubController = new HexSubController(app, (Polygon) parent.lookup("#" + stringX + stringY + stringZ), tile);
            //System.out.println("#" + stringX + stringY + stringZ);
            hexaCoords.add("#" + stringX + stringY + stringZ);

            hexSubController.init();
            this.hexSubControllers.add(hexSubController);
        }


        for (int i = 0; i < hexaCoords.size(); i++) {
            for (int j = 0; j < cirleCoords.size(); j++) {
                CircleSubController circleSubController = new CircleSubController(app, (Circle) parent.lookup(hexaCoords.get(i) + "_" + cirleCoords.get(j)), pioneersService, gameIDStorage, idStorage, eventListener, this);
                circleSubController.init();
                this.circleSubControllers.add(circleSubController);
            }
        }
        for (String string : waterTilesCircles) {
            CircleSubController circleSubController = new CircleSubController(app, (Circle) parent.lookup("#" + string), pioneersService, gameIDStorage, idStorage, eventListener, this);
            circleSubController.init();
            this.circleSubControllers.add(circleSubController);
        }
    }

    public void build(String building) {
        for (CircleSubController c : circleSubControllers) {
            c.setBuild(building);
        }
    }

    private void updateBuildings(int x, int y, int z, int side, String owner, String type) {
        String color = Color.BLACK.toString();
        for (Player player : players) {
            if (player.userId().equals(owner)) {
                color = player.color();
            }
        }
        for (CircleSubController c : circleSubControllers) {
            switch (type) {
                case "road" -> c.setRoad(x, y, z, side, color);
                case "settlement" -> c.setSettlement(x, y, z, side, color);
                case "city" -> c.setCity(x, y, z, side, color);
            }

        }
    }
}
