package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.service.LoginService;
import de.uniks.pioneers.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class SignUpController implements Controller {
    @FXML
    public TextField usernameTextField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField repeatPasswordField;
    @FXML
    public Label errorLabel;
    @FXML
    public Button signUpButton;
    @FXML
    public Button backButton;
    private final App app;
    private Provider<LoginController> loginController;
    private final LoginService loginService;
    private final UserService userService;


    @Inject
    public SignUpController(App app,
                            Provider<LoginController> loginController,
                            LoginService loginService,
                            UserService userService) {
        this.app = app;
        this.loginController = loginController;
        this.loginService = loginService;
        this.userService = userService;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/SignUpScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return parent;
    }

    public void register(String username, String avatar, String password) {

        userService.register(username, avatar, password)
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> app.show(loginController.get()));
    }


    public void signUpButtonPressed(ActionEvent event) {
        register(usernameTextField.getText(), null, passwordField.getText());
    }

    public void backButtonPressed(ActionEvent event) {
        app.show(loginController.get());
    }
}
