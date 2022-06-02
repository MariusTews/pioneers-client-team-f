package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.Message;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.uniks.pioneers.Constants.*;

public class GameLobbyController implements Controller {

    private final ObservableList<Member> members = FXCollections.observableArrayList();

    @FXML
    public Label idTitleLabel;
    @FXML
    public Button idLeaveButton;
    @FXML
    public ScrollPane idUserInLobby;
    @FXML
    public Button idReadyButton;
    @FXML
    public Button idStartGameButton;
    @FXML
    public VBox idUserList;
    @FXML
    public VBox idChatContainer;

    private final App app;
    private final MemberService memberService;
    private final UserService userService;
    private final MessageService messageService;
    private final GameService gameService;
    private final Provider<LobbyController> lobbyController;
    private final Provider<GameScreenController> gameScreenController;
    private MessageViewSubController messageViewSubController;
    private MemberListSubcontroller memberListSubcontroller;
    private final EventListener eventListener;
    private final GameIDStorage gameIDStorage;
    private final MemberIDStorage memberIDStorage;
    private final IDStorage idStorage;
    private final CompositeDisposable disposable = new CompositeDisposable();

    @FXML
    public ComboBox<String> colorPicker;
    //this will allow change the status if user is ready
    public boolean ready_button = false;


    @Inject
    public GameLobbyController(App app,
                               MemberService memberService,
                               UserService userService,
                               MessageService messageService,
                               GameService gameService,
                               Provider<LobbyController> lobbyController,
                               Provider<GameScreenController> gameScreenController,
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
        this.gameScreenController = gameScreenController;
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

        // listen to members
        disposable.add(eventListener
                .listen("games." + this.gameIDStorage.getId() + ".members.*.*", Member.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    final Member member = event.data();
                    if (event.event().endsWith(CREATED)) {
                        if (!members.contains(member)) {
                            members.add(member);
                        }
                    } else if (event.event().endsWith(DELETED)) {
                        members.remove(member);
                        if (member.userId().equals(idStorage.getID())) {
                            app.show(lobbyController.get());
                        }

                    } else if (event.event().endsWith(UPDATED)) {
                        for (Member updatedMember : this.members) {
                            if (updatedMember.userId().equals(member.userId())) {
                                this.members.set(this.members.indexOf(updatedMember), member);
                                break;
                            }
                        }
                        int readyMembers = 0;
                        for (Member members : this.members) {
                            if (members.ready()) {
                                readyMembers += 1;
                            }
                        }
                        this.idStartGameButton.disableProperty().set(readyMembers < 2 || readyMembers != members.size());
                    }
                }));

        //listen to the game
        disposable.add(eventListener
                .listen("games." + this.gameIDStorage.getId() + ".*.*", Message.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    if (event.event().endsWith("state" + CREATED)) {
                        final GameScreenController controller = gameScreenController.get();
                        this.app.show(controller);
                    }
                }));

    }

    @Override
    public void destroy() {
        // destroy sub controller, otherwise the messages are displayed twice in-game, because the game controller
        // creates a new messageViewSubController
        if (this.messageViewSubController != null) {
            this.messageViewSubController.destroy();
        }
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

        //TODO: load game title WIP SERVERREQUEST LIMIT
        gameService
                .findOneGame(this.gameIDStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> this.idTitleLabel.setText("Welcome to " + result.name()));

        // load game members
        members.addListener((ListChangeListener<? super Member>) c -> this.idUserList.getChildren()
                .setAll(c.getList().stream().map(this::renderMember).toList()));

        addColorOnComboBox(colorPicker);
        // disable start button when entering game lobby
        idStartGameButton.disableProperty().set(true);

        // initialize sub-controller, so the disposable in sub-controller listens to incoming/outgoing messages
        this.messageViewSubController = new MessageViewSubController(eventListener, gameIDStorage,
                userService, messageService, memberIDStorage, memberService);
        messageViewSubController.init();
        // show chat and load the messages
        idChatContainer.getChildren().setAll(messageViewSubController.render());

        return parent;
    }

    public void leave(ActionEvent ignoredEvent) {
        gameService
                .findOneGame(gameIDStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                    if ((int) result.members() == 1 || result.owner().equals(idStorage.getID())) {
                        gameService
                                .deleteGame(gameIDStorage.getId())
                                .observeOn(FX_SCHEDULER)
                                .subscribe(onSuccess -> app.show(lobbyController.get()), onError -> {
                                });
                    } else {
                        memberService
                                .leave(gameIDStorage.getId(), idStorage.getID())
                                .observeOn(FX_SCHEDULER)
                                .subscribe(onSuccess -> app.show(lobbyController.get()), onError -> {
                                });
                    }
                });
    }

    public void ready(ActionEvent ignoredEvent) {
        for (Member member: this.members) {
            if (member.userId().equals(idStorage.getID())){
                if (member.ready()) {
                    memberService.statusUpdate(gameIDStorage.getId(), idStorage.getID(), false, member.color()).subscribe();
                    this.idReadyButton.setText("Ready");
                } else {
                    memberService.statusUpdate(gameIDStorage.getId(), idStorage.getID(), true, member.color()).subscribe();
                    this.idReadyButton.setText("Not Ready");
                }
            }
        }
    }

    public void startGame(ActionEvent ignoredEvent) {

        gameService.updateGame(gameIDStorage.getId(), null, null, null, true)
                .observeOn(FX_SCHEDULER)
                .doOnError(error -> {
                    if ("HTTP 403 ".equals(error.getMessage())) {
                        new Alert(Alert.AlertType.INFORMATION, "only the owner can start the game!")
                                .showAndWait();
                    }
                })
                .subscribe(onSuccess -> {
                    final GameScreenController controller = gameScreenController.get();
                    this.app.show(controller);
                }, onError -> {
                });

    }

    private Node renderMember(Member member) {
        this.memberListSubcontroller = new MemberListSubcontroller(this.app, member, this.userService);
        return memberListSubcontroller.render();
    }

    private void addColorOnComboBox(ComboBox comboBox) {
        comboBox.setPromptText("Select Color");
        //get key and value
        HashMap<String, String> color_to_hex = colorToHexcode(color());
        List<String> leftColor = remainingColor(color_to_hex);
        //addToCombox(comboBox,leftColor);
        comboBox.getItems().addAll(color());
    }

    //this makes sure duplicate dones not come
    //into combox
    private void addToCombox(ComboBox comboBox, List<String> leftColor) {
        for (String color : leftColor) {
            if (!comboBox.getItems().contains(color)) {
                comboBox.getItems().add(color);
            }
        }
    }

    //Get all the color from members
    private List<String> remainingColor(HashMap<String, String> colortoHex) {
        List<String> remaining_color = new ArrayList<>();
        List<String> setOfColors = color();

        for (Member member : members) {
            if (colortoHex.containsKey(member.color())) {
                remaining_color.add(colortoHex.get(member.color()));
            }
        }

        for (String color : remaining_color) {
            if (setOfColors.contains(color)) {
                setOfColors.remove(color);
            }
        }
        return setOfColors;
    }

    //This maps every color to its hexcode BLUE:#0000FF
    private HashMap<String, String> colorToHexcode(List<String> list) {
        HashMap<String, String> color_to_hexcode = new HashMap<>();
        for (String E : list) {
            Color c = Color.web(E.toLowerCase());
            String pickedColor = "#" + c.toString().substring(2, 8);
            color_to_hexcode.put(pickedColor, E);
        }
        return color_to_hexcode;
    }

    //List of colors
    private List<String> color() {
        List<String> color = new ArrayList<>();
        color.add("RED");
        color.add("BLUE");
        color.add("GREEN");
        color.add("ORANGE");
        color.add("YELLOW");
        color.add("VIOLET");

        return color;
    }

    //color event
    public void colorPicked(ActionEvent ignoredEvent) {
        Color c = Color.web(colorPicker.getSelectionModel().getSelectedItem().toLowerCase());
        String pickedColor = "#" + c.toString().substring(2, 8);

        boolean chose = true;
        boolean ready = false;
        List<Member> memberList = memberService.getAllGameMembers(gameIDStorage.getId()).blockingFirst();
        for (Member member: memberList) {
            if (member.color() != null && member.color().equals(pickedColor)) {
                chose = false;
            }
            if (member.userId().equals(idStorage.getID())) {
                ready = member.ready();
            }
        }
        if (chose) {
            memberService.statusUpdate(gameIDStorage.getId(), idStorage.getID(), ready, pickedColor).
                    observeOn(FX_SCHEDULER).subscribe();
        }
    }
}
