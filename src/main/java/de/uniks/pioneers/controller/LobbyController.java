package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class LobbyController implements Controller {

    private final ObservableList<User> users = FXCollections.observableArrayList();
    @FXML
    public Button rulesButton;
    @FXML
    public Label userWelcomeLabel;
    @FXML
    public Button logoutButton;
    @FXML
    public ListView<String> userListView;
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
    private Provider <LoginController> loginController;
    private Provider<RulesScreenController> rulesScreenController;

    @Inject
    public LobbyController(App app, UserService userService,
                           Provider<LoginController> loginController,
                           Provider<RulesScreenController> rulesScreenController) {

        this.app = app;
        this.userService = userService;
        this.loginController = loginController;
        this.rulesScreenController = rulesScreenController;
    }

    @Override
    public void init() {
        userService.findAllUsers().observeOn(FX_SCHEDULER).subscribe(this.users::setAll);
    }

    @Override
    public void destroy() {

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

        users.addListener((ListChangeListener<? super User>) c -> {
            userListView.getItems().removeAll();
            for(User user: c.getList())
            {
                String toAdd = user.name() + " " + user.status();
                userListView.getItems().add(userListView.getItems().size(), toAdd);
            }
        });

        sendButton.setOnAction(this::sendButtonPressed);
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
        userService.findAllUsers().observeOn(FX_SCHEDULER).subscribe(this.users::setAll);
    }

    public void editButtonPressed(ActionEvent event) {
    }

    public void createGameButtonPressed(ActionEvent event) {
    }

    public void enterKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            checkMessageField();
        }
    }

    private void checkMessageField() {
        if (!chatMessageField.getText().isEmpty()) {
            ((VBox) ((ScrollPane) this.allTab.getContent()).getContent()).getChildren().add(new Label("username: " + chatMessageField.getText()));
            chatMessageField.setText("");
        }
    }
}
