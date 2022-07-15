package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Move;
import de.uniks.pioneers.service.GameStorage;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.PioneersService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.Constants.RESOURCES;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class TradeOfferSubcontroller implements Controller {
    @FXML
    public VBox offerContainer;
    @FXML
    public HBox tradeIconsContainer;
    @FXML
    public Label offerLabel;
    @FXML
    public Button acceptOfferButton;
    @FXML
    public Button declineOfferButton;

    private Stage primaryStage;
    private HashMap<String, Integer> resources;
    private final HashMap<String, String> images = new HashMap<>();
    private final PioneersService pioneersService;
    private final GameStorage gameStorage;
    private final IDStorage idStorage;
    private final Move move;

    public TradeOfferSubcontroller(Move move,
                                   PioneersService pioneersService,
                                   GameStorage gameStorage,
                                   IDStorage idStorage) {
        this.move = move;
        this.pioneersService = pioneersService;
        this.gameStorage = gameStorage;
        this.idStorage = idStorage;
    }

    @Override
    public void init() {
        // for dynamic loading of the icons
        images.put("lumber", "view/assets/earth_cactus.png");
        images.put("brick", "view/assets/mars_bar.png");
        images.put("ore", "view/assets/moon_rock.png");
        images.put("wool", "view/assets/neptun_crystals.png");
        images.put("grain", "view/assets/venus_grain.png");

        resources = move.resources();
    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/TradeOfferSubView.fxml"));
        loader.setControllerFactory(c -> this);
        Parent root;
        try {
            root = loader.load();
            this.primaryStage = new Stage();
            Scene scene = new Scene(root, 200, 200);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Offer");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // check, if player has the resources to trade and enable or disable the accept button
        pioneersService
                .findOnePlayer(gameStorage.getId(), idStorage.getID())
                .observeOn(FX_SCHEDULER)
                .subscribe(player -> {
                    int check = 0;
                    for (String res : move.resources().keySet()) {
                        if (move.resources().get(res) > 0) {
                            check++;
                            if (player.resources().get(res) != null) {
                                if (player.resources().get(res) >= move.resources().get(res)) {
                                    check--;
                                }
                            }
                        }
                    }
                    this.acceptOfferButton.disableProperty().set(check != 0);
                });

        this.acceptOfferButton.disableProperty().set(false);

        // icons with quantity for offer
        for (String res : RESOURCES) {
            if (resources.get(res) < 0) {
                addIcon(res, -1);
            }
        }

        tradeIconsContainer.getChildren().add(new Label(" : "));

        for (String res : RESOURCES) {
            if (resources.get(res) > 0) {
                addIcon(res, 1);
            }
        }

        return root;
    }

    public void addIcon(String res, int i) {
        VBox box = new VBox();
        ImageView imageView = new ImageView();
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        Image image = new Image(String.valueOf(Main.class.getResource(images.get(res))));
        imageView.setImage(image);
        box.getChildren().add(imageView);
        box.getChildren().add(new Label(String.valueOf(i * resources.get(res))));
        tradeIconsContainer.getChildren().add(box);
    }

    public void acceptOffer() {
        HashMap<String, Integer> tmp = new HashMap<>();
        for (String res : RESOURCES) {
            tmp.put(res, 0);
        }

        for (String res : move.resources().keySet()) {
            if (move.resources().get(res) != 0) {
                tmp.put(res, (-1) * move.resources().get(res));
            }
        }

        pioneersService
                .tradePlayer(gameStorage.getId(), "offer", null, tmp)
                .observeOn(FX_SCHEDULER)
                .subscribe();

        primaryStage.close();
    }

    public void declineOffer() {
        pioneersService
                .tradePlayer(gameStorage.getId(), "offer", null, null)
                .observeOn(FX_SCHEDULER)
                .subscribe();
        primaryStage.close();
    }
}
