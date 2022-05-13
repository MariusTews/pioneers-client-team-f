package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.*;
import java.util.Base64;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class EditUserController implements Controller {
    private final Provider<LobbyController> lobbyController;
    private final Provider<LoginController> loginController;
    private final IDStorage idStorage;
    @FXML
    public TextField newUserNameTextField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField repeatPasswordFiled;
    @FXML
    public Button deleteButton;
    @FXML
    public Button cancelButton;
    @FXML
    public Button confirmButton;
    @FXML
    public ImageView userPicture;
    public File pictureFile;

    private final App app;
    private final UserService userService;
    private Observable<User> user;
    private String avatar;


    @Inject
    public EditUserController(App app,
                              Provider<LobbyController> lobbyController,
                              Provider<LoginController> loginController,
                              UserService userService,
                              IDStorage idStorage) {
        this.app = app;
        this.lobbyController = lobbyController;
        this.loginController = loginController;
        this.userService = userService;
        this.idStorage = idStorage;

    }

    @Override
    public void init() {


    }

    @Override
    public void destroy() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/EditUser.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        user = this.userService.findOne(idStorage.getID());

        user.subscribe(currUser -> {
            if (currUser.avatar() == null) {

                this.userPicture.setImage(new Image(String.valueOf(Main.class.getResource("defaultPicture.png"))));

            } else {
                userPicture.setImage(new Image(currUser.avatar()));

            }
            newUserNameTextField.setText(currUser.name());

        });

        return parent;
    }

    public void cancelButtonPressed(ActionEvent event) {
        final LobbyController controller = lobbyController.get();
        this.app.show(controller);
    }

    public void confirmButtonPressed(ActionEvent event) {

        if (newUserNameTextField.getText().equals("")) {
            new Alert(Alert.AlertType.INFORMATION, "please enter a username!")
                    .showAndWait();
        } else if (passwordField.getText().equals("")) {
            new Alert(Alert.AlertType.INFORMATION, "please enter a password to continue!")
                    .showAndWait();
        } else {
            updateAvatar();
            updateUser(idStorage.getID(), newUserNameTextField.getText(), passwordField.getText(), repeatPasswordFiled.getText(), avatar);
        }
    }

    public void deleteButtonPressed(ActionEvent event) {
        userService.delete(idStorage.getID())
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> app.show(loginController.get()));
    }

    public void changePicture(MouseEvent event) throws Exception {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll();
        File selectedFile = fileChooser.showOpenDialog(null);
        this.pictureFile = selectedFile;

        userPicture.setImage(new Image(selectedFile.toURI().toString()));


    }

    private static String encodeFileToBase64Binary(File file) {
        String encodedFile = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fileInputStreamReader.read(bytes);
            encodedFile = new String(Base64.getEncoder().encodeToString(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "data:image/png;base64," + encodedFile;
    }


    public void updateAvatar() {
        if (pictureFile == null) {
            user.subscribe(u -> avatar = u.avatar());

        } else {
            avatar = encodeFileToBase64Binary(pictureFile);
        }
    }

    public void updateUser(String id, String name, String password, String repeatPassword, String avatar) {

        if (avatar != null) {
            if (avatar.length() > 16384) {
                new Alert(Alert.AlertType.INFORMATION, "the chosen image is to big!")
                        .showAndWait();
                return;
            }
        }
        if (!password.equals(repeatPassword)) {
            new Alert(Alert.AlertType.INFORMATION, "passwords do not match!")
                    .showAndWait();

        } else if (password.length() < 8) {
            new Alert(Alert.AlertType.INFORMATION, "the password length must be at least 8!")
                    .showAndWait();
        } else {

            userService.userUpdate(id, name, avatar, "online", password)
                    .observeOn(FX_SCHEDULER)
                    .subscribe(result -> app.show(lobbyController.get()));
        }

    }

}

