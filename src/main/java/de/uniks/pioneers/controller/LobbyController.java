package de.uniks.pioneers.controller;

import com.sun.javafx.UnmodifiableArrayList;
import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Message;
import de.uniks.pioneers.service.MessageService;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

public class LobbyController implements Controller {
    @FXML
    public Button rulesButton;
    @FXML
    public Label userWelcomeLabel;
    @FXML
    public Button logoutButton;
    @FXML
    public ListView userListView;
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
    private Provider <LoginController> loginController;
    private Provider<RulesScreenController> rulesScreenController;
    private Provider<CreateGameController> createGameController;
    private Provider<EditUserController> editUserController;

    private final MessageService messageService;
    private final ObservableList<Message> messages = FXCollections.observableArrayList();

    //added an empty Constructor with the Annotation Inject for later use
    @Inject
    public LobbyController(App app,
                           MessageService messageService,
                           Provider<LoginController> loginController,
                           Provider<RulesScreenController> rulesScreenController,
                           Provider<CreateGameController> createGameController,
                           Provider<EditUserController> editUserController) {

        this.app = app;
        this.messageService = messageService;
        this.loginController = loginController;
        this.rulesScreenController = rulesScreenController;
        this.createGameController = createGameController;
        this.editUserController = editUserController;
    }

    @Override
    public void init() {
        messageService.getAll().observeOn(Schedulers.from(Platform::runLater)).subscribe(this.messages::setAll);
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

        sendButton.setOnAction(this::sendButtonPressed);

        messages.addListener((ListChangeListener<? super Message>) c ->{
            System.out.println(c.getList());

            ScrollPane scrollPane = new ScrollPane();
            VBox box = new VBox();
            allTab.setContent(scrollPane);
            scrollPane.setContent(box);

            for(Message message : c.getList()) {
                box.getChildren().add(new Label(message.sender() + ": " + message.body()));
            }
        });

        return parent;
    }

    private Node renderItem(Message message) {
        final Label m = new Label(message.sender() + ": " + message.body());

        return new HBox(5, m);
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
        //checkMessageField();
        send();
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

    private void send() {
        if (!chatMessageField.getText().isEmpty()) {
            messageService.send(chatMessageField.getText());
            chatMessageField.clear();
        }
    }
}
