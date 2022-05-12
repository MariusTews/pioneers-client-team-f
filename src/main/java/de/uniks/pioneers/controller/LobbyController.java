package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Constants;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.AuthService;
import de.uniks.pioneers.service.GameService;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.UserService;
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
import java.util.Comparator;
import java.util.List;

import static de.uniks.pioneers.Constants.*;

public class LobbyController implements Controller {

    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<Game> games = FXCollections.observableArrayList();
    private List<UserListSubController> userSubCons = new ArrayList<>();
    private List<GameListSubController> gameSubCons = new ArrayList<>();

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
    private App app;
    private IDStorage idStorage;
    private UserService userService;
    private GameService gameService;
    private AuthService authService;
    private EventListener eventListener;
    private Provider <LoginController> loginController;
    private Provider<RulesScreenController> rulesScreenController;
    private Provider<CreateGameController> createGameController;
    private Provider<EditUserController> editUserController;

    @Inject
    public LobbyController(App app,
                           IDStorage idStorage,
                           UserService userService,
                           GameService gameService,
                           AuthService authService,
                           EventListener eventListener,
                           Provider<LoginController> loginController,
                           Provider<RulesScreenController> rulesScreenController,
                           Provider<CreateGameController> createGameController,
                           Provider<EditUserController> editUserController) {

        this.app = app;
        this.idStorage = idStorage;
        this.userService = userService;
        this.gameService = gameService;
        this.authService = authService;
        this.eventListener = eventListener;
        this.loginController = loginController;
        this.rulesScreenController = rulesScreenController;
        this.createGameController = createGameController;
        this.editUserController = editUserController;
    }

    @Override
    public void init() {
        gameService.findAllGames().observeOn(FX_SCHEDULER).subscribe(this.games::addAll);
        userService.findAllUsers().observeOn(FX_SCHEDULER).subscribe(users -> {
            List<User> online = users.stream().filter(user -> user.status().equals("online")).toList();
            List<User> offline = users.stream().filter(user -> user.status().equals("offline")).toList();
            this.users.addAll(online);
            this.users.addAll(offline);
        });

        eventListener.listen("users.*.*", User.class).observeOn(FX_SCHEDULER).subscribe(this::handleUserEvents);
        eventListener.listen("games.*.*",Game.class).observeOn(FX_SCHEDULER).subscribe(this::handleGameEvents);
    }

    @Override
    public void destroy() {
        this.userSubCons.forEach(UserListSubController::destroy);
        this.gameSubCons.forEach(GameListSubController::destroy);
        this.userSubCons.clear();
        this.gameSubCons.clear();

        eventListener.listen("users.*.*", User.class).unsubscribeOn(FX_SCHEDULER);
        eventListener.listen("games.*.*",Game.class).unsubscribeOn(FX_SCHEDULER);
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

        return parent;
    }

    public void rulesButtonPressed(ActionEvent event) {
        final RulesScreenController controller = rulesScreenController.get();
        app.show(controller);
    }

    public void logoutButtonPressed(ActionEvent event) {
        logout();
    }

    public void logout() {
        userService.statusUpdate(idStorage.getID(), "offline")
                        .observeOn(FX_SCHEDULER)
                                .subscribe();
        authService.logout()
                        .observeOn(FX_SCHEDULER)
                                .subscribe();
        app.show(loginController.get());
    }

    public void sendButtonPressed(ActionEvent event) {
        checkMessageField();
    }

    public void editButtonPressed(ActionEvent event) {
        final EditUserController controller = editUserController.get();
        app.show(controller);
    }

    public void createGameButtonPressed(ActionEvent event) {
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
            ((VBox) ((ScrollPane) this.allTab.getContent()).getContent()).getChildren().add(new Label("username: " + chatMessageField.getText()));
            chatMessageField.setText("");
        }
    }

    private Node renderUser(User user){
        for(UserListSubController subCon: this.userSubCons) {
            if (subCon.getId().equals(user._id())){
                return subCon.getParent();
            }
        }
        UserListSubController userCon = new UserListSubController(this.app,user);
        userSubCons.add(userCon);
        return userCon.render();
    }

    private Node renderGame(Game game){
        for(GameListSubController subCon: this.gameSubCons) {
            if (subCon.getId().equals(game._id())){
                return subCon.getParent();
            }
        }
        GameListSubController gameCon = new GameListSubController(this.app,game);
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
}
