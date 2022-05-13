package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.service.AuthService;
import de.uniks.pioneers.service.UserService;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class LoginController implements Controller {
    private final App app;
    private final AuthService authService;
    private final UserService userService;
    private Provider<SignUpController> signUpController;
    private Provider<LobbyController> lobbyController;
    @FXML
    public TextField usernameTextField;
    @FXML
    public PasswordField userPasswordField;
    @FXML
    public CheckBox rememberMeCheckBox;
    @FXML
    public Button loginButton;
    @FXML
    public Hyperlink signUpHyperlink;
    @FXML
    public Label errorLabel;

    @Inject
    public LoginController(App app, AuthService authService,
                           UserService userService,
                           Provider<SignUpController> signUpController,
                           Provider<LobbyController> lobbyController) {
        this.app = app;
        this.authService = authService;
        this.userService = userService;
        this.signUpController = signUpController;
        this.lobbyController = lobbyController;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {

        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/LoginScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        signUpHyperlink.setOnAction(this::signUPHyperlinkPressed);

        final BooleanBinding length = Bindings.greaterThan(8, userPasswordField.lengthProperty());
        loginButton.disableProperty().bind(length);

        return parent;
    }

    public void login(String username, String password) {
        authService.login(username, password)
                .observeOn(FX_SCHEDULER)
                .doOnError(error -> {
                    if ("HTTP 401 ".equals(error.getMessage())) {
                        errorLabel.setText("Invalid username or password");
                    }
                })
                .subscribe(result -> {
                    userService.statusUpdate(result, "online")
                                    .observeOn(FX_SCHEDULER)
                                            .subscribe();
                    app.show(lobbyController.get());
                });
    }

    public void loginButtonPressed(ActionEvent event) {
        login(usernameTextField.getText(), userPasswordField.getText());
    }

    public void signUPHyperlinkPressed(ActionEvent event) {
        final SignUpController controller = signUpController.get();
        app.show(controller);
    }
}
