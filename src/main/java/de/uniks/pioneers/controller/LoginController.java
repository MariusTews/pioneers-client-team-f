package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import javax.inject.Inject;
import java.io.IOException;

public class LoginController implements Controller {
    private final App app;
    private final AuthService authService;
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
    public LoginController(App app, AuthService authService){
        this.app = app;
        this.authService = authService;
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
        try{
            parent = loader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return parent;
    }

    public void loginButtonPressed(ActionEvent event) {
    }
}
