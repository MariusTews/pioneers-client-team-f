package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;

import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.service.*;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.uniks.pioneers.Constants.*;

public class GameScreenController implements Controller {

    private final ObservableList<Player> players = FXCollections.observableArrayList();

    @FXML
    public Pane mapPane;
    @FXML
    public Pane chatPane;
    @FXML
    public Label diceSumLabel;
    @FXML
    public VBox opponentsView;

    private final App app;

    private final GameIDStorage gameIDStorage;
    private final IDStorage idStorage;

    private final PioneersService pioneersService;
    private final EventListener eventListener;
    private final MemberIDStorage memberIDStorage;
    private final UserService userService;
    private final MessageService messageService;
    private final MemberService memberService;
    public Pane userPaneId;

    private GameFieldSubController gameFieldSubController;
    private MessageViewSubController messageViewSubController;
    private final CompositeDisposable disposable = new CompositeDisposable();

    private final List<OpponentSubController> opponentSubCons = new ArrayList<>();
    private final HashMap<String, User> userHash = new HashMap<>();

    private UserSubView userSubView;

    @Inject
    public GameScreenController(App app,
                                GameIDStorage gameIDStorage,
                                IDStorage idStorage,
                                PioneersService pioneersService,
                                EventListener eventListener,
                                MemberIDStorage memberIDStorage,
                                UserService userService,
                                MessageService messageService,
                                MemberService memberService) {
        this.app = app;
        this.gameIDStorage = gameIDStorage;
        this.idStorage = idStorage;
        this.pioneersService = pioneersService;
        this.eventListener = eventListener;
        this.userService = userService;
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

                    pioneersService
                            .findAllPlayers(this.gameIDStorage.getId())
                            .observeOn(FX_SCHEDULER)
                            .subscribe(c -> {
                                for (Player player : c) {
                                    if (!player.userId().equals(idStorage.getID())) {
                                        players.add(player);
                                    }
                                }
                            });
                });

        disposable.add(eventListener
                .listen("games." + this.gameIDStorage.getId() + ".moves.*." + "created", Move.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    Move move = event.data();
                    if (move.action().endsWith("roll")) {
                        diceSumLabel.setText(Integer.toString(move.roll()));
                    }
                }));

        // Listen to the buildings for recognizing if achievements changed
        disposable.add(eventListener
                .listen("games." + this.gameIDStorage.getId() + ".players.*.*", Player.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(this::handlePlayerEvent));

        // Initialize sub controller for ingame chat, add listener and load all messages
        this.messageViewSubController = new MessageViewSubController(eventListener, gameIDStorage,
                userService, messageService, memberIDStorage, memberService);
        messageViewSubController.init();

        this.userSubView = new UserSubView(gameIDStorage,idStorage,userService,eventListener,pioneersService);
        this.userSubView.init();

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

        GameFieldSubController gameFieldSubController = new GameFieldSubController(app, gameIDStorage, pioneersService,
                                                                                                idStorage,eventListener);
        gameFieldSubController.init();
        mapPane.getChildren().setAll(gameFieldSubController.render());

        // Show chat and load the messages
        chatPane.getChildren().setAll(messageViewSubController.render());

        userPaneId.getChildren().setAll(userSubView.render());

        // Render opponent loads the opponent view everytime the members list is changed
        // render opponents when achievements change
        this.players.addListener((ListChangeListener<? super Player>) c ->
               this.opponentsView.getChildren().setAll(c.getList().stream().map(this::renderOpponent).toList()));

        return parent;
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
            this.players.remove(player);
            this.removeOpponent(player);
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
        System.out.println("ENTER RENDER PLAYER");
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


    public void onMouseClicked(MouseEvent mouseEvent) {
        diceRoll();
    }

    public void diceRoll() {
        State state = pioneersService.findOneState(gameIDStorage.getId()).blockingFirst();
        List<ExpectedMove> expectedMove = state.expectedMoves();
        ExpectedMove currentExpectedMove = expectedMove.get(0);
        String action = currentExpectedMove.action();

        if (action.endsWith("roll")) {
            pioneersService.move(gameIDStorage.getId(), action, 0, 0, 0, 0, "settlement")
                    .observeOn(FX_SCHEDULER)
                    .subscribe(result -> {
                    }, Throwable::printStackTrace);
        }
    }
}
