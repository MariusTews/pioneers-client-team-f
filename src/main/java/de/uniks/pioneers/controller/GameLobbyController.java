package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Constants;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.Message;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.GameMembersService;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.MessageService;
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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

import static de.uniks.pioneers.Constants.*;

public class GameLobbyController implements Controller {

    private final ObservableList<Member> members = FXCollections.observableArrayList();
    private final ObservableList<Message> messages = FXCollections.observableArrayList();

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
    @FXML
    public VBox idMessageView;
    @FXML
    public ScrollPane idChatScrollPane;

    private final App app;
    private final GameMembersService gameMembersService;
    private final UserService userService;
    private final MessageService messageService;
    private final Provider<LobbyController> lobbyController;
    private final EventListener eventListener;
    private final IDStorage idStorage;

    @Inject
    public GameLobbyController(App app,
                               GameMembersService gameMembersService,
                               UserService userService,
                               MessageService messageService,
                               Provider<LobbyController> lobbyController,
                               EventListener eventListener,
                               IDStorage idStorage) {
        this.app = app;
        this.gameMembersService = gameMembersService;
        this.userService = userService;
        this.messageService = messageService;
        this.lobbyController = lobbyController;
        this.eventListener = eventListener;
        this.idStorage = idStorage;
    }

    @Override
    public void init() {

        //TODO: remove
        System.out.println("GameID: " + this.idStorage.getID());
        // get all game members
        gameMembersService
                .getAllGameMembers(this.idStorage.getID())
                .observeOn(FX_SCHEDULER)
                .subscribe(this.members::setAll);

        // get all messages
        messageService
                .getAllMessages(GAMES, this.idStorage.getID())
                .observeOn(FX_SCHEDULER)
                .subscribe(this.messages::setAll);

        // listen to game lobby messages
        eventListener
                .listen("games." + idStorage.getID() + ".*.*.*", Message.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    final Message message = event.data();
                   if (event.event().endsWith(CREATED)) {
                       messages.add(message);
                   } else if (event.event().endsWith(UPDATED)) {
                        // how do messages getting updated
                   } else if (event.event().endsWith(DELETED)) {
                       // implement later
                   }
                });

    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        // load UI elements
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
        /*members.addListener((ListChangeListener<? super Member>) c -> {
            idUserList.getChildren().removeAll();
            for(Member member: c.getList()) {
                idUserList.getChildren().add(renderUser(member));
            }
        });*/
        members.addListener((ListChangeListener<? super Member>) c -> {
            this.idUserList.getChildren().setAll(c.getList().stream().map(this::renderUser).toList());
        });

        // load and update game lobby messages
        messages.addListener((ListChangeListener<? super Message>) c -> {
            int indexLastElem = c.getList().size() - 1;
            Label label = new Label();
            userService.getUser(c.getList().get(indexLastElem).sender()).observeOn(FX_SCHEDULER).subscribe(result -> {
                label.setText(result.name() + ": " + c.getList().get(indexLastElem).body());
                idMessageView.getChildren().add(label);
                System.out.println("Send: " + label.getText());
            });
            //this.idChatScrollPane.setVvalue(1.0);
            this.idChatScrollPane.vvalueProperty().bind(idMessageView.heightProperty());
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
        this.checkMessageField();
    }

    // enter key for sending message
    public void enterKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            this.checkMessageField();
        }
    }

    public void ready(ActionEvent event) {
    }

    public void startGame(ActionEvent event) {
    }

    // private methods

    // checkMessageField
    private void checkMessageField() {
        if (!this.idMessageField.getText().isEmpty()) {
            messageService
                    .send(GAMES, idStorage.getID(), idMessageField.getText())
                    .subscribe();
            this.idMessageField.clear();
        }
    }

    // construct user by id
    private Label renderUser(Member member) {
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
