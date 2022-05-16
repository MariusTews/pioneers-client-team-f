package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Group;
import de.uniks.pioneers.model.Message;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.*;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static de.uniks.pioneers.Constants.*;

public class LobbyController implements Controller {

    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final ObservableList<Game> games = FXCollections.observableArrayList();
    private final ObservableList<Group> groups = FXCollections.observableArrayList();
    private final List<Message> messages = new ArrayList<>();
    private final List<String> deletedMessages = new ArrayList<>();
    private final List<UserListSubController> userSubCons = new ArrayList<>();
    private final List<GameListSubController> gameSubCons = new ArrayList<>();
    private final List<DirectChatStorage> directChatStorages = new ArrayList<>();

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
    private final UserService userService;
    private final GameService gameService;
    private final GroupService groupService;
    private final MessageService messageService;
    private final AuthService authService;
    private final MemberService memberService;
    private final EventListener eventListener;
    private final Provider <LoginController> loginController;
    private final Provider<RulesScreenController> rulesScreenController;
    private final Provider<CreateGameController> createGameController;
    private final Provider<EditUserController> editUserController;
    private final Provider<GameLobbyController> gameLobbyController;

    private final CompositeDisposable disposable = new CompositeDisposable();
    private Disposable tabDisposable;
    private DirectChatStorage currentDirectStorage;

    String ownUsername = "";

    @Inject
    public LobbyController(App app,
                           IDStorage idStorage,
                           UserService userService,
                           GameService gameService,
                           GroupService groupService,
                           MessageService messageService,
                           AuthService authService,
                           MemberService memberService,
                           EventListener eventListener,
                           Provider<LoginController> loginController,
                           Provider<RulesScreenController> rulesScreenController,
                           Provider<CreateGameController> createGameController,
                           Provider<EditUserController> editUserController,
                           Provider<GameLobbyController> gameLobbyController) {

        this.app = app;
        this.idStorage = idStorage;
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
        this.editUserController = editUserController;
        this.gameLobbyController = gameLobbyController;
    }

    @Override
    public void init() {
        gameService.findAllGames().observeOn(FX_SCHEDULER).subscribe(this.games::addAll);
        userService.findAllUsers().observeOn(FX_SCHEDULER).subscribe(this::loadUsers);
        groupService.getAll().observeOn(FX_SCHEDULER).subscribe(this::loadGroups);

        disposable.add(eventListener.listen("users.*.*", User.class).observeOn(FX_SCHEDULER).subscribe(this::handleUserEvents));
        disposable.add(eventListener.listen("games.*.*",Game.class).observeOn(FX_SCHEDULER).subscribe(this::handleGameEvents));
    }

    @Override
    public void destroy() {
        this.userSubCons.forEach(UserListSubController::destroy);
        this.gameSubCons.forEach(GameListSubController::destroy);
        this.userSubCons.clear();
        this.gameSubCons.clear();
        this.directChatStorages.clear();

        eventListener.listen("users.*.*", User.class).unsubscribeOn(FX_SCHEDULER);
        eventListener.listen("games.*.*",Game.class).unsubscribeOn(FX_SCHEDULER);

        disposable.dispose();
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

        this.users.addListener((ListChangeListener<? super User>) c -> {
            ((VBox) this.userScrollPane.getContent()).getChildren().setAll(c.getList().stream().sorted(userComparator).map(this::renderUser).toList());
        });

        this.games.addListener((ListChangeListener<? super Game>) c -> {
            ((VBox) this.gamesScrollPane.getContent()).getChildren().setAll(c.getList().stream().sorted(gameComparator).map(this::renderGame).toList());
        });

        this.tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> handleTabSwitching(oldValue, newValue));
        tabPane.tabClosingPolicyProperty().set(TabPane.TabClosingPolicy.SELECTED_TAB);
        return parent;
    }

    private void handleTabSwitching(Tab oldValue, Tab newValue) {
        for (DirectChatStorage directChatStorage : directChatStorages) {
            if (directChatStorage.getTab().equals(oldValue) && tabDisposable != null) {

                tabDisposable.dispose();
            }
        }
        for (DirectChatStorage directChatStorage : directChatStorages) {
            if (directChatStorage.getTab().equals(newValue)) {
                this.currentDirectStorage = directChatStorage;

                tabDisposable = eventListener.listen("groups." + directChatStorage.getGroupId() + ".messages.*.*", Message.class).observeOn(FX_SCHEDULER).subscribe(messageEvent -> {
                    if (messageEvent.event().endsWith(CREATED)) {
                        this.messages.add(messageEvent.data());
                        renderNewMessage(directChatStorage,messageEvent.data());
                    } else if (messageEvent.event().endsWith(DELETED)) {
                        this.deletedMessages.add(messageEvent.data()._id());
                        loadDirectMessages(directChatStorage.getGroupId(),directChatStorage.getUserId(),directChatStorage.getUserName(),newValue);
                    }
                });
            }
        }
    }

    public void rulesButtonPressed(ActionEvent ignoredEvent) {
        final RulesScreenController controller = rulesScreenController.get();
        app.show(controller);
    }

    public void logoutButtonPressed(ActionEvent ignoredEvent) {
        logout();
    }

    public void logout() {
        userService.statusUpdate(idStorage.getID(), "offline")
                        .observeOn(FX_SCHEDULER)
                                .subscribe();
        authService.logout()
                        .subscribeOn(FX_SCHEDULER)
                                .subscribe();
        app.show(loginController.get());
    }

    public void sendButtonPressed(ActionEvent ignoredEvent) {
        checkMessageField();
    }

    public void editButtonPressed(ActionEvent ignoredEvent) {
        final EditUserController controller = editUserController.get();
        app.show(controller);
    }

    public void createGameButtonPressed(ActionEvent ignoredEvent) {
        final CreateGameController controller = createGameController.get();
        app.show(controller);
    }

    public void enterKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            checkMessageField();
        }
    }

    private void checkMessageField() {
        if (!chatMessageField.getText().isEmpty()) {
            if(currentDirectStorage != null) {
                this.messageService.send(GROUPS, currentDirectStorage.getGroupId(), chatMessageField.getText())
                        .observeOn(FX_SCHEDULER)
                        .subscribe();
            }
        }
    }

    private Node renderUser(User user){
        for(UserListSubController subCon: this.userSubCons) {
            if (subCon.getId().equals(user._id())){
                return subCon.getParent();
            }
        }
        UserListSubController userCon = new UserListSubController(this.app, this, user, idStorage);
        userSubCons.add(userCon);
        return userCon.render();
    }

    private Node renderGame(Game game){
        for(GameListSubController subCon: this.gameSubCons) {
            if (subCon.getId().equals(game._id())){
                return subCon.getParent();
            }
        }
        GameListSubController gameCon = new GameListSubController(this.app,game, this);
        gameSubCons.add(gameCon);
        return gameCon.render();
    }

    private void handleUserEvents(Event<User> userEvent) {
        final User user = userEvent.data();

        if (userEvent.event().endsWith(CREATED)) {
            this.users.add(user);
        }

        else if (userEvent.event().endsWith(DELETED)) {
            removeUserSubCon(user);
            this.users.removeIf(u -> u._id().equals(user._id()));
        }

        else if (userEvent.event().endsWith(UPDATED)) {
            for (User updatedUser : this.users) {
                if (updatedUser._id().equals(user._id())) {
                    removeUserSubCon(user);
                    this.users.set(this.users.indexOf(updatedUser),user);
                    break;
                }
            }
        }
    }

    private void removeUserSubCon(User updatedUser) {
        for (UserListSubController subCon: this.userSubCons) {
            if(subCon.getId().equals(updatedUser._id())){
                this.userSubCons.remove(subCon);
                break;
            }
        }
    }

    private void handleGameEvents(Event<Game> gameEvent) {
        final Game game = gameEvent.data();

        if (gameEvent.event().endsWith(CREATED)) {
            this.games.add(game);
        }

        else if (gameEvent.event().endsWith(DELETED)) {
            removeGameSubcon(game);
            this.games.removeIf(u -> u._id().equals(game._id()));
        }

        else if (gameEvent.event().endsWith(UPDATED)) {
            for (Game updatedGame : this.games) {
                if (updatedGame._id().equals(game._id())) {
                    removeGameSubcon(updatedGame);
                    this.games.set(this.games.indexOf(updatedGame),game);
                    break;
                }
            }
        }
    }

    private void removeGameSubcon(Game updatedGame) {
        for (GameListSubController subCon: this.gameSubCons) {
            if(subCon.getId().equals(updatedGame._id())){
                this.gameSubCons.remove(subCon);
                break;
            }
        }
    }

    public void openDirectChat(User user) {
        if (user._id().equals(this.idStorage.getID())){
            return;
        }
        List<Tab> tabs = this.tabPane.getTabs();

        for (Tab tab: tabs){
            if (tab.getText().equals(DirectMessage + user.name())){
                checkGroups(user, tab);
                return;
            }
        }

        Tab tab = new Tab();
        tab.setText(DirectMessage + user.name());
        tab.setClosable(true);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(new VBox(3));
        tab.setContent(scrollPane);

        checkGroups(user, tab);
        this.tabPane.getTabs().add(tab);
    }

    private void checkGroups(User user, Tab tab) {

        for (Group group: this.groups){
            if (group.members().size() == 2 && group.members().contains(user._id()) && group.members().contains(this.idStorage.getID())) {
                for (DirectChatStorage storage: directChatStorages) {
                    if(storage.getGroupId().equals(group._id()) && storage.getUserId().equals(user._id()))
                    {
                        return;
                    }
                }
                addToDirectChatStorage(group._id(),user._id(),user.name(), tab);
                loadDirectMessages(group._id(), user._id(),user.name(), tab);
                return;
            }
        }
        String userId = this.idStorage.getID() ;

        List<String> toAdd = new ArrayList<>();
        toAdd.add(userId);
        toAdd.add(user._id());

        this.groupService.createGroup(toAdd).observeOn(FX_SCHEDULER).subscribe(group -> {
            addToDirectChatStorage(group._id(),user._id(),user.name(), tab);
            loadDirectMessages(group._id(), user._id(),user.name(), tab);
            this.groups.add(group);
        });
    }

    private void loadUsers(List<User> users) {
        for (User user: users){
            if (user._id().equals(this.idStorage.getID())){
                this.ownUsername = user.name();
            }
        }

        List<User> online = users.stream().filter(user -> user.status().equals("online")).toList();
        List<User> offline = users.stream().filter(user -> user.status().equals("offline")).toList();
        this.users.addAll(online);
        this.users.addAll(offline);
    }

    private void loadGroups(List<Group> groups) {
        this.groups.addAll(groups);
    }

    private void loadDirectMessages(String groupId, String userId,String username, Tab tab){
        this.messageService.getAllMessages(GROUPS,groupId).observeOn(FX_SCHEDULER).subscribe(messages -> {
            this.messages.clear();
            this.messages.addAll(messages);
            ((VBox) ((ScrollPane) tab.getContent()).getContent()).getChildren().clear();

            for (Message message: this.messages){
                if (!this.deletedMessages.contains(message._id())) {
                    Label label = new Label();
                    initRightClick(label,message._id(),message.sender(),groupId);
                    if (message.sender().equals(idStorage.getID())) {
                        label.setText(ownUsername + ": " + message.body());
                        ((VBox) ((ScrollPane) tab.getContent()).getContent()).getChildren().add(label);
                    } else if (message.sender().equals(userId)) {
                        label.setText(username + ": " + message.body());
                        ((VBox) ((ScrollPane) tab.getContent()).getContent()).getChildren().add(label);
                    }
                }
                else {
                    ((VBox) ((ScrollPane) tab.getContent()).getContent()).getChildren().add(new Label("message was deleted"));
                }
            }
        });
    }

    private void addToDirectChatStorage( String groupId, String userId, String userName, Tab tab) {
        DirectChatStorage directChatStorage = new DirectChatStorage();
        directChatStorage.setGroupId(groupId);
        directChatStorage.setUserId(userId);
        directChatStorage.setUserName(userName);
        directChatStorage.setTab(tab);
        this.directChatStorages.add(directChatStorage);
    }

    private void renderNewMessage(DirectChatStorage storage, Message message) {
        if (message.sender().equals(idStorage.getID())){
            ((VBox) ((ScrollPane) storage.getTab().getContent()).getContent()).getChildren().add(new Label( ownUsername +  ": " +message.body()));
        }
        else if (message.sender().equals(storage.getUserId())){
            ((VBox) ((ScrollPane) storage.getTab().getContent()).getContent()).getChildren().add(new Label( storage.getUserName() +  ": " +message.body()));
        }
    }
      
    public void joinGame(Game game) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter the password");
        dialog.setHeaderText("password");
        dialog.showAndWait()
                .ifPresent(password -> {
                    this.memberService.join(game._id(), password)
                            .observeOn(FX_SCHEDULER)
                            .doOnError(error -> {
                                if ("HTTP 401 ".equals(error.getMessage())) {
                                    new Alert(Alert.AlertType.ERROR, "wrong password")
                                            .showAndWait();
                                }
                            })
                            .subscribe(result -> {
                                app.show(gameLobbyController.get());
                            });
                });
    }

    private void initRightClick(Label label, String messageId, String sender, String groupId) {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem menuItem = new MenuItem("delete");

        contextMenu.getItems().add(menuItem);
        label.setContextMenu(contextMenu);

        menuItem.setOnAction(event -> {
            if (sender.equals(this.idStorage.getID())) {
                messageService
                        .delete(GROUPS, groupId, messageId)
                        .observeOn(FX_SCHEDULER)
                        .subscribe();
            } else {
                new Alert(Alert.AlertType.WARNING, "Deleting other members messages is not possible.")
                        .showAndWait();
            }
        });
    }
}
