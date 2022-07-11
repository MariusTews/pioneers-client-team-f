package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.service.*;
import de.uniks.pioneers.websocket.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;

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

    private final ObservableList<Player> opponents = FXCollections.observableArrayList();

    private final ObservableList<Player> playerOwnView = FXCollections.observableArrayList();

    private final ObservableList<Member> members = FXCollections.observableArrayList();

    private final ObservableList<Member> spectatorMember = FXCollections.observableArrayList();

    private final Provider<LobbyController> lobbyController;

    //all Users
    private final ArrayList<User> allUser = new ArrayList<>();

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
    public Label timerLabel;
    @FXML
    public Label playerLongestRoadLabel;
    @FXML
    public Button leave;
    @FXML
    //spectator pane
    public Pane spectatorPaneId;

    public Pane userViewPane;
    @FXML
    public Pane tradingPane;

    private final App app;
    private final GameStorage gameStorage;
    private final IDStorage idStorage;
    public Label gameScreenCountdown;

    private String lastBuildingPosition;

    private final PioneersService pioneersService;
    private final EventListener eventListener;
    private final MemberIDStorage memberIDStorage;
    private final UserService userService;

    private final GameService gameService;
    private final MessageService messageService;
    private final MemberService memberService;


    private GameFieldSubController gameFieldSubController;
    private MessageViewSubController messageViewSubController;
    private TradingSubController tradingSubController;

    private SpectatorViewController spectatorViewController;
    private final CompositeDisposable disposable = new CompositeDisposable();

    private final List<OpponentSubController> opponentSubCons = new ArrayList<>();
    private final HashMap<String, User> userHash = new HashMap<>();
    private final Timeline timeline = new Timeline();

    private final Timeline timelineGameCountDown = new Timeline();
    private boolean runDiscardOnce = true;
    private Point3D currentRobPlace;

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
                                MemberService memberService) {
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
                    this.allUser.addAll(result);
                    for (User user : result) {
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
                                if(!r.expectedMoves().isEmpty()) {
                                    if (r.expectedMoves().get(0).action().equals("founding-roll")) {
                                        foundingDiceRoll();
                                    }
                                }});
                                        break;
                                    }
                                }
                                this.members.setAll(c);
                            });

                    // Listen to the State to handle the event
                    disposable.add(eventListener
                            .listen("games." + this.gameStorage.getId() + ".state.*", State.class)
                            .observeOn(FX_SCHEDULER)
                            .subscribe(this::handleStateEvents));

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
                });
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
                userService, messageService, memberIDStorage, memberService);
        messageViewSubController.init();

        //count up
        countUp();

    }

    private void countUp() {
        final Integer[] startTime = {0};
        final Integer[] seconds = {startTime[0]};

        timelineGameCountDown.setCycleCount(Timeline.INDEFINITE);

        //gets called every second to reduce the timer by one second
        KeyFrame frame = new KeyFrame(Duration.seconds(1), event -> {
            seconds[0]++;
            if (seconds[0] % 60 > 9) {
                gameScreenCountdown.setText("" + (seconds[0] / 60) + ":" + seconds[0] % 60);
            } else {
                gameScreenCountdown.setText("" + (seconds[0] / 60) + ":0" + seconds[0] % 60);
            }
        });

        timelineGameCountDown.getKeyFrames().setAll(frame);
        // start timer
        timelineGameCountDown.playFromStart();
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

        //add listener on nextMoveLabel to reset the timer if founding-settlement-2
        nextMoveLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("founding-settlement-2")) {
                startTime();
            }
        });

        this.gameFieldSubController = new GameFieldSubController(gameStorage, pioneersService, userService, idStorage, eventListener);
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

        /*
         * Render trading sub view
         * hand over own player to trading sub view
         * */

        this.tradingSubController = new TradingSubController(gameStorage, pioneersService, idStorage, eventListener);
        tradingSubController.init();
        this.tradingPane.getChildren().setAll(this.tradingSubController.render());

        //spectator
        this.spectatorMember.addListener((ListChangeListener<? super Member>) c ->
                this.spectatorPaneId.getChildren().setAll(c.getList().stream().map(this::renderSpectator).toList()));

        //Action is performed when the platform is close
        this.app.getStage().setOnCloseRequest(e -> {
            actionOnCloseScreen();
            e.consume();
        });

        return parent;
    }

    private void actionOnCloseScreen() {

        if(playerOwnView.size() + opponents.size() == 1) {
            gameService
                    .deleteGame(gameStorage.getId())
                    .observeOn(FX_SCHEDULER)
                    .subscribe();
            userService.statusUpdate(this.idStorage.getID(), "offline").observeOn(FX_SCHEDULER)
                    .subscribe(s -> {
                        System.exit(0);
                    });
        } else {
            pioneersService.updatePlayer(this.gameStorage.getId(), this.idStorage.getID(), false).
                    observeOn(FX_SCHEDULER).subscribe();
            userService.statusUpdate(this.idStorage.getID(), "offline").observeOn(FX_SCHEDULER)
                    .subscribe(s -> {
                        System.exit(0);
                    });
        }
    }

    private Node renderSpectator(Member member) {
        for (User user : allUser) {
            if (member.userId().equals(user._id())) {
                this.spectatorViewController = new SpectatorViewController(user);
                break;
            }
        }
        return spectatorViewController.render();
    }

    private Node renderSingleUser(Player player) {
        UserSubView userSubView = new UserSubView(idStorage, userService, player, this.calculateVP(player), gameFieldSubController);
        userSubView.init();
        this.tradingSubController.setPlayer(player);
        return userSubView.render();
    }

    private void handleMoveEvents(Event<Move> moveEvent) {
        Move move = moveEvent.data();

        //if the move is a roll display new dice value
        if (move.action().equals("roll") || move.action().equals("founding-roll")) {
            displayDice(move.roll());
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
                //System.out.println(p.userId());
                //System.out.println(player.userId());
                if (p.userId().equals(player.userId())) {
                    //System.out.println("hallo");
                    playerOwnView.set(playerOwnView.indexOf(p), player);
                }
            }

            for (Player p : opponents) {
                if (p.userId().equals(player.userId())) {
                    this.removeOpponent(p);
                    opponents.set(opponents.indexOf(p), player);
                }
            }
            //sets name of the longest road
            updateLongestRoad(playerOwnView,opponents);
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

    //Sets the name of the user who has longestRoad
    private void updateLongestRoad(ObservableList<Player> playerOwnView, ObservableList<Player> opponents) {
        String userId = new String();
        int longestRoad = 0;
        for (Player p:playerOwnView) {
            if(p.longestRoad() != null) {
                if (((int) p.longestRoad()) > longestRoad) {
                    userId = p.userId();
                    longestRoad = (int) p.longestRoad();
                }
            }
        }

        for (Player p :opponents) {
            if(p.longestRoad() != null) {
                if (((int) p.longestRoad()) > longestRoad) {
                    userId = p.userId();
                    longestRoad = (int) p.longestRoad();
                }
            }
        }

        for (User u: allUser) {
            if(u._id().equals(userId)){
                playerLongestRoadLabel.setText(u.name());
                break;
            }
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
            if (s.contains(this.gameStorage.getVictoryPoints())) {
                WinnerController winnerController = new WinnerController(userNumberPoints, currentPlayerLabel.getScene().getWindow()
                        , gameStorage, idStorage, gameService, app, lobbyController);
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

        if (stateEvent.event().endsWith(UPDATED)) {
            // change the nextMoveLabel to the current move
            // checks the if the expected move is empty or not
            // in some cases it is required
            if (!state.expectedMoves().isEmpty()) {
                String currentMove = state.expectedMoves().get(0).action();
                nextMoveLabel.setText(currentMove);
                // change the currentPlayerLabel to the current player
                User currentPlayer = this.userHash.get(state.expectedMoves().get(0).players().get(0));
                currentPlayerLabel.setText(currentPlayer.name());

                // open screen for discarding resources if its current player's screen + state is drop
                // enters more than one time this method: runDiscardOnce variable or find another solution
                if ((currentMove.equals(DROP_ACTION)) && currentPlayer._id().equals(idStorage.getID()) && runDiscardOnce) {
                    // initialize and render the discard resources view -> open new window (new stage)
                    // get the current player and open the window for dropping resources
                    runDiscardOnce = false;
                    for (Player p : this.playerOwnView) {
                        if (p.userId().equals(currentPlayer._id())) {
                            DiscardResourcesController discard = new DiscardResourcesController(p, this.gameStorage.getId(),
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
                if (currentMove.equals(ROB_ACTION) && currentPlayer._id().equals(idStorage.getID())) {
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

    // diceRoll if the current move is "roll"
    public void diceRoll() {
        if (nextMoveLabel.getText().equals("roll")) {
            pioneersService.move(gameStorage.getId(), nextMoveLabel.getText(), 0, 0, 0, 0, "settlement", null, null)
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
            pioneersService.updatePlayer(this.gameStorage.getId(), this.idStorage.getID(), false)
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

    private void startTime() {

        // starting time
        final Integer[] startTime = {180};
        final Integer[] seconds = {startTime[0]};

        timeline.setCycleCount(Timeline.INDEFINITE);

        //gets called every second to reduce the timer by one second
        KeyFrame frame = new KeyFrame(Duration.seconds(1), event -> {
            seconds[0]--;
            if (seconds[0] % 60 > 9) {
                timerLabel.setText("" + (seconds[0] / 60) + ":" + seconds[0] % 60);
            } else {
                timerLabel.setText("" + (seconds[0] / 60) + ":0" + seconds[0] % 60);
            }

            if (seconds[0] <= 0) {
                timeline.stop();
                //current Move is founding-settlement
                if (currentPlayerLabel.getText().equals(userHash.get(idStorage.getID()).name()) && nextMoveLabel.getText().startsWith("founding-settlement")) {
                    //get all valid settlementPosition in dependence of map
                    List<String> validPositions = getAllValidPositions();
                    //get all invalid settlementPositions
                    List<String> allInvalidSettlementCoordinates = getAllInvalidSettlementCoordinates();

                    //remove invalid settlementPositions from validPositions
                    for (String string : allInvalidSettlementCoordinates) {
                        validPositions.remove(string);
                    }

                    int randomNumSettlement = (int) (Math.random() * (validPositions.size()));
                    //select one settlementPosition from all valid settlementPositions
                    String selectedSettlementPosition = validPositions.get(randomNumSettlement);
                    // String to int for next method call
                    int x = Integer.parseInt(selectedSettlementPosition.substring(selectedSettlementPosition.indexOf("x") + 1, selectedSettlementPosition.indexOf("y")));
                    int y = Integer.parseInt(selectedSettlementPosition.substring(selectedSettlementPosition.indexOf("y") + 1, selectedSettlementPosition.indexOf("z")));
                    int z = Integer.parseInt(selectedSettlementPosition.substring(selectedSettlementPosition.indexOf("z") + 1, selectedSettlementPosition.indexOf("_")));
                    int side = Integer.parseInt(selectedSettlementPosition.substring(selectedSettlementPosition.indexOf("_") + 1));

                    //get every possible roadPosition in dependence of chosen settlementPosition
                    List<String> possibleRoadPlacements = getPossibleRoadPlacements(x, y, z, side);

                    int randomNumRoad = (int) (Math.random() * possibleRoadPlacements.size());
                    //select one roadPosition from all valid roadPositions
                    String selectedRoadPosition = possibleRoadPlacements.get(randomNumRoad);
                    // String to int for move call
                    int xRoad = Integer.parseInt(selectedRoadPosition.substring(selectedRoadPosition.indexOf("x") + 1, selectedRoadPosition.indexOf("y")));
                    int yRoad = Integer.parseInt(selectedRoadPosition.substring(selectedRoadPosition.indexOf("y") + 1, selectedRoadPosition.indexOf("z")));
                    int zRoad = Integer.parseInt(selectedRoadPosition.substring(selectedRoadPosition.indexOf("z") + 1, selectedRoadPosition.indexOf("_")));
                    int sideRoad = Integer.parseInt(selectedRoadPosition.substring(selectedRoadPosition.indexOf("_") + 1));
                    //get foundingPhase (1 or 2)
                    String foundingPhase = nextMoveLabel.getText().substring(nextMoveLabel.getText().length() - 1);

                    //place chosen settlement and road
                    pioneersService.move(gameStorage.getId(), "founding-settlement-" + foundingPhase, x, y, z, side, "settlement", null, null)
                            .observeOn(FX_SCHEDULER)
                            .subscribe(result -> pioneersService.move(gameStorage.getId(), "founding-road-" + foundingPhase, xRoad, yRoad, zRoad, sideRoad, "road", null, null)
                                    .observeOn(FX_SCHEDULER)
                                    .subscribe());

                    //current Move is founding-road
                } else if (currentPlayerLabel.getText().equals(userHash.get(idStorage.getID()).name()) && nextMoveLabel.getText().startsWith("founding-road")) {
                    // String to int from lastBuildingPlaced to calculate possible roadPlacements
                    int x = Integer.parseInt(lastBuildingPosition.substring(lastBuildingPosition.indexOf("x") + 1, lastBuildingPosition.indexOf("y")));
                    int y = Integer.parseInt(lastBuildingPosition.substring(lastBuildingPosition.indexOf("y") + 1, lastBuildingPosition.indexOf("z")));
                    int z = Integer.parseInt(lastBuildingPosition.substring(lastBuildingPosition.indexOf("z") + 1, lastBuildingPosition.indexOf("_")));
                    int side = Integer.parseInt(lastBuildingPosition.substring(lastBuildingPosition.indexOf("_") + 1));

                    //get every possible roadPosition
                    List<String> possibleRoadPlacements = getPossibleRoadPlacements(x, y, z, side);

                    int randomNumRoad = (int) (Math.random() * possibleRoadPlacements.size());
                    //select one possibleRoad
                    String selectedRoadPosition = possibleRoadPlacements.get(randomNumRoad);
                    // String to int for move call
                    int xRoad = Integer.parseInt(selectedRoadPosition.substring(selectedRoadPosition.indexOf("x") + 1, selectedRoadPosition.indexOf("y")));
                    int yRoad = Integer.parseInt(selectedRoadPosition.substring(selectedRoadPosition.indexOf("y") + 1, selectedRoadPosition.indexOf("z")));
                    int zRoad = Integer.parseInt(selectedRoadPosition.substring(selectedRoadPosition.indexOf("z") + 1, selectedRoadPosition.indexOf("_")));
                    int sideRoad = Integer.parseInt(selectedRoadPosition.substring(selectedRoadPosition.indexOf("_") + 1));

                    //get foundingPhase (1 or 2)
                    String foundingPhase = nextMoveLabel.getText().substring(nextMoveLabel.getText().length() - 1);

                    //place chosen and road
                    pioneersService.move(gameStorage.getId(), "founding-road-" + foundingPhase, xRoad, yRoad, zRoad, sideRoad, "road", null, null)
                            .observeOn(FX_SCHEDULER)
                            .subscribe();


                } else if (nextMoveLabel.getText().equals("roll")) {
                    // player needs to roll and skips his turn if the timer reached 0 seconds
                    diceRoll();
                    finishTurn();
                } else {
                    finishTurn();
                }
            }
        });

        timeline.getKeyFrames().setAll(frame);
        // start timer
        timeline.playFromStart();
    }

    public List<String> getAllValidPositions() {
        //get current map
        Map map = pioneersService.findAllTiles(gameStorage.getId()).blockingFirst();
        List<String> allTileCoordinates = new ArrayList<>();
        List<String> allWaterTileCoordinates = new ArrayList<>();

        int gameFieldSize = this.gameStorage.getSize();

        for (Tile tile : map.tiles()) {
            allTileCoordinates.add("x" + tile.x().toString() + "y" + tile.y() + "z" + tile.z());
        }
        //top right fixed waterTile it always appears in any map size
        allWaterTileCoordinates.add("x" + (gameFieldSize + 1) + "y" + 0 + "z" + ((gameFieldSize + 1) * (-1)));
        for (int i = 1; i <= gameFieldSize; i++) {
            //top right waterTile side
            allWaterTileCoordinates.add("x" + (gameFieldSize + 1) + "y" + (-i) + "z" + ((gameFieldSize + 1) * (-1) + i));
            //top waterTile side
            allWaterTileCoordinates.add("x" + (gameFieldSize + 1 - i) + "y" + i + "z" + ((gameFieldSize + 1) * (-1)));
        }
        //top left fixed waterTile it always appears in any map size
        allWaterTileCoordinates.add("x" + 0 + "y" + (gameFieldSize + 1) + "z" + ((gameFieldSize + 1) * (-1)));
        for (int i = 1; i <= gameFieldSize; i++) {
            //top left water side
            allWaterTileCoordinates.add("x" + (-i) + "y" + (gameFieldSize + 1) + "z" + ((gameFieldSize + 1) * (-1) + i));
        }
        //far left fixed waterTile it always appears in any map size
        allWaterTileCoordinates.add("x" + ((gameFieldSize + 1) * (-1)) + "y" + (gameFieldSize + 1) + "z" + 0);
        for (int i = 1; i <= gameFieldSize; i++) {
            //bottom left waterTile side
            allWaterTileCoordinates.add("x" + ((gameFieldSize + 1) * (-1)) + "y" + (gameFieldSize + 1 - i) + "z" + i);
        }
        //bottom left fixed waterTile it always appears in any map size
        allWaterTileCoordinates.add("x" + ((gameFieldSize + 1) * (-1)) + "y" + 0 + "z" + (gameFieldSize + 1));
        for (int i = 1; i <= gameFieldSize; i++) {
            //bottom waterTile side
            allWaterTileCoordinates.add("x" + ((gameFieldSize + 1 - i) * (-1)) + "y" + (-i) + "z" + (gameFieldSize + 1));
        }
        //bottom right fixed waterTile it always appears in any map size
        allWaterTileCoordinates.add("x" + 0 + "y" + ((gameFieldSize + 1) * (-1)) + "z" + (gameFieldSize + 1));
        for (int i = 1; i <= gameFieldSize; i++) {
            //bottom right waterTile side
            allWaterTileCoordinates.add("x" + i + "y" + ((gameFieldSize + 1) * (-1)) + "z" + (gameFieldSize + 1 - i));
        }

        List<String> validPositions = new ArrayList<>();
        for (String string : allTileCoordinates) {
            validPositions.add(string + "_0");
            validPositions.add(string + "_6");
        }

        for (String string : allWaterTileCoordinates) {
            int z = Integer.parseInt(string.substring(string.indexOf("z") + 1));
            if (z < 0) {
                validPositions.add(string + "_6");
            } else if (z > 0) {
                validPositions.add(string + "_0");
            }
        }
        return validPositions;
    }

    public List<String> getAllInvalidSettlementCoordinates() {
        List<Building> allBuildings = pioneersService.findAllBuildings(gameStorage.getId()).blockingFirst();
        List<String> allInvalidSettlementCoordinates = new ArrayList<>();
        for (Building building : allBuildings) {
            if (building.side().intValue() == 0) {
                //building itself
                allInvalidSettlementCoordinates.add("x" + building.x().toString() + "y" + building.y() + "z" + building.z() + "_" + building.side());
                //building place down left from current building
                allInvalidSettlementCoordinates.add("x" + building.x() + "y" + (building.y().intValue() + 1) + "z" + (building.z().intValue() - 1) + "_" + "6");
                //building place down right from current building
                allInvalidSettlementCoordinates.add("x" + (building.x().intValue() + 1) + "y" + building.y() + "z" + (building.z().intValue() - 1) + "_" + "6");
                //building place on top of current building
                allInvalidSettlementCoordinates.add("x" + (building.x().intValue() + 1) + "y" + (building.y().intValue() + 1) + "z" + (building.z().intValue() - 2) + "_" + "6");
            } else if (building.side().intValue() == 6) {
                //building itself
                allInvalidSettlementCoordinates.add("x" + building.x().toString() + "y" + building.y() + "z" + building.z() + "_" + building.side());
                //building place top left from current building
                allInvalidSettlementCoordinates.add("x" + (building.x().intValue() - 1) + "y" + building.y() + "z" + (building.z().intValue() + 1) + "_" + "0");
                //building place top left from current building
                allInvalidSettlementCoordinates.add("x" + building.x() + "y" + (building.y().intValue() - 1) + "z" + (building.z().intValue() + 1) + "_" + "0");
                //building place bottom of current building
                allInvalidSettlementCoordinates.add("x" + (building.x().intValue() - 1) + "y" + (building.y().intValue() - 1) + "z" + (building.z().intValue() + 2) + "_" + "0");
            }
        }
        return allInvalidSettlementCoordinates;
    }

    public List<String> getPossibleRoadPlacements(int x, int y, int z, int side) {
        List<String> possibleRoadPlacements = new ArrayList<>();

        //TODOs: get size from server in V3
        int gameFieldSize = 2;
        if (side == 0) {
            //road bottom left
            if (x != ((gameFieldSize + 1) * (-1))) {
                possibleRoadPlacements.add("x" + x + "y" + y + "z" + z + "_" + 11);
            }
            //road bottom right
            if (y != ((gameFieldSize + 1) * (-1))) {
                possibleRoadPlacements.add("x" + (x + 1) + "y" + y + "z" + (z - 1) + "_" + 7);
            }
            //road on top
            if (z != gameFieldSize * (-1)) {
                possibleRoadPlacements.add("x" + x + "y" + (y + 1) + "z" + (z - 1) + "_" + 3);
            }
        } else if (side == 6) {
            //road top left
            if (y != gameFieldSize + 1) {
                possibleRoadPlacements.add("x" + x + "y" + y + "z" + z + "_" + 7);
            }
            //road top right
            if (x != gameFieldSize + 1) {
                possibleRoadPlacements.add("x" + x + "y" + (y - 1) + "z" + (z + 1) + "_" + 11);
            }
            //road bottom
            if (z != gameFieldSize) {
                possibleRoadPlacements.add("x" + (x - 1) + "y" + y + "z" + (z + 1) + "_" + 3);
            }
        }
        return possibleRoadPlacements;
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

        Image image1 = null;
        Image image2 = null;

        switch (diceNumber) {
            // according to swagger roll is between 1 and 12
            case 1 -> {
                image1 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border1.png")).toString());
                this.diceTwo.setVisible(false);
            }
            case 2 -> {
                image1 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border1.png")).toString());
                image2 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border1.png")).toString());
            }

            case 3 -> {
                image1 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border2.png")).toString());
                image2 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border1.png")).toString());
            }

            case 4 -> {
                image1 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border3.png")).toString());
                image2 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border1.png")).toString());
            }

            case 5 -> {
                image1 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border4.png")).toString());
                image2 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border1.png")).toString());
            }

            case 6 -> {
                image1 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border5.png")).toString());
                image2 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border1.png")).toString());
            }

            case 7 -> {
                image1 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border6.png")).toString());
                image2 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border1.png")).toString());
            }

            case 8 -> {
                image1 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border6.png")).toString());
                image2 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border2.png")).toString());
            }

            case 9 -> {
                image1 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border6.png")).toString());
                image2 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border3.png")).toString());
            }

            case 10 -> {
                image1 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border6.png")).toString());
                image2 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border4.png")).toString());
            }

            case 11 -> {
                image1 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border6.png")).toString());
                image2 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border5.png")).toString());
            }

            case 12 -> {
                image1 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border6.png")).toString());
                image2 = new Image(Objects.requireNonNull(Main.class.getResource("view/assets/dieWhite_border6.png")).toString());
            }
        }
        if (image1 != null) {
            this.diceOne.setImage(image1);
        }
        if (image2 != null) {
            this.diceTwo.setImage(image2);
        }
    }
}

