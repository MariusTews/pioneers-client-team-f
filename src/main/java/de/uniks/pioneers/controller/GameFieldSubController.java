package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Map;
import de.uniks.pioneers.model.Tile;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.PioneersApiService;
import de.uniks.pioneers.service.GameIDStorage;
import de.uniks.pioneers.service.PioneersService;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class GameFieldSubController implements Controller{

    private final ObservableList<Tile> tiles = FXCollections.observableArrayList();



    private Parent parent;
    private App app;
    private GameIDStorage gameIDStorage;
    private PioneersService pioneersService;


    // This variable is needed for the starting/stopping of the field controllers
    private List<HexSubController> hexSubControllers = new ArrayList<>();
    private List<CircleSubController> circleSubControllers = new ArrayList<>();

    @Inject
    public GameFieldSubController(App app,
                                  GameIDStorage gameIDStorage,
                                  PioneersService pioneersService){
        this.app = app;
        this.gameIDStorage = gameIDStorage;
        this.pioneersService = pioneersService;
    }

    @Override
    public void init() {
    }



    @Override
    public void destroy() {
        hexSubControllers.forEach(HexSubController::destroy);
        hexSubControllers.clear();
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

        for (int i=0; i < hexaCoords.size(); i++) {
            for (int j=0; j < cirleCoords.size(); j++) {
                CircleSubController circleSubController = new CircleSubController(app, (Circle) parent.lookup(hexaCoords.get(i) + "_" + cirleCoords.get(j)));
                circleSubController.init();
                System.out.println(hexaCoords.get(i) + "_" + cirleCoords.get(j));
            }
        }
    }
}
