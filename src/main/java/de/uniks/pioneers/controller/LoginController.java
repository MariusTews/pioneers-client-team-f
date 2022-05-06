package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;

import de.uniks.pioneers.service.LoginService;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

public class LoginController implements Controller {
    private final App app;
    private final LoginService loginService;
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
    public LoginController(App app, LoginService loginService,
                           Provider<SignUpController> signUpController,
                           Provider<LobbyController> lobbyController) {
        this.app = app;
        this.loginService = loginService;
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

        return parent;
    }

    public void login(String username, String password) {
        loginService.login(username, password)
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(result -> app.show(lobbyController.get()));
    }

    public void loginButtonPressed(ActionEvent event) {
        login(usernameTextField.getText(), userPasswordField.getText());
    }

    public void signUPHyperlinkPressed(ActionEvent event) {
        final SignUpController controller = signUpController.get();
        app.show(controller);
    }
}