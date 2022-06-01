package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.service.GameIDStorage;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.PioneersService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import javax.inject.Inject;
import java.io.IOException;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class CircleSubController implements Controller{

    private Parent parent;
    private App app;
    private Circle view;
    private PioneersService pioneersService;
    private GameIDStorage gameIDStorage;
    private IDStorage idStorage;
    private String myColor;

    @Inject
    public CircleSubController(App app, Circle view, PioneersService pioneersService, GameIDStorage gameIDStorage, IDStorage idStorage){
        this.app = app;
        this.view = view;
        this.pioneersService = pioneersService;
        this.gameIDStorage = gameIDStorage;
        this.idStorage = idStorage;

    }

    @Override
    public void init() {
        // Add mouse listeners
        this.view.setOnMouseEntered(this::onFieldMouseHoverEnter);
        this.view.setOnMouseExited(this::onFieldMouseHoverExit);
        this.view.setOnMouseClicked(this::onFieldClicked);

        pioneersService.findOnePlayer(gameIDStorage.getId(),idStorage.getID())
                .observeOn(FX_SCHEDULER)
                .subscribe(player -> {
                    this.myColor = player.color();
                });
    }

    private void onFieldClicked(MouseEvent mouseEvent) {
        String id = this.view.getId();
        id = (id.replace("M", "-"));
        id = id.substring(1);
        String[] split = id.split("y");
        int x = Integer.parseInt(split[0]);
        String[] split1 = split[1].split("z");
        int y = Integer.parseInt( split1[0]);
        String[] split2 = split1[1].split("_");
        int z =  Integer.parseInt(split2[0]);
        int side = Integer.parseInt(split2[1]);

        System.out.println(x + " " + y + " "+ z + " " + side);

        this.pioneersService.findOneState(gameIDStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(move ->{

                    String action = move.expectedMoves().get(0).action();
                    if(side == 0 || side == 6){

                        this.pioneersService.move(gameIDStorage.getId(), action, x ,y ,z ,side , "settlement")
                                .observeOn(FX_SCHEDULER)
                                .subscribe(suc->{

                                });
                        this.view.setFill(Paint.valueOf(myColor));
                    }else{
                        this.pioneersService.move(gameIDStorage.getId(), action, x ,y ,z ,side , "road")
                                .observeOn(FX_SCHEDULER)
                                .subscribe();
                        this.view.setFill(Paint.valueOf(myColor));
                    }

                });
    }



    @Override
    public void destroy() {

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
            this.view.setFill(Color.GRAY);
            this.view.setRadius(10.0);
    }

    // Mouse leaves the field
    private void onFieldMouseHoverExit(MouseEvent event) {
        // Change the view
            if(this.view.getFill().equals(Color.GRAY)) {
                this.view.setFill(Color.WHITE);
                this.view.setRadius(10.0);
            }
    }
}
