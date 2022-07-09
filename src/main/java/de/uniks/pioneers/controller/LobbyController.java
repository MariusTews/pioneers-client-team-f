package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.websocket.EventListener;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.service.*;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static de.uniks.pioneers.Constants.*;
import static de.uniks.pioneers.Constants.gameComparator;
import static java.util.concurrent.TimeUnit.SECONDS;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class LobbyController implements Controller {

    private final ObservableList<User> users = FXCollections.observableArrayList();

    private final ObservableList<Member> members = FXCollections.observableArrayList();
    private final ObservableList<Game> games = FXCollections.observableArrayList();
    private final ObservableList<Group> groups = FXCollections.observableArrayList();
    private final ObservableList<Message> messages = FXCollections.observableArrayList();

    //message for Lobby
    private final ObservableList<Message> lobby_messages = FXCollections.observableArrayList();

    private final List<String> deletedMessages = new ArrayList<>();
    private final List<String> deletedAllMessages = new ArrayList<>();
    private final List<UserListSubController> userSubCons = new ArrayList<>();
    private final List<GameListSubController> gameSubCons = new ArrayList<>();
    private final List<DirectChatStorage> directChatStorages = new ArrayList<>();
    //store id and user
    private final HashMap<String, User> memberHash = new HashMap<>();

    @FXML
    public ScrollPane gamesScrollPane;
    @FXML
    public ScrollPane userScrollPane;
    @FXML
    public Button rulesButton;
    @FXML
    public Label userWelcomeLabel;
    @FXML
    public Button logoutButton;
    @FXML
    public TabPane tabPane;
    @FXML
    public Tab allTab;
    @FXML
    public TextField chatMessageField;
    @FXML
    public Button sendButton;
    @FXML
    public Button editUserButton;
    @FXML
    public Button createGameButton;

    private final App app;
    private final IDStorage idStorage;

    private final GameStorage gameStorage;

    private final RefreshTokenStorage refreshTokenStorage;

    private final UserService userService;
    private final GameService gameService;
    private final GroupService groupService;
    private final MessageService messageService;
    private final AuthService authService;
    private final MemberService memberService;
    private final EventListener eventListener;
    private final Provider<LoginController> loginController;
    private final Provider<RulesScreenController> rulesScreenController;
    private final Provider<CreateGameController> createGameController;

    private final Provider<GameLobbyController> gameLobbyController;
    private final Provider<EditUserController> editUserController;
    private final Provider<GameScreenController> gameScreenController;

    private final PioneersService pioneersService;

    private final CompositeDisposable disposable = new CompositeDisposable();
    public Button rejoinButton;
    private Disposable tabDisposable;
    private DirectChatStorage currentDirectStorage;

    String ownUsername = "";
    String ownAvatar = null;

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    @Inject
    public LobbyController(App app,
                           IDStorage idStorage,
                           GameStorage gameStorage, RefreshTokenStorage refreshTokenStorage, UserService userService,
                           GameService gameService,
                           GroupService groupService,
                           MessageService messageService,
                           AuthService authService,
                           MemberService memberService,
                           EventListener eventListener,
                           Provider<LoginController> loginController,
                           Provider<RulesScreenController> rulesScreenController,
                           Provider<CreateGameController> createGameController,
                           Provider<GameLobbyController> gameLobbyController,
                           Provider<EditUserController> editUserController,
                           Provider<GameScreenController> gameScreenController,
                           PioneersService pioneersService) {

        this.app = app;
        this.idStorage = idStorage;
        this.gameStorage = gameStorage;
        this.refreshTokenStorage = refreshTokenStorage;
        this.userService = userService;
        this.gameService = gameService;
        this.groupService = groupService;
        this.messageService = messageService;
        this.authService = authService;
        this.memberService = memberService;
        this.eventListener = eventListener;
        this.loginController = loginController;
        this.rulesScreenController = rulesScreenController;
        this.createGameController = createGameController;
        this.gameLobbyController = gameLobbyController;
        this.editUserController = editUserController;
        this.gameScreenController = gameScreenController;
        this.pioneersService = pioneersService;
    }

    @Override
    public void init() {

        if(this.gameStorage.getId() != null) {
            memberService.getAllGameMembers(this.gameStorage.getId())
                    .observeOn(FX_SCHEDULER).subscribe(this.members::setAll);
        }
        gameService.findAllGames().observeOn(FX_SCHEDULER).subscribe(this::loadGames);
        userService.findAllUsers().observeOn(FX_SCHEDULER).subscribe(this::loadUsers);
        groupService.getAll().observeOn(FX_SCHEDULER).subscribe(this::loadGroups);

        disposable.add(eventListener.listen("users.*.*", User.class).observeOn(FX_SCHEDULER).subscribe(this::handleUserEvents));
        disposable.add(eventListener.listen("games.*.*", Game.class).observeOn(FX_SCHEDULER).subscribe(this::handleGameEvents));
		disposable.add(eventListener.listen("group.*.*", Group.class).observeOn(FX_SCHEDULER).subscribe(this::handleGroupEvents));

        //listen to messages on lobby on Global channel
        disposable.add(eventListener
                .listen("global." + LOBBY_ID + ".messages.*.*", Message.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(this::handleAllTabMessages));

        //refreshed Token and runs for three hour
        if(this.gameStorage.getId() == null) {
            beepForAHour();
        }

        this.app.getStage().setOnCloseRequest(e -> {
            actionOnclose();
            e.consume();
        });

    }

    @Override
    public void destroy() {
        this.userSubCons.forEach(UserListSubController::destroy);
        this.gameSubCons.forEach(GameListSubController::destroy);
        this.userSubCons.clear();
        this.gameSubCons.clear();
        this.directChatStorages.clear();

        disposable.dispose();
    }

    //call this method every 30 minutes to refresh refreshToken and ActiveToken
    public void beepForAHour() {
        String refreshToken = this.refreshTokenStorage.getRefreshToken();
        final Runnable beeper = new Runnable() {
            public void run() {
                authService.refreshToken(refreshToken).
                        observeOn(FX_SCHEDULER).subscribe();
            }
        };
        final ScheduledFuture<?> beeperHandle =
                scheduler.scheduleAtFixedRate(beeper, 10, 30*60, SECONDS);
        scheduler.schedule(new Runnable() {
            public void run() { beeperHandle.cancel(true); }
        }, 60 * 60, SECONDS);
    }


    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/LobbyScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        //make the rejoin button visible
        //based upon if a user is in game or not
        if (this.gameStorage.getId() != null) {
            memberService.getAllGameMembers(this.gameStorage.getId()).observeOn(FX_SCHEDULER)
                    .subscribe(result -> {
                        boolean trace = true;
                        for (Member member : result) {
                            if (member.userId().equals(this.idStorage.getID())) {
                                rejoinButton.disableProperty().set(false);
                                trace = false;
                                break;
                            }
                        }
                        if (trace) {
                            rejoinButton.disableProperty().set(true);
                        }
                    });
        } else {
            rejoinButton.disableProperty().set(true);
        }

        this.users.addListener((ListChangeListener<? super User>) c -> ((VBox) this.userScrollPane.getContent())
                .getChildren().setAll(c.getList().stream().sorted(userComparator).map(this::renderUser).toList()));

        this.games.addListener((ListChangeListener<? super Game>) c -> ((VBox) this.gamesScrollPane.getContent())
                .getChildren().setAll(c.getList().stream().sorted(gameComparator).map(this::renderGame).toList()));


        this.tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                handleTabSwitching(oldValue, newValue));
        tabPane.tabClosingPolicyProperty().set(TabPane.TabClosingPolicy.SELECTED_TAB);


        return parent;
    }

    //takes action when the application is forcefully closed
    //such as logging out
    private void actionOnclose() {
        userService.statusUpdate(idStorage.getID(), "offline")
                .observeOn(FX_SCHEDULER)
                .subscribe(er -> {
                    System.exit(0);
                });
    }


    private void handleTabSwitching(Tab oldValue, Tab newValue) {

        for (DirectChatStorage directChatStorage : directChatStorages) {
            if (directChatStorage.getTab() != null && directChatStorage.getTab().equals(oldValue) && tabDisposable != null) {
                this.currentDirectStorage = null;
                tabDisposable.dispose();
            }
        }
        for (DirectChatStorage directChatStorage : directChatStorages) {
            if (directChatStorage.getTab() != null && directChatStorage.getTab().equals(newValue)) {
                this.currentDirectStorage = directChatStorage;
                this.loadMessages(currentDirectStorage.getGroupId(), currentDirectStorage.getTab());
                tabDisposable = eventListener.listen("groups." + directChatStorage.getGroupId() + ".messages.*.*", Message.class).observeOn(FX_SCHEDULER).subscribe(messageEvent -> {
                    if (messageEvent.event().endsWith(CREATED)) {
                        this.messages.add(messageEvent.data());
                        renderSingleMessage(directChatStorage.getGroupId(), directChatStorage.getTab(), messageEvent.data());
                    } else if (messageEvent.event().endsWith(DELETED)) {
                        this.deletedMessages.add(messageEvent.data()._id());
                        loadMessages(directChatStorage.getGroupId(), newValue);
                    }
                });
            }
        }
    }

    public void rulesButtonPressed() {
        final RulesScreenController controller = rulesScreenController.get();
        app.show(controller);
    }

    public void logoutButtonPressed() {
        logout();
    }

    public void logout() {
        userService.statusUpdate(idStorage.getID(), "offline")
                .observeOn(FX_SCHEDULER)
                .subscribe();
        authService.logout()
                .subscribeOn(FX_SCHEDULER)
                .subscribe(onSuccess -> app.show(loginController.get()), onError -> {
                });
    }

    public void sendButtonPressed() {
        checkMessageField();
    }

    public void editButtonPressed() {
        final EditUserController controller = editUserController.get();
        app.show(controller);
    }

    public void createGameButtonPressed() {
        //makes sure if user in game or not , and depending on that
        //allows user to create the game
        if (this.gameStorage.getId() != null) {
            memberService.getAllGameMembers(this.gameStorage.getId()).observeOn(FX_SCHEDULER)
                    .subscribe(result -> {
                        boolean trace = true;
                        for (Member member : result) {
                            if (member.userId().equals(this.idStorage.getID())) {
                                Alert alert = new Alert(Alert.AlertType.ERROR, "You cannot create Game while being part of another Game");
                                // Change style of error alert
                                DialogPane dialogPane = alert.getDialogPane();
                                dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
                                        .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
                                alert.showAndWait();
                                trace = false;
                                break;
                            }
                        }
                        if (trace) {
                            final CreateGameController controller = createGameController.get();
                            app.show(controller);
                        }
                    });
        } else {
            final CreateGameController controller = createGameController.get();
            app.show(controller);
        }
    }

    public void enterKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            checkMessageField();
        }
    }

    private void checkMessageField() {
        if (!chatMessageField.getText().isEmpty()) {
            if (currentDirectStorage != null) {
                this.messageService.send(GROUPS, currentDirectStorage.getGroupId(), chatMessageField.getText())
                        .observeOn(FX_SCHEDULER)
                        .subscribe(result -> this.chatMessageField.setText(""));
            } else {
                this.messageService.send(GLOBAL, LOBBY_ID, chatMessageField.getText())
                        .observeOn(FX_SCHEDULER)
                        .subscribe();
                this.chatMessageField.clear();
            }
        }
    }

    private Node renderUser(User user) {
        if (user._id().equals(this.idStorage.getID())) {
            this.userWelcomeLabel.setText(WELCOME + user.name() + "!");
        }
        for (UserListSubController subCon : this.userSubCons) {
            if (subCon.getId().equals(user._id())) {
                return subCon.getParent();
            }
        }
        UserListSubController userCon = new UserListSubController(this, user, idStorage);
        userSubCons.add(userCon);
        return userCon.render();
    }

    private Node renderGame(Game game) {
        for (GameListSubController subCon : this.gameSubCons) {
            if (subCon.getId().equals(game._id())) {
                return subCon.getParent();
            }
        }
        GameListSubController gameCon = new GameListSubController(game, this);
        gameSubCons.add(gameCon);
        return gameCon.render();
    }

    private void handleUserEvents(Event<User> userEvent) {
        final User user = userEvent.data();

        if (userEvent.event().endsWith(CREATED)) {
            this.users.add(user);
        } else if (userEvent.event().endsWith(DELETED)) {
            removeUserSubCon(user);
            this.users.removeIf(u -> u._id().equals(user._id()));
        } else if (userEvent.event().endsWith(UPDATED)) {

            for (DirectChatStorage directChatStorage : directChatStorages) {
                if (directChatStorage.getUser()._id().equals(user._id())) {

                    directChatStorage.setUser(user);
                }
            }
            for (User updatedUser : this.users) {
                if (updatedUser._id().equals(user._id())) {
                    removeUserSubCon(user);
                    this.users.set(this.users.indexOf(updatedUser), user);
                    break;
                }
            }
        }
    }

    private void removeUserSubCon(User updatedUser) {
        for (UserListSubController subCon : this.userSubCons) {
            if (subCon.getId().equals(updatedUser._id())) {
                this.userSubCons.remove(subCon);
                break;
            }
        }
    }

    private void handleGameEvents(Event<Game> gameEvent) {
        final Game game = gameEvent.data();

        if (gameEvent.event().endsWith(CREATED)) {
            this.games.add(game);
        } else if (gameEvent.event().endsWith(DELETED)) {
            removeGameSubcon(game);
            this.games.removeIf(u -> u._id().equals(game._id()));
        } else if (gameEvent.event().endsWith(UPDATED)) {
            for (Game updatedGame : this.games) {
                if (updatedGame._id().equals(game._id())) {
                    removeGameSubcon(updatedGame);
                    this.games.set(this.games.indexOf(updatedGame), game);
                    break;
                }
            }
        }
    }

    private void removeGameSubcon(Game updatedGame) {
        for (GameListSubController subCon : this.gameSubCons) {
            if (subCon.getId().equals(updatedGame._id())) {
                this.gameSubCons.remove(subCon);
                break;
            }
        }
    }

	// Handle group events, so the users do not end up in different groups when opening the direct chat
	private void handleGroupEvents(Event<Group> groupEvent) {
		final Group group = groupEvent.data();

		if (groupEvent.event().endsWith(CREATED)) {
			this.groups.add(group);
		} else if (groupEvent.event().endsWith(DELETED)) {
			this.groups.removeIf(u -> u._id().equals(group._id()));
		} else if (groupEvent.event().endsWith(UPDATED)) {
			for (Group updatedGroup : this.groups) {
				if (updatedGroup._id().equals(group._id())) {
					this.groups.set(this.groups.indexOf(updatedGroup), group);
					break;
				}
			}
		}
	}

    public void openDirectChat(User user) {

        List<Tab> tabs = this.tabPane.getTabs();

        for (Tab tab : tabs) {
            if (tab.getText().equals(DirectMessage + user.name())) {
                checkGroups(user, tab);
                return;
            }
        }

        Tab tab = new Tab();
        tab.setText(DirectMessage + user.name());
        tab.setClosable(true);
        tab.setOnClosed(event -> {
            for (DirectChatStorage storage : this.directChatStorages) {

                if (storage != null && storage.getTab() != null && storage.getTab().equals(tab)) {
                    storage.setTab(null);
                }
            }
        });
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(new VBox(3));
        tab.setContent(scrollPane);

        checkGroups(user, tab);
        this.tabPane.getTabs().add(tab);
    }

    private void checkGroups(User user, Tab tab) {

        for (Group group : this.groups) {
            if (group.members().size() == 2 && group.members().contains(user._id()) && group.members().contains(this.idStorage.getID())) {
                for (DirectChatStorage storage : directChatStorages) {
                    User storageUser = storage.getUser();
                    if (storage.getGroupId().equals(group._id()) && storageUser._id().equals(user._id())) {
                        storage.setTab(tab);
                        loadMessages(storage.getGroupId(), storage.getTab());
                        return;
                    }
                }
                addToDirectChatStorage(group._id(), user, tab);
                loadMessages(group._id(), tab);
                return;
            }
        }
        String userId = this.idStorage.getID();

        List<String> toAdd = new ArrayList<>();
        toAdd.add(userId);
        toAdd.add(user._id());

        this.groupService.createGroup(toAdd).observeOn(FX_SCHEDULER).subscribe(group -> {
            addToDirectChatStorage(group._id(), user, tab);
            loadMessages(group._id(), tab);
            this.groups.add(group);
        });
    }

    private void loadUsers(List<User> users) {

        for (User user : users) {
            memberHash.put(user._id(), user);
        }

        List<User> online = users.stream().filter(user -> user.status().equals("online")).toList();
        List<User> offline = users.stream().filter(user -> user.status().equals("offline")).toList();
        this.users.addAll(online);
        this.users.addAll(offline);

        //get all messages from the user that are in lobby
        messageService
                .getAllMessages(GLOBAL, LOBBY_ID)
                .observeOn(FX_SCHEDULER)
                .subscribe(col -> {
                    this.lobby_messages.setAll(col);
                    for (Message message: lobby_messages) {
                        if (!this.deletedAllMessages.contains(message._id())) {
                            renderSingleMessage(null, allTab, message);
                        }
                    }
                });
    }

    private void loadGames(List<Game> games) {
        List<Game> accessible = games.stream().filter(game -> (!game.started() && (int) game.members() < MAX_MEMBERS)).toList();
        List<Game> notAccessible = games.stream().filter(game -> (game.started() || game.members().equals(MAX_MEMBERS))).toList();

        this.games.addAll(accessible);
        this.games.addAll(notAccessible);
    }

    private void loadGroups(List<Group> groups) {
        this.groups.addAll(groups);
    }


    private void loadMessages(String groupId, Tab tab) {
        if (tab.equals(allTab)) {
            this.messageService.getAllMessages(GLOBAL, LOBBY_ID).observeOn(FX_SCHEDULER).subscribe(messages -> {
                this.lobby_messages.clear();
                this.lobby_messages.addAll(messages);
                ((VBox) ((ScrollPane) tab.getContent()).getContent()).getChildren().clear();

                for (Message message : this.lobby_messages) {
                    if (!this.deletedAllMessages.contains(message._id())) {
                        renderSingleMessage(null, allTab, message);
                    }
                }
            });
        }
        else {
            this.messageService.getAllMessages(GROUPS, groupId).observeOn(FX_SCHEDULER).subscribe(messages -> {
                this.messages.clear();
                this.messages.addAll(messages);
                ((VBox) ((ScrollPane) tab.getContent()).getContent()).getChildren().clear();

                for (Message message : this.messages) {
                    if (!this.deletedMessages.contains(message._id())) {
                        renderSingleMessage(groupId, tab, message);
                    }
                }
            });
        }
    }

    private void addToDirectChatStorage(String groupId, User user, Tab tab) {
        DirectChatStorage directChatStorage = new DirectChatStorage();
        directChatStorage.setGroupId(groupId);
        directChatStorage.setUser(user);
        directChatStorage.setTab(tab);
        this.directChatStorages.add(directChatStorage);
    }

    private  void handleAllTabMessages(Event<Message> event) {
        final Message message = event.data();
        if (event.event().endsWith(CREATED)) {
            this.lobby_messages.add(message);
            renderSingleMessage(null, allTab, message);
        } else if (event.event().endsWith(DELETED)) {
            this.deletedAllMessages.add(message._id());
            this.lobby_messages.removeIf(m -> m._id().equals(message._id()));
            this.loadMessages(null, allTab);
        }
    }

    private void renderSingleMessage(String groupID, Tab tab, Message message) {

        HBox box = new HBox(3);
        Label label = new Label();
        ImageView imageView = new ImageView();
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);
        if (this.memberHash.get(message.sender()).avatar() != null) {
            imageView.setImage(new Image(this.memberHash.get(message.sender()).avatar()));
        }
        box.getChildren().add(imageView);
        label.setMinWidth(100);
        initRightClick(label, message._id(), message.sender(), groupID);
        label.setText(memberHash.get(message.sender()).name() + ": " + message.body());
        box.getChildren().add(label);

        ((VBox) ((ScrollPane) tab.getContent()).getContent()).getChildren().add(box);

        ((ScrollPane) tab.getContent()).vvalueProperty().bind(((VBox) ((ScrollPane) tab.getContent()).getContent()).heightProperty());
    }

    public void joinGame(Game game) {
        //allows to join a game, if the user does not belong to another game
        //otherwise user cannot join the game
        if (this.gameStorage.getId() != null) {
            memberService.getAllGameMembers(this.gameStorage.getId()).observeOn(FX_SCHEDULER)
                    .subscribe(result -> {
                        boolean trace = true;
                        for (Member member : result) {
                            if (member.userId().equals(this.idStorage.getID())) {
                                Alert alert = new Alert(Alert.AlertType.ERROR, "You can't join a game while \nbeing part of another game");
                                // Change style of error alert
                                DialogPane dialogPane = alert.getDialogPane();
                                dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
                                        .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
                                alert.showAndWait();
                                trace = false;
                                break;
                            }
                        }
                        if (trace) {
                            joinMessage(game);
                        }
                    });
        } else {
            joinMessage(game);
        }
    }

    private void joinMessage(Game game) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter the password");
        dialog.setHeaderText("password");
        // Change style of password input dialog
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
                .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
        dialog.showAndWait()
                .ifPresent(password -> this.memberService.join(game._id(), password)
                        .observeOn(FX_SCHEDULER)
                        .doOnError(error -> {
                            if ("HTTP 403 ".equals(error.getMessage())) {
                                Alert alert = new Alert(Alert.AlertType.ERROR, "wrong password");
                                // Change style of error alert
                                DialogPane errorPane = alert.getDialogPane();
                                errorPane.getStylesheets().add(Objects.requireNonNull(Main.class.
                                        getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
                                alert.showAndWait();
                            }
                        })
                        .subscribe(result -> app.show(gameLobbyController.get()), onError -> {
                        }));
    }

    private void initRightClick(Label label, String messageId, String sender, String groupId) {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem menuItem = new MenuItem("delete");

        contextMenu.getItems().add(menuItem);
        label.setOnMouseEntered(event -> label.setStyle("-fx-background-color: LIGHTGREY"));
        label.setOnMouseExited(event -> label.setStyle("-fx-background-color: TRANSPARENT"));
        label.setContextMenu(contextMenu);

        menuItem.setOnAction(event -> {
            if (sender.equals(this.idStorage.getID())) {
                if(groupId != null) {
                    messageService
                            .delete(GROUPS, groupId, messageId)
                            .observeOn(FX_SCHEDULER)
                            .subscribe();
                } else {
                    messageService
                            .delete(GLOBAL, LOBBY_ID, messageId)
                            .observeOn(FX_SCHEDULER)
                            .subscribe();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Deleting other members messages is not possible.");
                // set style of warning
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
                        .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
                alert.showAndWait();
            }
        });
    }

    //reactivate for the possibility of joining the game
    public void onRejoin() {
        boolean changeToPlayer = false;
        for (Member m: this.members) {
            if(m.gameId().equals(this.gameStorage.getId()) && m.userId().equals(this.idStorage.getID())
                && m.spectator()){
                this.app.show(gameScreenController.get());
                changeToPlayer = true;
                break;
            }
        }
        if(!changeToPlayer) {
            pioneersService.updatePlayer(this.gameStorage.getId(), this.idStorage.getID(), true)
                    .observeOn(FX_SCHEDULER)
                    .subscribe();
            this.app.show(gameScreenController.get());
        }

    }
}
