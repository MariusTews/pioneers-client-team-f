package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.Message;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.*;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.uniks.pioneers.Constants.*;

public class GameLobbyController implements Controller {

    private final ObservableList<Member> members = FXCollections.observableArrayList();
    private final ObservableList<Message> messages = FXCollections.observableArrayList();
    private final List<String> deletedMessages = new ArrayList<>();
    private final HashMap<String, User> memberHash = new HashMap<>();

    @FXML
    public Label idTitleLabel;
    @FXML
    public Button idLeaveButton;
    @FXML
    public ScrollPane idUserInLobby;
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
    private final MemberService memberService;
    private final UserService userService;
    private final MessageService messageService;
    private final GameService gameService;
    private final Provider<LobbyController> lobbyController;
    private final EventListener eventListener;
    private final GameIDStorage gameIDStorage;
    private final MemberIDStorage memberIDStorage;
    private final IDStorage idStorage;
    private final CompositeDisposable disposable = new CompositeDisposable();



    @Inject
    public GameLobbyController(App app,
                               MemberService memberService,
                               UserService userService,
                               MessageService messageService,
                               GameService gameService,
                               Provider<LobbyController> lobbyController,
                               EventListener eventListener,
                               IDStorage idStorage,
                               GameIDStorage gameIDStorage,
                               MemberIDStorage memberIDStorage
                               ) {
        this.app = app;
        this.memberService = memberService;
        this.userService = userService;
        this.messageService = messageService;
        this.gameService = gameService;
        this.lobbyController = lobbyController;
        this.eventListener = eventListener;
        this.gameIDStorage = gameIDStorage;
        this.memberIDStorage = memberIDStorage;
        this.idStorage = idStorage;
    }

    @Override
    public void init() {
        // get all game members
        memberService
                .getAllGameMembers(this.gameIDStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(this.members::setAll);

        // init memberHash
        userService
                .findAllUsers()
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                    for (User user : result) {
                        this.memberHash.put(user._id(), user);
                    }

                    // get all messages and initial load
                    messageService
                            .getAllMessages(GAMES, this.gameIDStorage.getId())
                            .observeOn(FX_SCHEDULER)
                            .subscribe(col -> {
                                this.messages.setAll(col);
                                this.initAllMessages();
                            });
                });

        // listen to members
        disposable.add(eventListener
                .listen("games." + this.gameIDStorage.getId() + ".members.*.*", Member.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    final Member member = event.data();
                    if (event.event().endsWith(CREATED)) {
                        members.add(member);
                    } else if (event.event().endsWith(DELETED)) {
                        members.remove(member);
                        if (member.userId().equals(idStorage.getID())) {
                            app.show(lobbyController.get());
                        }

                    } else if (event.event().endsWith(UPDATED)) {
                        for (Member updatedMember : this.members) {
                            if (updatedMember.userId().equals(member.userId())) {
                                this.members.set(this.members.indexOf(updatedMember),member);
                                break;
                            }
                        }
                        int readyMembers = 0;
                        for (Member members : this.members) {
                            if (members.ready()) {
                               readyMembers +=1;
                            }
                        }
                        this.idStartGameButton.disableProperty().set(readyMembers < 2 || readyMembers != members.size());


                    }
                }));

        // listen to game lobby messages
        disposable.add(eventListener
                .listen("games." + this.gameIDStorage.getId() + ".messages.*.*", Message.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    final Message message = event.data();
                    if (event.event().endsWith(CREATED)) {
                        renderMessage(message, true);
                    } else if (event.event().endsWith(DELETED)) {
                        renderMessage(message, false);
                    }
                }));

    }

    @Override
    public void destroy() {
        disposable.dispose();
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

        // load game title
        gameService
                .findOneGame(this.gameIDStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> this.idTitleLabel.setText("Welcome to " + result.name()));

        // load game members
        members.addListener((ListChangeListener<? super Member>) c -> {
            this.idUserList.getChildren().setAll(c.getList().stream().map(this::renderMember).toList());
        });

        // disable start button when entering game lobby
        idStartGameButton.disableProperty().set(true);

        return parent;
    }

    public void leave(ActionEvent event) {
        gameService
                .findOneGame(gameIDStorage.getId())
                        .observeOn(FX_SCHEDULER)
                                .subscribe(result -> {
                                    if ((int)result.members() == 1 || result.owner().equals(idStorage.getID())) {
                                        gameService
                                                .deleteGame(gameIDStorage.getId())
                                                .observeOn(FX_SCHEDULER)
                                                .subscribe(onSuccess -> app.show(lobbyController.get()), onError -> {});
                                    } else {
                                        memberService
                                                .leave(gameIDStorage.getId(), idStorage.getID())
                                                .observeOn(FX_SCHEDULER)
                                                .subscribe(onSuccess -> app.show(lobbyController.get()), onError -> {});
                                                }
                                    });
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
        Member member = memberService.findOne(gameIDStorage.getId(), idStorage.getID()).blockingFirst();
        if(member.ready()){
            memberService.statusUpdate(gameIDStorage.getId(),idStorage.getID(),false, member.color()).subscribe();
            this.idReadyButton.setText("Ready");
        }else {
            memberService.statusUpdate(gameIDStorage.getId(),idStorage.getID(), true, member.color()).subscribe();
            this.idReadyButton.setText("Not Ready");
        }

    }

    public void startGame(ActionEvent event) {
    }

    // private methods
    // checkMessageField
    private void checkMessageField() {
        if (!this.idMessageField.getText().isEmpty()) {
            messageService
                    .send(GAMES, this.gameIDStorage.getId(), idMessageField.getText())
                    .observeOn(FX_SCHEDULER)
                    .subscribe();
            this.idMessageField.clear();
        }
    }

    private Node renderMember(Member member) {
        MemberListSubcontroller memberListSubcontroller = new MemberListSubcontroller(this.app, member, this.userService);
        return memberListSubcontroller.render();
    }

    private void initAllMessages() {
        for (Message m : this.messages) {
            HBox box = new HBox(3);
            Label label = new Label();
            ImageView imageView = new ImageView();
            imageView.setFitWidth(20);
            imageView.setFitHeight(20);
            if (this.memberHash.get(m.sender()).avatar() != null) {
                imageView.setImage(new Image(this.memberHash.get(m.sender()).avatar()));
            }
            box.getChildren().add(imageView);
            label.setMinWidth(this.idChatScrollPane.widthProperty().doubleValue());
            this.initRightClick(label, m._id(), m.sender());
            label.setText(memberHash.get(m.sender()).name() + ": " + m.body());
            box.getChildren().add(label);
            this.idMessageView.getChildren().add(box);
        }
    }

    /*
     * Render message for add and delete
     * save id in deleted messages
     * if deleted messages contains id, create different label
     * and make label not right clickable
     * */
    private void renderMessage(Message message, Boolean render) {
        this.idMessageView.getChildren().clear();

        if (render) {
            this.messages.add(message);
        } else {
            this.deletedMessages.add(message._id());
        }

        if (!messages.isEmpty()) {
            for (Message m : messages) {
                HBox box = new HBox(3);
                ImageView imageView = new ImageView();
                imageView.setFitWidth(20);
                imageView.setFitHeight(20);
                if (this.memberHash.get(m.sender()).avatar() != null) {
                    imageView.setImage(new Image(this.memberHash.get(m.sender()).avatar()));
                }
                box.getChildren().add(imageView);
                if (this.deletedMessages.contains(m._id())) {
                    Label label = new Label(this.memberHash.get(m.sender()).name() + ": - message deleted - ");
                    label.setFont(Font.font("Italic"));
                    box.getChildren().add(label);
                    this.idMessageView.getChildren().add(box);
                } else {
                    Label label = new Label();
                    label.setMinWidth(this.idChatScrollPane.widthProperty().doubleValue());
                    this.initRightClick(label, m._id(), m.sender());
                    label.setText(memberHash.get(m.sender()).name() + ": " + m.body());
                    box.getChildren().add(label);
                    this.idMessageView.getChildren().add(box);
                }
            }
        }
        // scroll automatically to bottom
        this.idChatScrollPane.vvalueProperty().bind(idMessageView.heightProperty());
    }

    private void initRightClick(Label label, String messageId, String sender) {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem menuItem = new MenuItem("delete");

        contextMenu.getItems().add(menuItem);

        label.setOnMouseEntered(event -> {
            label.setStyle("-fx-background-color: LIGHTGREY");
        });
        label.setOnMouseExited(event -> {
            label.setStyle("-fx-background-color: DEFAULT");
        });
        label.setContextMenu(contextMenu);

        menuItem.setOnAction(event -> {
            if (sender.equals(this.memberIDStorage.getId())) {
                messageService
                        .delete(GAMES, this.gameIDStorage.getId(), messageId)
                        .observeOn(FX_SCHEDULER)
                        .subscribe();
                this.idMessageView.getChildren().remove(label);
            } else {
                new Alert(Alert.AlertType.WARNING, "Deleting other members messages is not possible.")
                        .showAndWait();
            }
        });
    }
}
