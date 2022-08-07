package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Move;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.GameStorage;
import de.uniks.pioneers.service.PioneersService;
import de.uniks.pioneers.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class TradeAcceptSubcontroller implements Controller {
    @FXML
    public Button tradeButton;
    @FXML
    public Button declineButton;
    @FXML
    public VBox tradeUsers;

    private Stage primaryStage;

    private final ObservableList<User> tradingUsers = FXCollections.observableArrayList();

    private User tradePartner;

    private final HashMap<String, User> userHash = new HashMap<>();

    private final UserService userService;
    final PioneersService pioneersService;
    private final GameStorage gameStorage;

    private Move move;

    public TradeAcceptSubcontroller(UserService userService,
                                    PioneersService pioneersService,
                                    GameStorage gameStorage) {
        this.userService = userService;
        this.pioneersService = pioneersService;
        this.gameStorage = gameStorage;
    }

    @Override
    public void init() {
        userService
                .findAllUsers()
                .observeOn(FX_SCHEDULER)
                .subscribe(users -> {
                    for (User user : users) {
                        userHash.put(user.name(), user);
                    }
                });
    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/TradeAcceptSubView.fxml"));
        loader.setControllerFactory(c -> this);
        Parent root;
        try {
            root = loader.load();
            this.primaryStage = new Stage();
            Scene scene = new Scene(root, 200, 200);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Accept");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        tradeUsers.getChildren().add(renderUser());

        tradingUsers.addListener((ListChangeListener<? super User>) c -> tradeUsers.getChildren().addAll(c.getList().stream().map(user -> renderUser()).toList()));

        return root;
    }

    private Node renderUser() {
        Label label = new Label(tradingUsers.get(0).name());
        //need the id to click on the user in the test
        label.setId("tradePartnerLabel");
        label.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 1) {
                    label.setStyle("-fx-background-color: grey");
                    tradePartner = userHash.get(label.getText());
                }
            }
        });
        return label;
    }

    public void tradeButton() {
        // check if partner was selected
        if (tradePartner == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please choose a player to trade with");
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
                    .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
            alert.showAndWait();
        } else {
            pioneersService
                    .tradePlayer(gameStorage.getId(), "accept", tradePartner._id(), null)
                    .observeOn(FX_SCHEDULER)
                    .subscribe();
            primaryStage.close();
        }
    }

    public void declineTrade() {
        pioneersService
                .tradePlayer(gameStorage.getId(), "accept", null, move.resources())
                .observeOn(FX_SCHEDULER)
                .subscribe();
        primaryStage.close();
    }

    public void addUser(User user) {
        this.tradingUsers.add(user);
    }

    public void setMove(Move move) {
        this.move = move;
    }
}
