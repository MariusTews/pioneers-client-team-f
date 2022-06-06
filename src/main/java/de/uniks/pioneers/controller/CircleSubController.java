package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.service.GameIDStorage;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.PioneersService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
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
    private final EventListener eventListener;
    private final CompositeDisposable disposable = new CompositeDisposable();

    private int x;
    private int y;
    private int z;
    private int side;

    @Inject
    public CircleSubController(App app, Circle view, PioneersService pioneersService, GameIDStorage gameIDStorage, IDStorage idStorage, EventListener eventListener){
        this.app = app;
        this.view = view;
        this.pioneersService = pioneersService;
        this.gameIDStorage = gameIDStorage;
        this.idStorage = idStorage;
        this.eventListener = eventListener;

    }

    @Override
    public void init() {
        // Add mouse listeners
        this.view.setOnMouseEntered(this::onFieldMouseHoverEnter);
        this.view.setOnMouseExited(this::onFieldMouseHoverExit);
        this.view.setOnMouseClicked(this::onFieldClicked);

        /*pioneersService.findOnePlayer(gameIDStorage.getId(),idStorage.getID())
                .observeOn(FX_SCHEDULER)
                .subscribe(player -> {
                    this.myColor = player.color();
                });*/
        String id = this.view.getId();
        System.out.println(id);
        id = (id.replace("M", "-"));
        id = id.substring(1);
        String[] split = id.split("y");
        this.x = Integer.parseInt(split[0]);
        String[] split1 = split[1].split("z");
        this.y = Integer.parseInt( split1[0]);
        String[] split2 = split1[1].split("_");
        this.z =  Integer.parseInt(split2[0]);
        this.side = Integer.parseInt(split2[1]);

        /*disposable.add(eventListener
                .listen("games." + this.gameIDStorage.getId() + ".buildings.*." + "created", Building.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event ->{
                    Building building = event.data();
                    this.setColor((int) building.x(),(int) building.y(),(int) building.z(),(int) building.side());
                    System.out.println(event.event());
                    System.out.println(event.data().type());
                }));*/
    }

    private void onFieldClicked(MouseEvent mouseEvent) {


        System.out.println(x + " " + y + " "+ z + " " + side);

        this.pioneersService.findOneState(gameIDStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(move ->{

                    String action = move.expectedMoves().get(0).action();
                    if(side == 0 || side == 6){

                        this.pioneersService.move(gameIDStorage.getId(), action, x ,z ,y ,side , "settlement")
                                .observeOn(FX_SCHEDULER)
                                .subscribe(suc->{

                                });
                        this.view.setFill(Color.RED);
                    }else{
                        this.pioneersService.move(gameIDStorage.getId(), action, x ,z ,y ,side , "road")
                                .observeOn(FX_SCHEDULER)
                                .subscribe();
                        this.view.setFill(Color.RED);
                    }
                    System.out.println(myColor);
                });
    }

    public void setColor(int x, int y, int z, int side){
        if(this.x == x && this.y == y && this.z == z && this.side == side){
            this.view.setFill(Color.RED);
        }

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
        //System.out.println(myColor);
        //this.view.setFill(Color.GRAY);
        this.view.setRadius(10.0);
    }

    // Mouse leaves the field
    private void onFieldMouseHoverExit(MouseEvent event) {
        // Change the view
            if(!this.view.getFill().equals(Color.GRAY)) {
                //this.view.setFill(Color.WHITE);
                //this.view.setRadius(10.0);
            }
    }
}
