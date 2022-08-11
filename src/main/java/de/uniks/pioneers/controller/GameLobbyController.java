package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.computation.GameLobbyInformation;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.Message;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.*;
import de.uniks.pioneers.websocket.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.Objects;

import static de.uniks.pioneers.Constants.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class GameLobbyController implements Controller {

    private final ObservableList<Member> members = FXCollections.observableArrayList();

    private final ObservableList<Member> spectatorMember = FXCollections.observableArrayList();

    private final ObservableList<User> playerList = FXCollections.observableArrayList();

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
    @SuppressWarnings("CanBeFinal")
    @FXML
    public ComboBox<Label> colorPicker = new ComboBox<>();
    @FXML
    public Label settingsLabel;
    @FXML
    public ScrollPane idUserInLobby1;
    @FXML
    public Label spectatorId;
    @FXML
    public CheckBox checkBoxId;

    private final App app;
    private final MemberService memberService;
    private final UserService userService;
    private final MessageService messageService;
    private final GameService gameService;
    private final Provider<LobbyController> lobbyController;
    private final Provider<GameScreenController> gameScreenController;
    //Button for spectator
    public VBox spectatorViewId;
    //player Numbers Label
    public Label playersNumberId;
    //player LabelID
    public Label spectatorLabelId;

    private MessageViewSubController messageViewSubController;
    private final EventListener eventListener;
    private final GameStorage gameStorage;
    private final MemberIDStorage memberIDStorage;
    private final IDStorage idStorage;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final UserStorage userStorage;
    private Game game;
    private boolean started = false;

    private final GameLobbyInformation gameLobbyInformation = new GameLobbyInformation();

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
                               GameStorage gameStorage,
                               MemberIDStorage memberIDStorage,
                               UserStorage userStorage) {
        this.app = app;
        this.memberService = memberService;
        this.userService = userService;
        this.messageService = messageService;
        this.gameService = gameService;
        this.lobbyController = lobbyController;
        this.gameScreenController = gameScreenController;
        this.eventListener = eventListener;
        this.gameStorage = gameStorage;
        this.memberIDStorage = memberIDStorage;
        this.idStorage = idStorage;
        this.userStorage = userStorage;
    }

    @Override
    public void init() {
        // get all game members
        memberService
                .getAllGameMembers(this.gameStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(col -> {
                    this.members.setAll(col);
                    int ready = 0;
                    for (Member member : members) {
                        if (member.ready()) {
                            ready++;
                        }
                    }
                    if (ready >= 2) {
                        idStartGameButton.disableProperty().set(false);
                    }
                    this.initPlayerList();
                });

        // listen to members
        disposable.add(eventListener
                .listen("games." + this.gameStorage.getId() + ".members.*.*", Member.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    final Member member = event.data();
                    if (event.event().endsWith(CREATED)) {
                        if (!members.contains(member)) {
                            members.add(member);
                            userService.findOne(member.userId())
                                    .observeOn(FX_SCHEDULER)
                                    .subscribe(this.playerList::add);

                        }
                    } else if (event.event().endsWith(DELETED)) {
                        this.deleteMember(member);
                    } else if (event.event().endsWith(UPDATED)) {
                        this.updateMember(member);
                    }
                }));

        //listen to the game
        disposable.add(eventListener
                .listen("games." + this.gameStorage.getId() + ".*.*", Message.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    if (event.event().endsWith("state" + CREATED)) {
                        this.userStorage.setUserList(playerList);
                        // flag needed, otherwise the gameScreenController is initialized twice
                        if (!started) {
                            started = true;
                            gameLobbyInformation.changeView(gameScreenController, idStartGameButton, app);
                        }
                    }
                }));

        // initialize sub-controller, so the disposable in sub-controller listens to incoming/outgoing messages
        this.messageViewSubController = new MessageViewSubController(eventListener, gameStorage,
                userService, messageService, memberIDStorage, memberService, null);
        messageViewSubController.init();
        //action when the screen is closed
        this.app.getStage().setOnCloseRequest(e -> {
            gameService
                    .findOneGame(gameStorage.getId())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(this::actionOnCloseScreen);
            e.consume();
        });
    }

    @Override
    public void destroy() {
        // destroy sub controller, otherwise the messages are displayed twice in-game, because the game controller
        // creates a new messageViewSubController
        if (this.messageViewSubController != null) {
            this.messageViewSubController.destroy();
        }
        disposable.clear();
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

        gameService
                .findOneGame(this.gameStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                    this.game = result;
                    this.gameStorage.setGameOptions(result.settings());
                    int victoryPoints = result.settings().victoryPoints();
                    int mapRadius = result.settings().mapRadius();
                    this.settingsLabel.setText("Settings: Map Size = " + mapRadius + "  Required VP = " + victoryPoints);
                    this.idTitleLabel.setText("Welcome to " + this.game.name());
                });

        // load game members

        this.idUserList.getChildren().setAll(members.stream().map(m -> gameLobbyInformation.renderMember(m, playerList,
                playersNumberId, members)).toList());
        playerList.addListener((ListChangeListener<? super User>) c -> this.idUserList.getChildren().setAll(
                members.stream().map(m -> gameLobbyInformation.renderMember(m, playerList,
                        playersNumberId, members)).toList()));

        gameLobbyInformation.addColourOnComboBox(colorPicker);

        this.spectatorViewId.getChildren().setAll(spectatorMember.stream().map(m -> gameLobbyInformation.renderSpectatorMember(m, playerList)).toList());
        playerList.addListener((ListChangeListener<? super User>) c -> this.spectatorViewId.getChildren().setAll(
                spectatorMember.stream().map(m -> gameLobbyInformation.renderSpectatorMember(m, playerList)).toList()));

        // disable start button when entering game lobby
        idStartGameButton.disableProperty().set(true);

        // show chat and load the messages
        idChatContainer.getChildren().setAll(messageViewSubController.render());

        return parent;
    }

    // initPlayerList, deleteMember and updateMember have to be outsourced in a service class in 4th release.
    private void initPlayerList() {
        this.userService.findAllUsers()
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    for (User user : event) {
                        for (Member member : members) {
                            if (user._id().equals(member.userId())) {
                                this.playerList.add(user);
                            }
                        }
                    }
                    //pushes all the users to spectator
                    //if the player number is greater than 6
                    //this allows unlimited number of spectator.
                    if (this.members.size() > MAX_MEMBERS) {
                        for (Member member : members) {
                            if (member.userId().equals(this.idStorage.getID())
                                    && member.gameId().equals(this.gameStorage.getId())) {
                                memberService.statusUpdate(member.gameId(), member.userId(), true, "#000000", true)
                                        .observeOn(FX_SCHEDULER).subscribe(e -> {
                                            //makes ready button invisible
                                            this.idReadyButton.setText("Not Ready");
                                            this.idReadyButton.setDisable(true);
                                            this.colorPicker.setDisable(true);
                                        });
                                break;
                            }
                        }
                    }
                });
    }

    private void deleteMember(Member member) {
        if (members.contains(member)) {
            members.remove(member);
        } else {
            spectatorMember.remove(member);
        }
        if (member.userId().equals(idStorage.getID())) {
            app.show(lobbyController.get());
        }
        clearAll();

        //make sure if members are less than max number, checkbox is visible
        if (members.size() < MAX_MEMBERS) {
            checkBoxId.disableProperty().set(false);
        }
    }

    private void clearAll() {
        this.idUserList.getChildren().clear();
        this.idUserList.getChildren().setAll(members.stream().map(m -> gameLobbyInformation.renderMember(m, playerList,
                playersNumberId, members)).toList());
        this.spectatorViewId.getChildren().clear();
        this.spectatorViewId.getChildren().setAll(spectatorMember.stream().map(m -> gameLobbyInformation.renderSpectatorMember(m, playerList)).toList());
    }

    private void updateMember(Member member) {
        for (Member updatedMember : this.members) {
            if (updatedMember.userId().equals(member.userId()) && member.spectator()) {
                spectatorMember.add(member);
                members.remove(updatedMember);
                this.playersNumberId.setText("Players " + members.size() + "/6");
                break;
            } else if (updatedMember.userId().equals(member.userId())) {
                this.members.set(this.members.indexOf(updatedMember), member);
                break;
            }
        }

        for (Member upSpectatorMember : this.spectatorMember) {
            if (upSpectatorMember.userId().equals(member.userId()) && !member.spectator()) {
                spectatorMember.remove(upSpectatorMember);
                members.add(member);
                this.playersNumberId.setText("Players " + members.size() + "/6");
                break;
            }
        }
        int readyMembers = 0;
        for (Member members : this.members) {
            if (members.ready()) {
                readyMembers += 1;
            }
        }
        for (Member member1 : this.spectatorMember) {
            if (member1.ready()) {
                readyMembers += 1;
            }
        }
        this.idStartGameButton.disableProperty().set(readyMembers < 1 || readyMembers != members.size()
                + spectatorMember.size());// || members.size() == 0);

        //checks if combobox has been clicked,if not
        //then automatically picks color for the user.
        if (colorPicker.getSelectionModel().isEmpty()) {
            if (readyMembers == members.size() + spectatorMember.size()) {
                gameLobbyInformation.giveYourSelfColour(members, memberService, idStorage);
                //giveYourselfColor();
            }
        }

        //deactivate checkbox if maximum member has been reached
        checkBoxId.disableProperty().set(members.size() == MAX_MEMBERS);

        clearAll();
    }

    //This makes sure the user is offline
    // and is not  part of the game anymore.
    private void actionOnCloseScreen(Game result) {
        if ((int) result.members() == 1 || result.owner().equals(idStorage.getID())) {
            gameService
                    .deleteGame(gameStorage.getId())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(e -> {
                    });
        } else {
            memberService
                    .leave(gameStorage.getId(), idStorage.getID())
                    .observeOn(FX_SCHEDULER)
                    .subscribe();
        }

        userService.statusUpdate(this.idStorage.getID(), "offline").
                observeOn(FX_SCHEDULER)
                .subscribe(e -> System.exit(0));
    }

    public void leave() {
        gameService
                .findOneGame(gameStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                    if ((int) result.members() == 1 || result.owner().equals(idStorage.getID())) {
                        gameService
                                .deleteGame(gameStorage.getId())
                                .observeOn(FX_SCHEDULER)
                                .subscribe(onSuccess -> app.show(lobbyController.get()), onError -> {
                                });
                    } else {
                        memberService
                                .leave(gameStorage.getId(), idStorage.getID())
                                .observeOn(FX_SCHEDULER)
                                .subscribe(onSuccess -> app.show(lobbyController.get()), onError -> {
                                });
                    }
                });
    }

    public void ready() {
        for (Member member : this.members) {
            if (member.userId().equals(idStorage.getID())) {
                if (member.ready()) {
                    memberService.statusUpdate(gameStorage.getId(), idStorage.getID(), false, member.color(), member.spectator()).subscribe();
                    this.idReadyButton.setText("Ready");
                } else {
                    memberService.statusUpdate(gameStorage.getId(), idStorage.getID(), true, member.color(), member.spectator()).subscribe();
                    this.idReadyButton.setText("Not Ready");
                }
            }
        }
    }

    public void startGame() {
        gameService.updateGame(gameStorage.getId(), null, null, this.idStorage.getID(), true, game.settings().mapRadius(), game.settings().victoryPoints(), this.gameStorage.getMapTemplate(), this.gameStorage.isRollSeven(), this.gameStorage.getStartingResources())
                .observeOn(FX_SCHEDULER)
                .doOnError(error -> {
                    if ("HTTP 403 ".equals(error.getMessage())) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "only the owner can start the game!");
                        // Set alert stylesheet
                        DialogPane dialogPane = alert.getDialogPane();
                        dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
                                .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
                        alert.showAndWait();
                    }
                })
                .subscribe();
    }

    //color event, if color is picked then send color
    public void colorPicked() {
        gameLobbyInformation.colorPicked(colorPicker, memberService, gameStorage, idStorage);
    }

    //changes between Spectator and Player
    public void onCheckBox() {
        gameLobbyInformation.checkBoxClicked(memberService, gameStorage, idStorage, colorPicker, idReadyButton);
    }
}
