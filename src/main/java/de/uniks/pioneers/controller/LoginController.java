package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.service.AuthService;
import de.uniks.pioneers.service.UserService;
import de.uniks.pioneers.util.JsonUtil;
import de.uniks.pioneers.util.ResourceManager;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import kong.unirest.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

import static de.uniks.pioneers.Constants.*;
import static de.uniks.pioneers.Constants.JSON_NAME;

public class LoginController implements Controller {
    private final App app;
    private final AuthService authService;
    private final UserService userService;
    private final Provider<SignUpController> signUpController;
    private final Provider<LobbyController> lobbyController;
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

        JSONObject loadConfig = ResourceManager.loadConfig();
        if (loadConfig.get(JSON_REMEMBER_ME).equals(true)) {
            usernameTextField.setText(loadConfig.getString(JSON_NAME));
            rememberMeCheckBox.setSelected(true);
        }

        signUpHyperlink.setOnAction(this::signUPHyperlinkPressed);

        final BooleanBinding length = Bindings.greaterThan(8, userPasswordField.lengthProperty());
        final BooleanBinding usernameLengthMin = Bindings.greaterThan(1, usernameTextField.lengthProperty());
        final BooleanBinding usernameLengthMax = Bindings.greaterThan(33, usernameTextField.lengthProperty());
        loginButton.disableProperty().bind(length.or(usernameLengthMin.or(usernameLengthMax.not())));

        //this makes sure enter key works while loginging in
        parent.addEventFilter(KeyEvent.KEY_PRESSED,event -> {
            if(event.getCode() == KeyCode.ENTER){
                if(!(length.or(usernameLengthMin.or(usernameLengthMax.not())).get())){
                    loginButtonPressed();
                }
            }
        });

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

    public void loginButtonPressed() {
        if (rememberMeCheckBox.isSelected()) {
            ResourceManager.saveConfig(JsonUtil.createRememberMeConfig(usernameTextField.getText()));
        } else {
            ResourceManager.saveConfig(JsonUtil.createDefaultConfig());
        }
        login(usernameTextField.getText(), userPasswordField.getText());
    }

    public void signUPHyperlinkPressed(ActionEvent event) {
        final SignUpController controller = signUpController.get();
        app.show(controller);
    }
}
