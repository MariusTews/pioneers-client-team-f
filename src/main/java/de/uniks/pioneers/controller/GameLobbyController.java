package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Constants;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.GameMembersService;
import de.uniks.pioneers.service.UserService;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class GameLobbyController implements Controller {

    private final ObservableList<Member> members = FXCollections.observableArrayList();

    @FXML
    public Text idTitle;
    @FXML
    public Button idLeaveButton;
    @FXML
    public ScrollPane idUserInLobby;
    @FXML
    public AnchorPane idMessageArea;
    @FXML
    public TextField idMessageField;
    @FXML
    public Button idSendButton;
    @FXML
    public Button idReadyButton;
    @FXML
    public Button idStartGameButton;
    @FXML
    public VBox idUserList;

    private final App app;
    private final GameMembersService gameMembersService;
    private final UserService userService;
    private final Provider<LobbyController> lobbyController;

    @Inject
    public GameLobbyController(App app,
                               GameMembersService gameMembersService,
                               UserService userService,
                               Provider<LobbyController> lobbyController) {
        this.app = app;
        this.gameMembersService = gameMembersService;
        this.userService = userService;
        this.lobbyController = lobbyController;
    }

    @Override
    public void init() {
        //TODO: remove later because of use of concrete variable for testGAME
        // get gameId from somewhere else
        gameMembersService
                .getAllGameMembers("6273d25f8800880014e65b84")
                .observeOn(FX_SCHEDULER)
                .subscribe(this.members::setAll);
    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        // load ui elements
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/GameLobbyScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;

        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // load game members
        //TODO: load game members
        members.addListener((ListChangeListener<? super Member>) c -> {
            idUserList.getChildren().removeAll();
            for(Member member: c.getList()) {
                idUserList.getChildren().add(constructUser(member));
            }
        });

        // disable start game button when entering lobby
        idStartGameButton.disableProperty().set(true);

        return parent;
    }

    public void leave(ActionEvent event) {
        final LobbyController controller = lobbyController.get();
        app.show(controller);
    }

    public void send(ActionEvent event) {
        //TODO: send a message to all
    }

    public void ready(ActionEvent event) {
    }

    public void startGame(ActionEvent event) {
    }

    // construct user by id
    private Label constructUser(Member member) {
        Label label = new Label();

        userService
                .getUser(member.userId())
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                    label.setText(result.name() + " - ready: " + member.ready());
                });

        return label;
    }
}
