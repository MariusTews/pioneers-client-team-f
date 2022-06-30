package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.service.GameService;
import de.uniks.pioneers.service.GameStorage;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.PioneersService;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class WinnerController implements Controller {
    public Label winnerTitel;
    public Label winnerName;
    public Label loserTitel;
    public VBox loserBoxId;

    private HashMap<String, List<String>> userNumberPoints ;

    private final PioneersService pioneersService;

    private final GameStorage gameStorage;

    private final IDStorage idStorage;

    private final GameService gameService;
    private final Window owner;
    private Stage primaryStage;

    private App app;

    private final Provider<LobbyController> lobbyController;

    @Inject
    public WinnerController(HashMap<String, List<String>> userNumberPoints, Window window,
                            PioneersService pioneersService, GameStorage gameStorage, IDStorage idStorage,
                            GameService gameService, App app, Provider<LobbyController> lobbyController) {
        this.userNumberPoints = userNumberPoints;
        this.owner = window;
        this.pioneersService = pioneersService;
        this.gameStorage = gameStorage;
        this.idStorage = idStorage;
        this.gameService = gameService;
        this.app = app;
        this.lobbyController = lobbyController;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/WinnerController.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent root;
        try {
            root = loader.load();
            this.primaryStage = new Stage();
            // Set to UNDECORATED or TRANSPARENT (without white background) to remove minimize, maximize and close button of stage
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("view/stylesheets/winner.css")).toString());
            primaryStage.setScene(scene);
            primaryStage.setTitle("");
            // Specify modality of the new window: interactions are only possible on the second window
            primaryStage.initModality(Modality.WINDOW_MODAL);
            primaryStage.initOwner(this.owner);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        winnerTitel.setText("Winner");

        loserTitel.setText("Loser");

        //This updates VBox with respective points
        for (Map.Entry<String, List<String>> entry : userNumberPoints.entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();
            if(value.contains("10")){
                winnerName.setText(key);
                winnerName.setTextFill(Color.web(value.get(0)));

            }else {
                //changes for Labels for loser
                //needs to be taken here
                Label loserNames = new Label();
                loserNames.setText(key + "("+value.get(1)+")");
                loserNames.setTextFill(Color.web(value.get(0)));
                loserNames.setFont(new Font(22.0));
                this.loserBoxId.setAlignment(Pos.CENTER);
                this.loserBoxId.getChildren().add(loserNames);
            }
        }


        return root;
    }

    //this close the winner screen and the whole game Screen
    public void onClickCloseGame(ActionEvent event) {
        gameService.findOneGame(this.gameStorage.getId()).observeOn(FX_SCHEDULER).
                doOnError(e->{
                    this.gameStorage.setId(null);
                    this.app.show(lobbyController.get());
                }).
                subscribe( c ->{
                    if(c.owner().equals(this.idStorage.getID())){
                        gameService.
                                deleteGame(this.gameStorage.getId()).
                                observeOn(FX_SCHEDULER).
                                subscribe(onSuccess ->{
                                        //setting gameStorage to null will make sure
                                        //user cannot join the game
                                        this.gameStorage.setId(null);
                                        this.app.show(lobbyController.get());
                                });
                    }else{
                        this.gameStorage.setId(null);
                        this.app.show(lobbyController.get());
                    }
                });
        primaryStage.close();
    }
}
