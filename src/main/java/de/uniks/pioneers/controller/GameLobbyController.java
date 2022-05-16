package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.Message;
import de.uniks.pioneers.service.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

import static de.uniks.pioneers.Constants.*;

public class GameLobbyController implements Controller {

    private final ObservableList<Member> members = FXCollections.observableArrayList();
    private final ObservableList<Message> messages = FXCollections.observableArrayList();

    @FXML
    public Label idTitleLabel;
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
    private final GameIDStorage gameIDStorage;
    private final MemberIDStorage memberIDStorage;

    @Inject
    public GameLobbyController(App app,
                               GameMembersService gameMembersService,
                               UserService userService,
                               MessageService messageService,
                               Provider<LobbyController> lobbyController,
                               EventListener eventListener,
                               GameIDStorage gameIDStorage,
                               MemberIDStorage memberIDStorage) {
        this.app = app;
        this.gameMembersService = gameMembersService;
        this.userService = userService;
        this.messageService = messageService;
        this.lobbyController = lobbyController;
        this.eventListener = eventListener;
        this.gameIDStorage = gameIDStorage;
        this.memberIDStorage = memberIDStorage;
    }

    @Override
    public void init() {
        // get all game members
        gameMembersService
                .getAllGameMembers(this.gameIDStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(this.members::setAll);

        // get all messages
        messageService
                .getAllMessages(GAMES, this.gameIDStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(this.messages::setAll);

        // listen to members
        eventListener
                .listen("games." + this.gameIDStorage.getId() + ".members.*.*", Member.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    final Member member = event.data();
                    if (event.event().endsWith(CREATED)) {
                        members.add(member);
                    }
                });

        // listen to game lobby messages
        eventListener
                .listen("games." + this.gameIDStorage.getId() + ".messages.*.*", Message.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    final Message message = event.data();
                    if (event.event().endsWith(CREATED)) {
                        renderMessage(message, true);
                    } else if (event.event().endsWith(DELETED)) {
                        renderMessage(message, false);
                    }
                });
    }

    @Override
    public void destroy() {
        eventListener
                .listen("games." + this.gameIDStorage.getId() + ".members.*.*", Member.class)
                .unsubscribeOn(FX_SCHEDULER);
        eventListener
                .listen("games." + this.gameIDStorage.getId() + ".messages.*.*", Message.class)
                .unsubscribeOn(FX_SCHEDULER);
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
        members.addListener((ListChangeListener<? super Member>) c -> {
            this.idUserList.getChildren().setAll(c.getList().stream().map(this::renderMember).toList());
        });

        // load and update game lobby messages
        //messages.addListener((ListChangeListener<? super Message>) c -> {
            // experiment
            //this.idMessageView.getChildren().addAll(c.getList().stream().map(this::renderMessage).toList());

            /*for(Message message : messages) {
                Label label = new Label();
                label.setMinWidth(this.idChatScrollPane.widthProperty().doubleValue());
                this.initRightClick(label, message._id(), message.sender());
                label.setOnMouseEntered(event -> {
                    label.setStyle("-fx-background-color: LIGHTGREY");
                });
                label.setOnMouseExited(event -> {
                    label.setStyle("-fx-background-color: DEFAULT");
                });
                userService.findOne(message.sender()).observeOn(FX_SCHEDULER).subscribe(result -> {
                    label.setText(result.name() + ": " + message.body());
                    idMessageView.getChildren().add(label);
                });
            }
            this.idChatScrollPane.vvalueProperty().bind(idMessageView.heightProperty());*/
            //

            /*int indexLastElem = c.getList().size() - 1;
            Label label = new Label();
            label.setMinWidth(this.idChatScrollPane.widthProperty().doubleValue());
            // make message right clickable if current member is also the sender
            if (c.getList().get(indexLastElem).sender().equals(this.memberIDStorage.getId())) {
                this.initRightClick(label, c.getList().get(indexLastElem)._id());
            }
            label.setOnMouseEntered(event -> {
                label.setStyle("-fx-background-color: LIGHTGREY");
            });
            label.setOnMouseExited(event -> {
                label.setStyle("-fx-background-color: DEFAULT");
            });
            userService.findOne(c.getList().get(indexLastElem).sender()).observeOn(FX_SCHEDULER).subscribe(result -> {
                label.setText(result.name() + ": " + c.getList().get(indexLastElem).body());
                idMessageView.getChildren().add(label);
            });
            this.idChatScrollPane.vvalueProperty().bind(idMessageView.heightProperty());*/
        //});

        // disable start game button when entering the GameLobby
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
                    .send(GAMES, this.gameIDStorage.getId(), idMessageField.getText())
                    .subscribe();
            this.idMessageField.clear();
        }
    }

    private Node renderMember(Member member) {
        MemberListSubcontroller memberListSubcontroller = new MemberListSubcontroller(this.app, member, this.userService);
        return memberListSubcontroller.render();
    }

    //TODO: observe
    private void renderMessage(Message message, Boolean render) {
        this.idMessageView.getChildren().clear();
        if (render) {
            this.messages.add(message);
        } else {
            this.messages.remove(message);
        }
        if (!messages.isEmpty()) {
            for (Message m : messages) {
                Label label = new Label();
                label.setMinWidth(this.idChatScrollPane.widthProperty().doubleValue());
                this.initRightClick(label, m._id(), m.sender());
                label.setOnMouseEntered(event -> {
                    label.setStyle("-fx-background-color: LIGHTGREY");
                });
                label.setOnMouseExited(event -> {
                    label.setStyle("-fx-background-color: DEFAULT");
                });
            /*userService
                    .findOne(m.sender())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(result -> {
                        label.setText(result.name() + ": " + m.body());
                        this.idMessageView.getChildren().add(label);
                    });*/
                label.setText(m.sender() + ": " + m.body());
                this.idMessageView.getChildren().add(label);
            }
        }
    }

    //TODO: messageService delete
    // cut off username and delete threw service
    // maybe replace with new label and the text that this message where deleted
    private void initRightClick(Label label, String messageId, String sender) {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem menuItem = new MenuItem("delete");

        contextMenu.getItems().add(menuItem);
        label.setContextMenu(contextMenu);

        menuItem.setOnAction(event -> {
            if (sender.equals(this.memberIDStorage.getId())) {
                messageService
                        .delete(GAMES, this.gameIDStorage.getId(), messageId)
                        .observeOn(FX_SCHEDULER)
                        .subscribe();
                this.idMessageView.getChildren().remove(label);
            }
        });

    }
}
