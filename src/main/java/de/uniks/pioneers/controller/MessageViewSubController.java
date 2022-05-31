package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.Message;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.GameIDStorage;
import de.uniks.pioneers.service.MemberIDStorage;
import de.uniks.pioneers.service.MessageService;
import de.uniks.pioneers.service.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.uniks.pioneers.Constants.*;


public class MessageViewSubController implements Controller {

    private final ObservableList<Message> messages = FXCollections.observableArrayList();
    private final ObservableList<Member> members = FXCollections.observableArrayList();
    private final List<String> deletedMessages = new ArrayList<>();
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final EventListener eventListener;
    private final GameIDStorage gameIDStorage;
    private final MemberIDStorage memberIDStorage;
    private final UserService userService;
    private final MessageService messageService;
    private final HashMap<String, User> memberHash = new HashMap<>();

    @FXML
    public ScrollPane idChatScrollPane;
    @FXML
    public TextField idMessageField;
    @FXML
    public Button idSendButton;
    @FXML
    public VBox idMessageView;

    private Parent parent;

    @Inject
    public MessageViewSubController(EventListener eventListener,
                                    GameIDStorage gameIDStorage,
                                    UserService userService,
                                    MessageService messageService,
                                    MemberIDStorage memberIDStorage) {
        this.eventListener = eventListener;
        this.gameIDStorage = gameIDStorage;
        this.userService = userService;
        this.messageService = messageService;
        this.memberIDStorage = memberIDStorage;
    }

    @Override
    public void init() {
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
        // Show the chat with text field and send button
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/MessageSubView.fxml"));
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

    public void send(ActionEvent event) {
        this.checkMessageField();
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

    public void enterKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            this.checkMessageField();
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
                    //this is responsible for showing messages
                    Label label2 = new Label();
                    label2.setMinWidth(this.idChatScrollPane.widthProperty().doubleValue()/4);
                    String color = null;
                    for (Member member: members ) {
                        if(member.userId().equals(m.sender())){
                            color = member.color();
                            break;
                        }
                    }
                    if(color != null) {
                        label2.setText(memberHash.get(m.sender()).name());
                        label2.setTextFill(Color.web(color));
                    } else {
                        label2.setText(memberHash.get(m.sender()).name());
                    }

                    //label2.setTextFill(Color.GREEN);
                    label.setText(": " + m.body());
                    //label.setTextFill(Color.GREEN);
                    box.getChildren().addAll(label2,label);
                    //box.getChildren().add(label2);
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

    // TODO: color the name of the user/member in the message
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
            this.initRightClick(label, m._id(), m.sender());
            //label.setMinWidth(this.idChatScrollPane.widthProperty().doubleValue());
            //this.initRightClick(label, m._id(), m.sender());
            //label.setText(memberHash.get(m.sender()).name() + ":" + m.body());
            //box.getChildren().add(label);
            Label label2 = new Label();
            label2.setMinWidth(this.idChatScrollPane.widthProperty().doubleValue()/4);
            String color = null;
            for (Member member: members ) {
                if(member.userId().equals(m.sender())){
                    color = member.color();
                    break;
                }
            }
            if(color != null){
                label2.setText(memberHash.get(m.sender()).name());
                label2.setTextFill(Color.web(color));
            }else {
                label2.setText(memberHash.get(m.sender()).name());
            }

            //label2.setTextFill(Color.GREEN);
            label.setText(":" + m.body());
            //label.setTextFill(Color.GREEN);
            box.getChildren().addAll(label2,label);
            //box.getChildren().add(label2);
            this.idMessageView.getChildren().add(box);
        }
    }

}
