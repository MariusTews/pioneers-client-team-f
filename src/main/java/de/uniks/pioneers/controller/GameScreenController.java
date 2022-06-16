package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.Move;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.State;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.*;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static de.uniks.pioneers.Constants.*;

public class GameScreenController implements Controller {

    private final ObservableList<Player> players = FXCollections.observableArrayList();

    private final ObservableList<Player> playerOwnView = FXCollections.observableArrayList();

    private final Provider<LobbyController> lobbyController;

    @FXML
    public Pane mapPane;
    @FXML
    public Pane chatPane;
    @FXML
    public Label diceSumLabel;
    @FXML
    public VBox opponentsView;
    @FXML
    public Button finishTurnButton;
    @FXML
    public Label nextMoveLabel;
    @FXML
    public Label currentPlayerLabel;
    @FXML
    public Label timerLabel;

    private final App app;

    private final GameIDStorage gameIDStorage;
    private final IDStorage idStorage;

    private final PioneersService pioneersService;
    private final EventListener eventListener;
    private final MemberIDStorage memberIDStorage;
    private final UserService userService;

    private final GameService gameService;
    private final MessageService messageService;
    private final MemberService memberService;
    public Pane userPaneId;

    private GameFieldSubController gameFieldSubController;
    private MessageViewSubController messageViewSubController;
    private final CompositeDisposable disposable = new CompositeDisposable();

    private final List<OpponentSubController> opponentSubCons = new ArrayList<>();
    private final HashMap<String, User> userHash = new HashMap<>();
    private final Timeline timeline = new Timeline();

    @Inject
    public GameScreenController(Provider<LobbyController> lobbyController,
                                App app,
                                GameIDStorage gameIDStorage,
                                IDStorage idStorage,
                                PioneersService pioneersService,
                                EventListener eventListener,
                                MemberIDStorage memberIDStorage,
                                UserService userService,
                                GameService gameService,
                                MessageService messageService,
                                MemberService memberService) {
        this.lobbyController = lobbyController;
        this.app = app;
        this.gameIDStorage = gameIDStorage;
        this.idStorage = idStorage;
        this.pioneersService = pioneersService;
        this.eventListener = eventListener;
        this.userService = userService;
        this.gameService = gameService;
        this.messageService = messageService;
        this.memberIDStorage = memberIDStorage;
        this.memberService = memberService;
    }

    @Override
    public void init() {

        // For later : userHash is needed in MessageViewSubController too,
        // improvement would be to not initialize the hash twice.
        // Get all users for the username and avatar. Save in HashMap to find the user by his/her ID
        userService
                .findAllUsers()
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                    for (User user : result) {
                        this.userHash.put(user._id(), user);
                    }

                    // Listen to the State to handle the event
                    disposable.add(eventListener
                            .listen("games." + this.gameIDStorage.getId() + ".state.*", State.class)
                            .observeOn(FX_SCHEDULER)
                            .subscribe(this::handleStateEvents));

                    // Check if expected move is founding-roll after joining the game
                    pioneersService
                            .findOneState(gameIDStorage.getId())
                            .observeOn(FX_SCHEDULER)
                            .subscribe(r -> {
                                if (r.expectedMoves().get(0).action().equals("founding-roll")) {
                                    foundingDiceRoll();
                                }
                            });

                    pioneersService
                            .findAllPlayers(this.gameIDStorage.getId())
                            .observeOn(FX_SCHEDULER)
                            .subscribe(c -> {
                                for (Player player : c) {
                                    if (!player.userId().equals(idStorage.getID())) {
                                        players.add(player);
                                    } else {
                                        playerOwnView.add(player);
                                    }
                                }
                            });
                });
        // Listen to the Moves to handle the event
        disposable.add(eventListener
                .listen("games." + this.gameIDStorage.getId() + ".moves.*." + "created", Move.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(this::handleMoveEvents));

        // Listen to the players for recognizing if achievements changed
        disposable.add(eventListener
                .listen("games." + this.gameIDStorage.getId() + ".players.*.*", Player.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(this::handlePlayerEvent));


        // Initialize sub controller for ingame chat, add listener and load all messages
        this.messageViewSubController = new MessageViewSubController(eventListener, gameIDStorage,
                userService, messageService, memberIDStorage, memberService);
        messageViewSubController.init();

    }


    @Override
    public void destroy() {
        if (this.messageViewSubController != null) {
            this.messageViewSubController.destroy();
        }

        disposable.dispose();

        this.opponentSubCons.forEach(OpponentSubController::destroy);
        this.opponentSubCons.clear();
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/GameScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        //add listener on currentPlayerLabel to reset the timer if a currentPlayer changes
        currentPlayerLabel.textProperty().addListener((observable, oldValue, newValue) -> startTime());

        this.gameFieldSubController = new GameFieldSubController(app, gameIDStorage, pioneersService, idStorage, eventListener);
        gameFieldSubController.init();
        mapPane.getChildren().setAll(gameFieldSubController.render());

        // Show chat and load the messages
        chatPane.getChildren().setAll(messageViewSubController.render());

        // Render opponent loads the opponent view everytime the members list is changed
        // render opponents when achievements change
        this.players.addListener((ListChangeListener<? super Player>) c ->
                this.opponentsView.getChildren().setAll(c.getList().stream().map(this::renderOpponent).toList()));

        //userSubView
        this.playerOwnView.addListener((ListChangeListener<? super Player>) c ->
                this.userPaneId.getChildren().setAll(c.getList().stream().map(this::renderSingleUser).toList()));

        return parent;
    }

    private Node renderSingleUser(Player player) {
        UserSubView userSubView = new UserSubView(idStorage, userService, player, this.calculateVP(player), gameFieldSubController);
        userSubView.init();

        return userSubView.render();
    }

    private void handleMoveEvents(Event<Move> moveEvent) {
        Move move = moveEvent.data();

        // if the move is a roll change the diceSumLabel to the roll number
        if (move.action().equals("roll")) {
            diceSumLabel.setText(Integer.toString(move.roll()));
        }
    }

    private int calculateVP(Player player) {
        // Calculate the victory points, when the change listener of players recognizes changes
        // Update opponent by removing and rendering opponent with new victory points again
        // 13 is the total number of cities and settlements a player is able to set in the game
        return AMOUNT_SETTLEMENTS_CITIES - (player.remainingBuildings().get(SETTLEMENT) + player.remainingBuildings().get(CITY) * 2);
    }

    private void handlePlayerEvent(Event<Player> playerEvent) {
        // Handle event on player and refresh list of players
        Player player = playerEvent.data();

        if (playerEvent.event().endsWith(UPDATED)) {
            for (Player p : playerOwnView) {
                if (p.userId().equals(player.userId())) {
                    playerOwnView.set(playerOwnView.indexOf(p), player);
                }
            }

            for (Player p : players) {
                if (p.userId().equals(player.userId())) {
                    this.removeOpponent(p);
                    players.set(players.indexOf(p), player);
                }
            }
        } else if (playerEvent.event().endsWith(CREATED)) {
            if (!player.userId().equals(idStorage.getID())) {
                this.players.add(player);
                this.opponentsView.getChildren().add(renderOpponent(player));
            }
        } else if (playerEvent.event().endsWith(DELETED)) {
            if (players.size() < 2) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("You are the Winner!!!");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isEmpty()) {
                    this.app.show(lobbyController.get());
                } else if (result.get() == ButtonType.OK) {
                    this.app.show(lobbyController.get());
                } else if (result.get() == ButtonType.CANCEL) {
                    this.app.show(lobbyController.get());
                }
            }
            this.players.remove(player);
            this.removeOpponent(player);
        }
    }

    private void handleStateEvents(Event<State> stateEvent) {
        State state = stateEvent.data();

        if (stateEvent.event().endsWith(UPDATED)) {
            // change the nextMoveLabel to the current move
            nextMoveLabel.setText(state.expectedMoves().get(0).action());
            // change the currentPlayerLabel to the current player
            currentPlayerLabel.setText(this.userHash.get(state.expectedMoves().get(0).players().get(0)).name());
        }
    }

    private void removeOpponent(Player player) {
        // Remove sub-controller of opponent who left
        for (OpponentSubController subCon : this.opponentSubCons) {
            if (subCon.getId().equals(player.userId())) {
                this.opponentSubCons.remove(subCon);
                break;
            }
        }
    }

    // Load the opponent view with username and avatar
    private Node renderOpponent(Player player) {
        // user's view loads the opponents without the user himself/herself, because user has own view with stats
        // User as parameter for getting avatar and username
        for (OpponentSubController subCon : this.opponentSubCons) {
            if (subCon.getId().equals(player.userId())) {
                return subCon.getParent();
            }
        }

        OpponentSubController opponentCon = new OpponentSubController(player, this.userHash.get(player.userId()),
                this.calculateVP(player));
        opponentSubCons.add(opponentCon);
        return opponentCon.render();
    }

    public void onMouseClicked() {
        diceRoll();
    }

    // diceRoll if the current move is roll
    public void diceRoll() {
        if (nextMoveLabel.getText().equals("roll")) {
            pioneersService.move(gameIDStorage.getId(), nextMoveLabel.getText(), 0, 0, 0, 0, "settlement")
                    .observeOn(FX_SCHEDULER)
                    .subscribe(result -> {
                    }, Throwable::printStackTrace);
        }
    }

    // automatic foundingDiceRoll after joining the game
    public void foundingDiceRoll() {
        pioneersService.move(gameIDStorage.getId(), "founding-roll", 0, 0, 0, 0, "settlement")
                .observeOn(FX_SCHEDULER)
                .subscribe();
    }

    public void onLeave(ActionEvent ignoredEvent) {
        if ((players.size() + playerOwnView.size()) == 2) {
            gameService.findOneGame(this.gameIDStorage.getId())
                    .observeOn(FX_SCHEDULER).
                    subscribe(col -> {
                        if (col.owner().equals(idStorage.getID())) {
                            gameService.
                                    deleteGame(this.gameIDStorage.getId()).
                                    observeOn(FX_SCHEDULER).
                                    subscribe(onSuccess ->
                                            this.app.show(lobbyController.get()), onError -> {
                                    });
                        } else {
                            this.app.show(lobbyController.get());
                        }
                    });

        } else {
            this.app.show(lobbyController.get());
        }
    }

    public void finishTurn() {
        pioneersService.move(gameIDStorage.getId(), "build", null, null, null, null, null)
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                }, onError -> {
                });

    }

    private void startTime() {

        // starting time
        final Integer[] startTime = {180};
        final Integer[] seconds = {startTime[0]};

        timeline.setCycleCount(Timeline.INDEFINITE);

        //gets called every second to reduce the timer by one second
        KeyFrame frame = new KeyFrame(Duration.seconds(1), event -> {
            seconds[0]--;
            timerLabel.setText(seconds[0].toString());
            if (seconds[0] <= 0) {
                timeline.stop();
                if (currentPlayerLabel.getText().equals(userHash.get(idStorage.getID()).name())) {
                    // player needs to roll and skips his turn if the timer reached 0 seconds
                    if (nextMoveLabel.getText().equals("roll")) {
                        diceRoll();
                    }
                    finishTurn();
                }
            }
        });

        timeline.getKeyFrames().setAll(frame);
        // start timer
        timeline.playFromStart();
    }
}
