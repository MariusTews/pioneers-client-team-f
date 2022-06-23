package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.model.ExpectedMove;
import de.uniks.pioneers.model.State;
import de.uniks.pioneers.service.GameIDStorage;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.PioneersService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class CircleSubController implements Controller {

    private Parent parent;
    private final Circle view;
    private final PioneersService pioneersService;
    private final GameIDStorage gameIDStorage;
    private final IDStorage idStorage;
    private final EventListener eventListener;
    private final GameFieldSubController gameFieldSubController;
    private final CompositeDisposable disposable = new CompositeDisposable();

    private int x;
    private int y;
    private int z;
    private int side;
    private ExpectedMove nextMove;
    private String build = null;

    @Inject
    public CircleSubController(Parent parent, App app, Circle view, PioneersService pioneersService,
                               GameIDStorage gameIDStorage, IDStorage idStorage, EventListener eventListener,
                               GameFieldSubController gameFieldSubController) {
        this.parent = parent;
        this.view = view;
        this.pioneersService = pioneersService;
        this.gameIDStorage = gameIDStorage;
        this.idStorage = idStorage;
        this.eventListener = eventListener;
        this.gameFieldSubController = gameFieldSubController;
    }

    @Override
    public void init() {
        // Add mouse listeners
        this.view.setOnMouseEntered(this::onFieldMouseHoverEnter);
        this.view.setOnMouseExited(this::onFieldMouseHoverExit);
        this.view.setOnMouseClicked(this::onFieldClicked);

        //get coordinates from fxid
        String id = this.view.getId();
        id = (id.replace("M", "-"));
        id = id.substring(1);
        String[] split = id.split("y");
        this.x = Integer.parseInt(split[0]);
        String[] split1 = split[1].split("z");
        this.y = Integer.parseInt(split1[0]);
        String[] split2 = split1[1].split("_");
        this.z = Integer.parseInt(split2[0]);
        this.side = Integer.parseInt(split2[1]);

        this.nextMove = new ExpectedMove("", Collections.singletonList(idStorage.getID()));

        disposable.add(eventListener
                .listen("games." + this.gameIDStorage.getId() + ".state.*", State.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    State state = event.data();
                    this.nextMove = state.expectedMoves().get(0);
                }));
    }


    private void onFieldClicked(MouseEvent mouseEvent) {
        //if it's not your turn
        if (!yourTurn(nextMove)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Not your turn!");
            // set style
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
                    .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
            alert.showAndWait();
            // if the game is in the founding-phase
        } else if (nextMove.action().startsWith("founding-r") || nextMove.action().startsWith("founding-s") || nextMove.action().equals("")) {
            this.pioneersService.findOneState(gameIDStorage.getId())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(move -> {
                        String action = move.expectedMoves().get(0).action();
                        if (side == 0 || side == 6) {
                            this.pioneersService.move(gameIDStorage.getId(), action, x, y, z, side, "settlement", null, null)
                                    .observeOn(FX_SCHEDULER)
                                    .doOnError(error -> {
                                        String[] building = nextMove.action().split("-");
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "you can't place that " + building[1] + " here!");
                                        // Set style
                                        DialogPane dialogPane = alert.getDialogPane();
                                        dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
                                                .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
                                        alert.showAndWait();
                                    })
                                    .subscribe(onSuc -> {
                                    }, onError -> {
                                    });
                        } else {
                            this.pioneersService.move(gameIDStorage.getId(), action, x, y, z, side, "road", null, null)
                                    .observeOn(FX_SCHEDULER)
                                    .doOnError(error -> {
                                        String[] building = nextMove.action().split("-");
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "you can't place that " + building[1] + " here!");
                                        // Set style
                                        DialogPane dialogPane = alert.getDialogPane();
                                        dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
                                                .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
                                        alert.showAndWait();
                                    })
                                    .subscribe(onSuc -> {
                                    }, onError -> {
                                    });
                        }
                    });
        } else {//if you build a new building in round-loop
            if (build != null) {

                this.pioneersService.move(gameIDStorage.getId(), "build", x, y, z, side, build, null, null)
                        .observeOn(FX_SCHEDULER)
                        .subscribe();

                this.gameFieldSubController.build(null);
            }
        }
    }

    public Boolean yourTurn(ExpectedMove move) {
        List<String> currPlayer = move.players();
        for (String player : currPlayer) {
            if (player.equals(idStorage.getID())) {
                return true;
            }
        }
        return false;
    }


    public void setRoad(int x, int y, int z, int side, String color) {
        if (this.x == x && this.y == y && this.z == z && this.side == side) {
            // Set polygon as road on the coordinates
            Polygon road = new Polygon(-12.0, 5.0, 12.0, 5.0, 12.0, -5.0, -12.0, -5.0);
            road.setLayoutX(view.getLayoutX() + 106.0);
            road.setLayoutY(view.getLayoutY() + 119.0);

            switch (side) {
                case 3 -> road.setRotate(90.0);
                case 11 -> road.setRotate(150.0);
                case 7 -> road.setRotate(30.0);
            }

            road.setFill(Color.valueOf(color));
            Pane pane = (Pane) this.parent;
            Group group = (Group) pane.getChildren().get(0);
            group.getChildren().add(road);
            road.setLayoutX(view.getLayoutX());
            road.setLayoutY(view.getLayoutY());

            this.view.setFill(Color.TRANSPARENT);
            this.view.setStroke(Color.TRANSPARENT);
            this.view.setOnMouseClicked(null);
        }
    }

    public void setSettlement(int x, int y, int z, int side, String color) {
        if (this.x == x && this.y == y && this.z == z && this.side == side) {
            // Set style and image on the coordinates
            this.view.setRadius(20);
            this.view.toFront();
            this.view.setStroke(Color.TRANSPARENT);
            Image settlement = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/settlement_" + color + ".png")).toExternalForm());
            ImagePattern settlementPattern = new ImagePattern(settlement);
            this.view.setFill(settlementPattern);
        }
    }

    public void setCity(int x, int y, int z, int side, String color) {
        if (this.x == x && this.y == y && this.z == z && this.side == side) {
            // Set style and image on the coordinates
            this.view.setRadius(20);
            this.view.setStroke(Color.TRANSPARENT);
            Image city = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/city_" + color + ".png")).toExternalForm());
            ImagePattern cityPattern = new ImagePattern(city);
            this.view.setFill(cityPattern);
        }
    }


    @Override
    public void destroy() {
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
        return parent;
    }

    // Mouse hovers over field
    private void onFieldMouseHoverEnter(MouseEvent event) {
        // Change the view
        if (this.view.getFill().equals(Color.TRANSPARENT)) {
            this.view.setFill(Color.GRAY);
        }
    }

    // Mouse leaves the field
    private void onFieldMouseHoverExit(MouseEvent event) {
        // Change the view
        if (this.view.getFill().equals(Color.GRAY)) {
            this.view.setFill(Color.TRANSPARENT);
        }
    }

    public void setBuild(String build) {
        this.build = build;
    }
}
