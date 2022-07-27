package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.Message;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.*;
import de.uniks.pioneers.Websocket.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;

import static de.uniks.pioneers.Constants.*;


@SuppressWarnings("ResultOfMethodCallIgnored")
public class MessageViewSubController implements Controller {

    private final ObservableList<Message> messages = FXCollections.observableArrayList();
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final EventListener eventListener;
    private final GameStorage gameStorage;
    private final MemberIDStorage memberIDStorage;

    private final UserService userService;
    private final MemberService memberService;
    private final MessageService messageService;
    private final HashMap<String, User> userHash = new HashMap<>();
    private final HashMap<String, Member> memberHash = new HashMap<>();

    @FXML
    public ScrollPane idChatScrollPane;
    @FXML
    public TextField idMessageField;
    @FXML
    public Button idSendButton;
    @FXML
    public FlowPane idMessageView;

    @Inject
    public MessageViewSubController(EventListener eventListener,
                                    GameStorage gameStorage,
                                    UserService userService,
                                    MessageService messageService,
                                    MemberIDStorage memberIDStorage,
                                    MemberService memberService) {
        this.eventListener = eventListener;
        this.gameStorage = gameStorage;
        this.userService = userService;
        this.messageService = messageService;
        this.memberIDStorage = memberIDStorage;
        this.memberService = memberService;
    }

    @Override
    public void init() {
        // init userHash to access name and avatar by the given ID
        userService
                .findAllUsers()
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                    for (User user : result) {
                        this.userHash.put(user._id(), user);
                    }

                    // get all messages and initial load
                    messageService
                            .getAllMessages(GAMES, this.gameStorage.getId())
                            .observeOn(FX_SCHEDULER)
                            .subscribe(col -> {
                                this.messages.setAll(col);
                                this.initAllMessages();
                            });
                });

        // init. memberHash to not request the color of the member via REST every single time the messages are loaded
        memberService
                .getAllGameMembers(this.gameStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                    for (Member member : result) {
                        this.memberHash.put(member.userId(), member);
                    }
                });

        // listen to game lobby messages
        disposable.add(eventListener
                .listen("games." + this.gameStorage.getId() + ".messages.*.*", Message.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    final Message message = event.data();
                    if (event.event().endsWith(CREATED)) {
                        // New message will be added to the chat view
                        this.messages.add(message);
                        this.renderOneMessage(message);
                    } else if (event.event().endsWith(DELETED)) {
                        // Add message to list of deleted messages, so the message will not be rendered again
                        this.messages.removeIf(m -> m._id().equals(message._id()));
                        this.initAllMessages();
                    }
                }));

        // listen to game members, only relevant for the game lobby chat where color is chosen
        disposable.add(eventListener
                .listen("games." + this.gameStorage.getId() + ".members.*.*", Member.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    final Member member = event.data();
                    if (event.event().endsWith(UPDATED) || event.event().endsWith(CREATED)) {
                        // refresh the member saved in memberHash, so the color is updated
                        memberHash.put(member.userId(), member);
                        // load all messages again for coloring the username label with the new color
                        this.initAllMessages();
                    }
                }));
    }

    @Override
    public void destroy() {
        disposable.dispose();
    }

    @Override
    public Parent render() {
        // Show the chat with text field and send button, maybe a flag is needed so the color of the button
        // can be changed (different colors in lobby and in-game)
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/MessageSubView.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        this.idMessageView.setPrefWrapLength(270);

        return parent;
    }

    public void send() {
        this.checkMessageField();
    }

    // private methods
    // checkMessageField
    private void checkMessageField() {
        if (!this.idMessageField.getText().isEmpty()) {
            messageService
                    .send(GAMES, this.gameStorage.getId(), idMessageField.getText())
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

    // Renders one message by filling username with the chosen color, initializes mouse hover over message label
    // and right click option for deleting messages. Also adds the image of the user next to the username.
    private void renderOneMessage(Message m) {
        String currentHourAndMinute = getMessageTimeFormatted(m.createdAt());
        HBox box = new HBox(10);
        box.fillHeightProperty();
        // maybe a flag is needed, so the user's image will not be added in the in-game chat
        ImageView imageView = new ImageView();
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);
        if (this.userHash.get(m.sender()).avatar() != null) {
            imageView.setImage(new Image(this.userHash.get(m.sender()).avatar()));
        }
        box.getChildren().add(imageView);

        //member for checking whether it is spectator or not
        Member member = memberHash.get(this.userHash.get(m.sender())._id());

        // Label with message
        Label label = new Label();
        // Format the message label and display the whole message with line breaks
        label.setMinWidth(100);
        label.setMaxWidth(250);
        label.setWrapText(true);
        if(member.spectator()) {
            label.setText("\n" + "["+currentHourAndMinute+"]"+" "+m.body());
        }else {
            label.setText("[" + currentHourAndMinute + "]" + " " + m.body());
        }
        label.setTextFill(Color.WHITE);
        this.initRightClick(label, m._id(), m.sender());

        // Label with colored username
        Label nameLabel = coloredUsername(m);


        box.getChildren().addAll(nameLabel, label);
        this.idMessageView.getChildren().add(box);
    }
    // Option to delete the own message by right-clicking.
    // Fails when not sender of the message.
    private void initRightClick(Label label, String messageId, String sender) {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem menuItem = new MenuItem("delete");

        contextMenu.getItems().add(menuItem);

        label.setOnMouseEntered(event -> label.setStyle("-fx-background-color: rgba(255, 255, 255, .7)"));
        label.setOnMouseExited(event -> label.setStyle("-fx-background-color: TRANSPARENT"));
        label.setContextMenu(contextMenu);

        menuItem.setOnAction(event -> {
            if (sender.equals(this.memberIDStorage.getId())) {
                messageService
                        .delete(GAMES, this.gameStorage.getId(), messageId)
                        .observeOn(FX_SCHEDULER)
                        .subscribe();
                // the message is deleted from the chat view as soon as the listener detects DELETED in event
            } else {
                new Alert(Alert.AlertType.WARNING, "Deleting other members messages is not possible.")
                        .showAndWait();
            }
        });
    }

    // Initializing all messages is needed,
    // when the old messages have to be loaded again (f.e. change view to in-game)
    // Because of the delete function the VBox of the chat has to be cleared and loaded again
    // If the color of a user's name changes, the updated color will be displayed by loading all messages again
    private void initAllMessages() {
        if (this.idMessageView.getChildren() != null) {
            this.idMessageView.getChildren().clear();
        }
        for (Message m : this.messages) {
            if (m != null && !m.body().isEmpty()) {
                this.renderOneMessage(m);
            }
        }

        // scroll automatically to bottom
        this.idChatScrollPane.vvalueProperty().bind(idMessageView.heightProperty());
    }

    // Return the username label colored with the chosen color
    private Label coloredUsername(Message m) {
        //member for checking whether it is spectator or not
        Member member = memberHash.get(this.userHash.get(m.sender())._id());

        Label username = new Label();
        username.setMinWidth(50);
        // Set username as text, default is black which shall be white later
        if(member.spectator()) {
            username.setText("(Spectator) \n" + userHash.get(m.sender()).name() + ":");
        }else {
            username.setText(userHash.get(m.sender()).name() + ":");
        }

        // get the color of the game member from memberHash
        String color = memberHash.get(m.sender()).color();

        if (color != null) {
            // set the color of the username on user's chosen color
            username.setTextFill(Color.web(color));
        }
        return username;
    }

    private String getMessageTimeFormatted(String messageTime) {
        // Format messageCreatedAt to Hour and Minutes
        // Adapt TimeZone From UTC to LocalTimeZone Used on System
        Instant instant = Instant.parse(messageTime);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        return zonedDateTime.getHour() + ": " + zonedDateTime.getMinute();
    }
}
