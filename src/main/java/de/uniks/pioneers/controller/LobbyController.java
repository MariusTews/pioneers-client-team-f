package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.model.User;
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
import java.util.List;

import static de.uniks.pioneers.Constants.*;

public class LobbyController implements Controller {

    private ObservableList<User> users = FXCollections.observableArrayList();
    private List<UserListSubController> userSubCons = new ArrayList<>();

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
    public ListView gameListView;
    @FXML
    public Button editUserButton;
    @FXML
    public Button createGameButton;
    private App app;
    private UserService userService;
    private EventListener eventListener;
    private Provider <LoginController> loginController;
    private Provider<RulesScreenController> rulesScreenController;
    private Provider<CreateGameController> createGameController;
    private Provider<EditUserController> editUserController;

    @Inject
    public LobbyController(App app, UserService userService, EventListener eventListener,
                           Provider<LoginController> loginController,
                           Provider<RulesScreenController> rulesScreenController,
                           Provider<CreateGameController> createGameController,
                           Provider<EditUserController> editUserController) {

        this.app = app;
        this.userService = userService;
        this.eventListener = eventListener;
        this.loginController = loginController;
        this.rulesScreenController = rulesScreenController;
        this.createGameController = createGameController;
        this.editUserController = editUserController;
    }

    @Override
    public void init() {
        userService.findAllUsers().observeOn(FX_SCHEDULER).subscribe(users -> {
            List<User> online = users.stream().filter(user -> user.status().equals("online")).toList();
            List<User> offline = users.stream().filter(user -> user.status().equals("offline")).toList();
            this.users.addAll(online);
            this.users.addAll(offline);
        });

        eventListener.listen("users.*.*", User.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(userEvent -> {
                    final User user = userEvent.data();
                    if (userEvent.event().endsWith(CREATED))
                    {
                        this.users.add(user);
                    }
                    else if(userEvent.event().endsWith(DELETED))
                    {
                        this.users.removeIf(u -> u._id().equals(user._id()));
                    }
                    else if (userEvent.event().endsWith(UPDATED))
                    {
                        this.users.replaceAll(u -> u._id().equals(user._id()) ? user: u);
                    }
                });
    }

    @Override
    public void destroy() {
        this.userSubCons.forEach(UserListSubController::destroy);
        this.userSubCons.clear();

        eventListener.listen("users.*.*", User.class).unsubscribeOn(FX_SCHEDULER);
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
            ((VBox) this.userScrollPane.getContent()).getChildren().setAll(c.getList().stream().map(this::renderUser).toList());
        });

        return parent;
    }

    public void rulesButtonPressed(ActionEvent event) {
        final RulesScreenController controller = rulesScreenController.get();
        app.show(controller);
    }

    public void logoutButtonPressed(ActionEvent event) {
        final LoginController controller = loginController.get();
        app.show(controller);
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
        UserListSubController userCon = new UserListSubController(this.app,user);
        userSubCons.add(userCon);
        return userCon.render();
    }
}
