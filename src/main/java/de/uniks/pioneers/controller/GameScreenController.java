package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.computation.DiceRoll;
import de.uniks.pioneers.computation.RandomAction;
import de.uniks.pioneers.computation.SpectatorRenderInGame;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.service.*;
import de.uniks.pioneers.websocket.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static de.uniks.pioneers.Constants.*;
import static de.uniks.pioneers.computation.CalculateMap.createId;


@SuppressWarnings("ResultOfMethodCallIgnored")
public class GameScreenController implements Controller {

    private final AchievementsService achievementsService;
    private final UserStorage userStorage;
    public Label spectatorTitleId;
    public ImageView arrowImageId;
    public ImageView imageTradingFoldoutId;
    public ImageView imageTradingFoldInId;
    public Pane paneTradingId;

    private List<CircleSubController> circleSubControllers = new ArrayList<>();

    private final ObservableList<Player> opponents = FXCollections.observableArrayList();

    private final ObservableList<Player> playerOwnView = FXCollections.observableArrayList();

    private final ObservableList<Member> members = FXCollections.observableArrayList();

    private final ObservableList<Member> spectatorMember = FXCollections.observableArrayList();

    private final Provider<LobbyController> lobbyController;

    //all Users
    private final List<User> allUser = new ArrayList<>();

    @FXML
    public ScrollPane mapPane;
    @FXML
    public Pane chatPane;
    @FXML
    public VBox opponentsView;
    @FXML
    public Button finishTurnButton;
    @FXML
    public ImageView diceOne;
    @FXML
    public ImageView diceTwo;
    @FXML
    public Label nextMoveLabel;
    @FXML
    public Label currentPlayerLabel;
    @FXML
    public Button leave;
    @FXML
    public VBox remainingTimeView;
    @FXML
    public VBox gameTimeView;
    @FXML
    //spectator pane
    public Pane spectatorPaneId;

    public Pane userViewPane;
    @FXML
    public Pane tradingPane;

    //total amount of cards currently at the moment
    @FXML
    public Label devCardsAmountLabel;

    @FXML
    public ImageView imageChatFoldoutId;
    @FXML
    public Pane chatPaneFoldOut;
    @FXML
    public ImageView imageChatFoldInId;
    @FXML
    public ImageView imageChatNotification;
    private final App app;
    private final GameStorage gameStorage;
    private final IDStorage idStorage;

    private String lastBuildingPosition;

    private final PioneersService pioneersService;
    private final EventListener eventListener;
    private final MemberIDStorage memberIDStorage;
    private final UserService userService;

    private final GameService gameService;
    private final MessageService messageService;
    private final MemberService memberService;
    private final SoundService soundService;


    private GameFieldSubController gameFieldSubController;
    private MessageViewSubController messageViewSubController;
    private TradingSubController tradingSubController;
    private TradeAcceptSubcontroller tradeAcceptSubcontroller;
    private final CompositeDisposable disposable = new CompositeDisposable();

    private final List<OpponentSubController> opponentSubCons = new ArrayList<>();
    private final HashMap<String, User> userHash = new HashMap<>();
    private boolean runDiscardOnce = true;
    private Point3D currentRobPlace;
    private RandomAction calculateMove;
    private TimerController moveTimer;
    private DiscardResourcesController discard;
    private boolean acceptRenderFlag = false;
    private ExpectedMove nextMove;
    private boolean rejoin = false;
    private int playerOffer = 0;

    @Inject
    public GameScreenController(Provider<LobbyController> lobbyController,
                                App app,
                                GameStorage gameStorage,
                                IDStorage idStorage,
                                PioneersService pioneersService,
                                EventListener eventListener,
                                MemberIDStorage memberIDStorage,
                                UserService userService,
                                GameService gameService,
                                MessageService messageService,
                                MemberService memberService,
                                SoundService soundService,
                                AchievementsService achievementsService,
                                UserStorage userStorage) {
        this.lobbyController = lobbyController;
        this.app = app;
        this.gameStorage = gameStorage;
        this.idStorage = idStorage;
        this.pioneersService = pioneersService;
        this.eventListener = eventListener;
        this.userService = userService;
        this.gameService = gameService;
        this.messageService = messageService;
        this.memberIDStorage = memberIDStorage;
        this.memberService = memberService;
        this.achievementsService = achievementsService;
        this.userStorage = userStorage;
        this.soundService = soundService;
    }

    @Override
    public void init() {
        // For later : userHash is needed in MessageViewSubController too,
        // improvement would be to not initialize the hash twice.
        // Get all users for the username and avatar. Save in HashMap to find the user by his/her ID
        this.allUser.addAll(this.userStorage.getUserList());
        for (User user : this.allUser) {
            this.userHash.put(user._id(), user);
        }

        //get all the members
        memberService
                .getAllGameMembers(gameStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(c -> {
                    for (Member member : c) {
                        if (member.spectator() && member.gameId().equals(this.gameStorage.getId())) {
                            this.spectatorMember.add(member);
                        }
                    }
                    //Added to prevent 403 error
                    for (Member m : c) {
                        if (!m.spectator() && m.gameId().equals(this.gameStorage.getId()) &&
                                m.userId().equals(this.idStorage.getID())) {
                            //get access to it

                            // Check if expected move is founding-roll after joining the game
                            pioneersService
                                    .findOneState(gameStorage.getId())
                                    .observeOn(FX_SCHEDULER)
                                    .subscribe(r -> {
                                        if (!r.expectedMoves().isEmpty()) {
                                            if (r.expectedMoves().get(0).action().equals("founding-roll")) {
                                                foundingDiceRoll();
                                            }
                                        }
                                    });
                            break;
                        }
                    }
                    this.members.setAll(c);
                });

        pioneersService
                .findAllPlayers(this.gameStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(c -> {
                    for (Member member : this.members) {
                        for (Player player : c) {
                            //this checks if the player is opponent or spectator or yourself
                            if (!player.userId().equals(idStorage.getID()) && member.userId()
                                    .equals(player.userId())) {
                                opponents.add(player);
                            } else if (player.userId().equals(idStorage.getID()) && member.userId()
                                    .equals(player.userId())) {
                                playerOwnView.add(player);
                            }
                        }
                    }
                });

        // Listen to the State to handle the event
        disposable.add(eventListener
                .listen("games." + this.gameStorage.getId() + ".state.*", State.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(this::handleStateEvents));

        // Listen to the Moves to handle the event
        disposable.add(eventListener
                .listen("games." + this.gameStorage.getId() + ".moves.*." + "created", Move.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(this::handleMoveEvents));

        // Listen to the players for recognizing if achievements changed
        disposable.add(eventListener
                .listen("games." + this.gameStorage.getId() + ".players.*.*", Player.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(this::handlePlayerEvent));

        //Listen to the Building to handle the event
        disposable.add(eventListener
                .listen("games." + this.gameStorage.getId() + ".buildings.*.*", Building.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(this::handleBuildingEvents));


        // Initialize sub controller for inGame chat, add listener and load all messages
        this.messageViewSubController = new MessageViewSubController(eventListener, gameStorage,
                userService, messageService, memberIDStorage, memberService, this);
        messageViewSubController.init();

        this.calculateMove = new RandomAction(this.gameStorage, this.pioneersService);

        this.tradeAcceptSubcontroller = new TradeAcceptSubcontroller(userService, pioneersService, achievementsService, gameStorage);
        this.tradeAcceptSubcontroller.init();

        // init AchievementsService
        achievementsService.init();
        disposable.add(achievementsService.initUserAchievements().observeOn(FX_SCHEDULER).subscribe());
    }

    @Override
    public void destroy() {
        if (this.messageViewSubController != null) {
            this.messageViewSubController.destroy();
        }

        disposable.clear();

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

        // load and start game timer
        TimerController gameTimer = new TimerController(this);
        gameTimer.countUp();
        gameTimeView.getChildren().setAll(gameTimer.render());

        // load timer for remaining time
        this.moveTimer = new TimerController(this);
        this.remainingTimeView.getChildren().setAll(this.moveTimer.render());

        // add listener on nextMoveLabel to reset the timer if the next action is expected, else the automatic
        // move stops after founding phase with the "roll" move where the current player does not change
        nextMoveLabel.textProperty().addListener((observable, oldValue, newValue) -> this.moveTimer.startTime());

        //add listener on nextMoveLabel to reset the timer if founding-settlement-2 (Placing-UFO-2)
        nextMoveLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(RENAME_FOUNDING_SET2)) {
                this.moveTimer.startTime();
            }
        });

        this.gameFieldSubController = new GameFieldSubController(gameStorage, pioneersService, userService, idStorage, eventListener, achievementsService);
        gameFieldSubController.init();
        mapPane.setContent(gameFieldSubController.render());

        // Show chat and load the messages
        chatPane.getChildren().setAll(messageViewSubController.render());

        // Render opponent loads the opponent view everytime the members list is changed
        // render opponents when achievements change
        this.opponents.addListener((ListChangeListener<? super Player>) c ->
                this.opponentsView.getChildren().setAll(c.getList().stream().map(this::renderOpponent).toList()));

        //userSubView
        this.playerOwnView.addListener((ListChangeListener<? super Player>) c ->
                this.userViewPane.getChildren().setAll(c.getList().stream().map(this::renderSingleUser).toList()));

        imageTradingFoldoutId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/down.png"))));

        //Action is performed when the platform is close
        this.app.getStage().setOnCloseRequest(e -> {
            actionOnCloseScreen();
            e.consume();
        });

        //arrow image
        arrowImageId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/right.png"))));

        imageChatFoldoutId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/up.png"))));
        chatPane.disableProperty().set(true);
        chatPane.visibleProperty().set(false);

        //calculate all the owned cards
        allTheCards();

        //reload on rejoin
        if (rejoin) {
            this.pioneersService
                    .findAllBuildings(this.gameStorage.getId())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(
                            buildings -> {
                                for (Building b : buildings) {
                                    this.gameFieldSubController.updateBuildings(b.x().intValue(), b.y().intValue(),
                                            b.z().intValue(), b.side().intValue(), b.owner(), b.type());
                                }
                            }
                    );
            this.pioneersService
                    .findOneState(this.gameStorage.getId())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(
                            state -> {
                                Event<State> event = new Event<>(UPDATED, state);
                                this.handleStateEvents(event);
                            }
                    );
            //display the dices to be able to click on them (if there is no founding move, they are not displayed automatically)
            displayDice(2);
            rejoin = false;
        }

        return parent;
    }

    private void allTheCards() {
        pioneersService.findOnePlayer(this.gameStorage.getId(), this.idStorage.getID())
                .observeOn(FX_SCHEDULER).subscribe(e -> {
                    //get only not revealed development cards
                    List<DevelopmentCard> developmentCards = e.developmentCards();
                    int sizeNotRevealedCard = 0;
                    for (DevelopmentCard currentDevCard: developmentCards) {
                        if (!currentDevCard.revealed() && !currentDevCard.type().equals(VICTORY)) {
                            sizeNotRevealedCard++;
                        }
                    }
                    //set label for not revealed development cards
                    devCardsAmountLabel.setText(String.valueOf(sizeNotRevealedCard));
                });
    }

    private void actionOnCloseScreen() {
        if (playerOwnView.size() + opponents.size() == 1) {
            gameService
                    .deleteGame(gameStorage.getId())
                    .observeOn(FX_SCHEDULER)
                    .subscribe();
            userService.statusUpdate(this.idStorage.getID(), "offline").observeOn(FX_SCHEDULER)
                    .subscribe(s -> System.exit(0));
        } else {
            pioneersService.updatePlayer(this.gameStorage.getId(), this.idStorage.getID(), false).
                    observeOn(FX_SCHEDULER).subscribe();
            userService.statusUpdate(this.idStorage.getID(), "offline").observeOn(FX_SCHEDULER)
                    .subscribe(s -> System.exit(0));
        }
    }

    private Node renderSingleUser(Player player) {
        UserSubView userSubView = new UserSubView(idStorage, gameStorage, userService, player, gameFieldSubController,
                this.gameStorage.getVictoryPoints(), pioneersService);
        userSubView.init();
        if (tradingSubController != null) {
            this.tradingSubController.setPlayer(player);
        }
        return userSubView.render();
    }

    private void handleMoveEvents(Event<Move> moveEvent) {
        Move move = moveEvent.data();

        //if the move is a roll display new dice value
        if (move.action().equals("roll") || move.action().equals("founding-roll")) {
            displayDice(move.roll());
        }

        //initiate a trade and show to other players besides yourself
        if (move.action().equals("build") &&
                move.partner() == null &&
                !move.userId().equals(idStorage.getID()) &&
                move.resources() != null) {
            TradeOfferSubcontroller tradeOfferSubcontroller = new TradeOfferSubcontroller(move, pioneersService, gameStorage, idStorage);
            tradeOfferSubcontroller.init();
            tradeOfferSubcontroller.render();
        }

        if (move.action().equals("accept") && Objects.equals(move.partner(), idStorage.getID())) {
            achievementsService.putOrUpdateAchievement(TRADE_PLAYER, 1).blockingFirst();
        }

        // set flag to true, so accept window only renders once
        if (move.action().equals("build") &&
                move.partner() == null &&
                move.userId().equals(idStorage.getID()) &&
                move.resources() != null) {
            acceptRenderFlag = true;
        }

        //wait until everybody made an offer, then show accept dialog
        if (move.action().equals("offer") && !move.userId().equals(idStorage.getID())) {
            if (acceptRenderFlag) {
                tradeAcceptSubcontroller.render();
                // if a previous trade happened and someone declined, reset
                playerOffer = 0;
                acceptRenderFlag = false;
            }

            tradeAcceptSubcontroller.setMove(move);

            if (move.resources() != null) {
                tradeAcceptSubcontroller.addUser(userHash.get(move.userId()), true);
            } else {
                tradeAcceptSubcontroller.addUser(userHash.get(move.userId()), false);
                playerOffer++;
            }

            if (playerOffer == this.opponents.size()) {
                tradeAcceptSubcontroller.declineTrade();
                new AlertService().showAlert("Every player declined the trade");
            }
        }
    }

    private void handlePlayerEvent(Event<Player> playerEvent) {
        // Handle event on player and refresh list of players
        Player player = playerEvent.data();

        if (playerEvent.event().endsWith(UPDATED)) {

            for (Player p : playerOwnView) {
                if (p.userId().equals(player.userId())) {
                    if (player.developmentCards() != null) {
                        if (!player.developmentCards().isEmpty()) {
                            AlertService alertService = new AlertService();
                            alertService.alertForEachCard(player, p);
                        }
                    }
                    playerOwnView.set(playerOwnView.indexOf(p), player);

                    // check if the user dropped or received resources and play the according sound
                    int amountResources = 0;
                    for (int resource : p.resources().values()) {
                        amountResources += resource;
                    }
                    int amountNewResources = 0;
                    for (int resource : player.resources().values()) {
                        amountNewResources += resource;
                    }
                    if (amountNewResources > amountResources) {
                        if (p.resources().getOrDefault("lumber", 0) < player.resources().getOrDefault("lumber", 0)) {
                            achievementsService.putOrUpdateAchievement(EARTH_CACTUS_PICKER, player.resources().get("lumber") - p.resources().getOrDefault("lumber", 0)).blockingFirst();
                        }
                        if (p.resources().getOrDefault("brick", 0) < player.resources().getOrDefault("brick", 0)) {
                            achievementsService.putOrUpdateAchievement(MARS_BAR_PICKER, player.resources().get("brick") - p.resources().getOrDefault("brick", 0)).blockingFirst();
                        }
                        if (p.resources().getOrDefault("ore", 0) < player.resources().getOrDefault("ore", 0)) {
                            achievementsService.putOrUpdateAchievement(MOON_ROCK_PICKER, player.resources().get("ore") - p.resources().getOrDefault("ore", 0)).blockingFirst();
                        }
                        if (p.resources().getOrDefault("wool", 0) < player.resources().getOrDefault("wool", 0)) {
                            achievementsService.putOrUpdateAchievement(NEPTUNE_CRYSTAL_PICKER, player.resources().get("wool") - p.resources().getOrDefault("wool", 0)).blockingFirst();
                        }
                        if (p.resources().getOrDefault("grain", 0) < player.resources().getOrDefault("grain", 0)) {
                            achievementsService.putOrUpdateAchievement(VENUS_GRAIN_PICKER, player.resources().get("grain") - p.resources().getOrDefault("grain", 0)).blockingFirst();
                        }
                        this.soundService.playSound("receive");
                    } else if (amountNewResources < amountResources) {
                        this.soundService.playSound("drop");
                    }
                    //call calculate method for calculating all the owned cards
                    allTheCards();
                }
            }

            for (Player p : opponents) {
                if (p.userId().equals(player.userId())) {
                    this.removeOpponent(p);
                    opponents.set(opponents.indexOf(p), player);
                }
            }
            //sets the winner
            winnerScreen(playerOwnView, opponents);
        } else if (playerEvent.event().endsWith(CREATED)) {
            if (!player.userId().equals(idStorage.getID())) {
                this.opponents.add(player);
                this.opponentsView.getChildren().add(renderOpponent(player));
            }
        } else if (playerEvent.event().endsWith(DELETED)) {
            this.opponents.remove(player);
            this.removeOpponent(player);
        }
    }

    private void winnerScreen(ObservableList<Player> playerOwnView, ObservableList<Player> opponents) {
        // If winner screen appears during rob move - change cursor back to default
        this.mapPane.getScene().setCursor(Cursor.DEFAULT);
        HashMap<String, List<String>> userNumberPoints = new HashMap<>();
        //This saves username and their respective points from game in hashmap
        for (User user : this.allUser) {
            save(playerOwnView, userNumberPoints, user);
            save(opponents, userNumberPoints, user);
        }

        for (List<String> s : userNumberPoints.values()) {
            if (s.contains(String.valueOf(this.gameStorage.getVictoryPoints()))) {
                WinnerController winnerController = new WinnerController(userNumberPoints, currentPlayerLabel.getScene().getWindow()
                        , gameStorage, idStorage, userService, achievementsService, gameService, app, lobbyController);
                winnerController.init();
                winnerController.render();
            }
        }
    }

    private void save(ObservableList<Player> playerList, HashMap<String, List<String>> userNumberPoints, User user) {
        for (Player p : playerList) {
            if (p.userId().equals(user._id()) && p.gameId().equals(this.gameStorage.getId())) {
                List<String> ls = new ArrayList<>();
                ls.add(p.color());
                ls.add(p.victoryPoints().toString());
                userNumberPoints.put(user.name(), ls);
            }
        }
    }

    private void handleStateEvents(Event<State> stateEvent) {
        State state = stateEvent.data();
        if (!state.expectedMoves().isEmpty()) {
            if (circleSubControllers.isEmpty()) {
                this.circleSubControllers = this.getGameFieldSubController().getCirclesSubCons();
            }
            for (CircleSubController subController : this.circleSubControllers) {
                subController.setNextMove(state.expectedMoves().get(0));
            }
            this.nextMove = state.expectedMoves().get(0);
        }

        if (stateEvent.event().endsWith(UPDATED)) {
            // change the nextMoveLabel to the current move and adapt to the renamed buildings
            // checks the if the expected move is empty or not
            // in some cases it is required
            if (!state.expectedMoves().isEmpty()) {
                String currentMove = state.expectedMoves().get(0).action();
                switch (currentMove) {
                    case "founding-settlement-1" -> nextMoveLabel.setText(RENAME_FOUNDING_SET1);
                    case "founding-settlement-2" -> nextMoveLabel.setText(RENAME_FOUNDING_SET2);
                    case "founding-road-1" -> nextMoveLabel.setText(RENAME_FOUNDING_ROAD1);
                    case "founding-road-2" -> nextMoveLabel.setText(RENAME_FOUNDING_ROAD2);
                    default -> nextMoveLabel.setText(currentMove);
                }
                // change the currentPlayerLabel to the current player
                User currentPlayer = this.userHash.get(state.expectedMoves().get(0).players().get(0));
                if (currentPlayer == null) {
                    currentPlayer = userService.findOne(state.expectedMoves().get(0).players().get(0)).blockingFirst();
                }
                currentPlayerLabel.setText(currentPlayer.name());

                // open screen for discarding resources if its current player's screen + state is drop
                // enters more than one time this method: runDiscardOnce variable or find another solution
                if ((currentMove.equals(DROP_ACTION)) && currentPlayer._id().equals(idStorage.getID()) && runDiscardOnce) {
                    // initialize and render the discard resources view -> open new window (new stage)
                    // get the current player and open the window for dropping resources
                    runDiscardOnce = false;
                    for (Player p : this.playerOwnView) {
                        if (p.userId().equals(currentPlayer._id())) {
                            discard = new DiscardResourcesController(p, this.gameStorage.getId(),
                                    this.pioneersService, currentPlayerLabel.getScene().getWindow());
                            discard.render();
                            // Deleting the controller is not needed, because the garbage collector should delete the controller
                            // after closing the window
                        }
                    }
                }

                // set runDiscardOnce on true again, when another action appears
                if (!currentMove.equals(DROP_ACTION)) {
                    runDiscardOnce = true;
                }

                // change the cursor when action is "rob" instead of alert (or notification),
                //  remove the image from cursor, when leaving the game or when placing robber
                if (currentMove.equals(ROB_ACTION) && currentPlayer._id().equals(idStorage.getID()) && !app.getTest()) {
                    Image image = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/robber.png")).toString());
                    currentPlayerLabel.getScene().setCursor(new ImageCursor(image, image.getWidth() / 2, image.getHeight() / 2));
                } else {
                    currentPlayerLabel.getScene().setCursor(Cursor.DEFAULT);
                }

                if (state.robber() != null && !state.robber().equals(currentRobPlace)) {
                    // update the view and place the robber on new tile
                    this.updateRobView(state.robber());
                }
            }
        }
    }

    // update view when robber was set
    private void updateRobView(Point3D newCoordinates) {
        Scene scene = this.mapPane.getScene();
        // find fx:id of the existing ImageView by already available method: first delete image from old tile
        if (currentRobPlace != null) {
            ImageView oldRobImageView = (ImageView) scene.lookup("#" +
                    createId(currentRobPlace.x().intValue(), currentRobPlace.y().intValue(), currentRobPlace.z().intValue()) + "_RobberImage");
            oldRobImageView.setImage(null);
        }
        // set image of robber on new tile
        ImageView robImageView = (ImageView) scene.lookup("#" +
                createId(newCoordinates.x().intValue(), newCoordinates.y().intValue(), newCoordinates.z().intValue()) + "_RobberImage");
        robImageView.setImage(new Image(Objects.requireNonNull(Main.class
                .getResource("view/assets/robber.png")).toString()));

        // Play robber sound
        this.soundService.playSound(ROB_ACTION);

        currentRobPlace = newCoordinates;
    }

    private void handleBuildingEvents(Event<Building> buildingEvent) {
        Building building = buildingEvent.data();
        int x = building.x().intValue();
        int y = building.y().intValue();
        int z = building.z().intValue();
        int side = building.side().intValue();
        String position = "x" + x + "y" + y + "z" + z + "_" + side;
        if (buildingEvent.event().endsWith(CREATED)) {
            lastBuildingPosition = position;
        }
        if (buildingEvent.event().endsWith(UPDATED)) {
            lastBuildingPosition = position;
        }

        // Play sound for buildings
        this.soundService.playSound("building");
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
                this.gameStorage.getVictoryPoints(), pioneersService);
        opponentSubCons.add(opponentCon);
        return opponentCon.render();
    }

    // diceRoll if the current move is "roll"
    public void diceRoll() {
        if (nextMoveLabel.getText().equals("roll")) {
            pioneersService.move(gameStorage.getId(), nextMoveLabel.getText(), null, null, null, null, null, null, null)
                    .observeOn(FX_SCHEDULER)
                    .subscribe(result -> {
                    }, Throwable::printStackTrace);
        }
    }

    // automatic foundingDiceRoll after joining the game
    public void foundingDiceRoll() {
        pioneersService.move(gameStorage.getId(), "founding-roll", 0, 0, 0, 0, "settlement", null, null)
                .observeOn(FX_SCHEDULER)
                .subscribe();
    }

    //update GameStatus when leaving game
    public void onLeave() {
        boolean changeToPlayer = false;
        for (Member m : spectatorMember) {
            if (m.gameId().equals(this.gameStorage.getId()) && m.userId().equals(this.idStorage.getID())) {
                this.app.show(lobbyController.get());
                changeToPlayer = true;
                break;
            }
        }
        //this distinguishes between player and spectator
        if (!changeToPlayer) {
            // set active to false only when there is more than one game member
            boolean active = this.members.size() == 1;
            pioneersService.updatePlayer(this.gameStorage.getId(), this.idStorage.getID(), active)
                    .observeOn(FX_SCHEDULER).subscribe(onSuccess -> this.app.show(lobbyController.get()));
        }

        // If player leaves during rob move - change cursor back to default
        this.mapPane.getScene().setCursor(Cursor.DEFAULT);
    }

    public void finishTurn() {
        pioneersService.move(gameStorage.getId(), "build", null, null, null, null, null, null, null)
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                }, onError -> {
                });
    }

    public void handleExpiredTime() {
        //current Move is founding-settlement
        if (currentPlayerLabel.getText().equals(userHash.get(idStorage.getID()).name())) {
            if (nextMoveLabel.getText().startsWith("Place-UFO")) {
                //get foundingPhase (1 or 2)
                String foundingPhase = nextMoveLabel.getText().substring(nextMoveLabel.getText().length() - 1);
                // place random settlement
                this.calculateMove.calculateSettlement("founding-settlement-" + foundingPhase);

                //current Move is founding-road
            } else if (nextMoveLabel.getText().startsWith("Place-Tube-")) {
                //get foundingPhase (1 or 2)
                String foundingPhase = nextMoveLabel.getText().substring(nextMoveLabel.getText().length() - 1);

                // calculate and place random road
                this.calculateMove.calculateRoad(this.lastBuildingPosition, "founding-road-" + foundingPhase);
            } else if (nextMoveLabel.getText().equals("roll")) {
                // player needs to roll and skips his turn if the timer reached 0 seconds
                diceRoll();
                finishTurn();
            } else if (nextMoveLabel.getText().equals(ROB_ACTION)) {
                this.calculateMove.automaticRob(this.idStorage.getID());
                mapPane.getScene().setCursor(Cursor.DEFAULT);
            } else if (nextMoveLabel.getText().equals(DROP_ACTION)) {
                // get the current stage for closing the discard window
                discard.getPrimaryStage().close();
                this.calculateMove.automaticDrop(playerOwnView.get(0).resources());
            } else if (nextMoveLabel.getText().equals("offer")) {
                pioneersService
                        .tradePlayer(gameStorage.getId(), "offer", null, null)
                        .observeOn(FX_SCHEDULER)
                        .subscribe();
            } else if (nextMoveLabel.getText().equals("accept")) {
                pioneersService
                        .tradePlayer(gameStorage.getId(), "accept", null, null)
                        .observeOn(FX_SCHEDULER)
                        .subscribe();
            } else {
                finishTurn();
            }
        }
    }

    public GameFieldSubController getGameFieldSubController() {
        return gameFieldSubController;
    }

    public void zoomIn() {
        this.gameFieldSubController.zoomIn();
    }

    public void zoomOut() {
        this.gameFieldSubController.zoomOut();
    }

    public void displayDice(int diceNumber) {
        this.diceOne.toFront();
        this.diceTwo.toFront();
        this.diceTwo.setVisible(true);

        DiceRoll diceRoll = new DiceRoll();
        List<Image> dices = diceRoll.getDiceImages(diceNumber);
        Image image1 = dices.get(0);
        Image image2 = dices.get(1);

        if (image1 != null) {
            this.diceOne.setImage(image1);
        }
        if (image2 != null) {
            this.diceTwo.setImage(image2);
        } else {
            this.diceTwo.setVisible(false);
        }
    }

    public void onShowDevCard() {
        DevelopmentCardController developmentCardController = new DevelopmentCardController(this, this.currentPlayerLabel.getScene().getWindow(), gameStorage,
                idStorage, pioneersService, nextMove, userHash);
        developmentCardController.render();
    }

    public void onShowSpectator() {
        if (spectatorMember.size() >= 1) {
            SpectatorRenderInGame spectatorRenderInGame = new SpectatorRenderInGame();
            spectatorRenderInGame.checkMember(spectatorMember, spectatorPaneId, allUser, spectatorTitleId, arrowImageId);
        }
    }

    public void onClickTradeView() {
        this.tradingSubController = new TradingSubController(gameStorage, pioneersService, achievementsService, idStorage, eventListener);
        tradingSubController.init();
        this.tradingPane.getChildren().setAll(this.tradingSubController.render());
        imageTradingFoldInId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/up.png"))));
        imageTradingFoldInId.disableProperty().set(false);
        paneTradingId.visibleProperty().set(false);
        paneTradingId.disableProperty().set(true);
    }

    public void onClickUP() {
        this.tradingPane.getChildren().clear();
        imageTradingFoldInId.setImage(null);
        imageTradingFoldInId.disableProperty().set(true);
        paneTradingId.disableProperty().set(false);
        paneTradingId.visibleProperty().set(true);
    }

    // open chat
    public void onClickFoldOutChat() {
        imageChatFoldInId.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/down.png"))));
        imageChatFoldInId.toFront();
        imageChatFoldInId.disableProperty().set(false);
        chatPane.visibleProperty().set(true);
        chatPane.disableProperty().set(false);
        chatPaneFoldOut.visibleProperty().set(false);
        chatPaneFoldOut.disableProperty().set(true);
        imageChatNotification.setImage(null);
    }

    // close chat
    public void onClickFoldInChat() {
        imageChatFoldInId.setImage(null);
        chatPane.visibleProperty().set(false);
        chatPaneFoldOut.visibleProperty().set(true);
        chatPaneFoldOut.disableProperty().set(false);
    }

    //set red notification circle
    public void setNotificationCircle() {
        if (chatPaneFoldOut.visibleProperty().getValue().equals(true)) {
            this.imageChatNotification.setImage(new Image(String.valueOf(Main.class.getResource("view/assets/circleRed.png"))));
        }
    }

    public void setRejoin(boolean rejoin) {
        this.rejoin = rejoin;
    }
}
