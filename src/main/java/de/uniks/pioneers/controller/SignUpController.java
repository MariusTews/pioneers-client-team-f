package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.service.AuthService;
import de.uniks.pioneers.service.UserService;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.Objects;

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
    private final Provider<LoginController> loginController;
    private final AuthService authService;
    private final UserService userService;


    @Inject
    public SignUpController(App app,
                            Provider<LoginController> loginController,
                            AuthService authService,
                            UserService userService) {
        this.app = app;
        this.loginController = loginController;
        this.authService = authService;
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

        final BooleanBinding match = Bindings.equal(passwordField.textProperty(), repeatPasswordField.textProperty());
        final BooleanBinding length = Bindings.greaterThan(8, passwordField.lengthProperty());
        final BooleanBinding usernameLengthMax = Bindings.greaterThan(33, usernameTextField.lengthProperty());
        final BooleanBinding usernameLengthMin = Bindings.greaterThan(1, usernameTextField.lengthProperty());
        errorLabel.textProperty().bind(
                Bindings.when(match)
                        .then("")
                        .otherwise("Passwords do not match")
        );
        signUpButton.disableProperty().bind(length.or(match.not()).or(usernameLengthMax.not().or(usernameLengthMin)));


        return parent;
    }

    public void register(String username, String avatar, String password) {

        userService.register(username, avatar, password)
                .observeOn(FX_SCHEDULER)
                .doOnError(error -> {
                    if (error.getMessage().equals("HTTP 409 ")) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Username already taken");
                        // set style of error
                        DialogPane dialogPane = alert.getDialogPane();
                        dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
                                .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
                        alert.showAndWait();
                    }
                })
                .subscribe(result -> {
                    if (result._id() != null) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "sign up successful");
                        // set style of information
                        DialogPane dialogPane = alert.getDialogPane();
                        dialogPane.getStylesheets().add(Objects.requireNonNull(Main.class
                                .getResource("view/stylesheets/AlertStyle.css")).toExternalForm());
                        alert.showAndWait()
                                .ifPresent((btn) -> app.show(loginController.get()));
                    }
                });
    }


    public void signUpButtonPressed() {
        register(usernameTextField.getText(), null, passwordField.getText());
    }

    public void backButtonPressed() {
        app.show(loginController.get());
    }
}
