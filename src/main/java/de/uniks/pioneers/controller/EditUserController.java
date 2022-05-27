package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.UserApiService;
import de.uniks.pioneers.service.IDStorage;
import de.uniks.pioneers.service.UserService;
import io.reactivex.rxjava3.core.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.*;
import java.util.Base64;
import java.util.Optional;

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
    @FXML
    public Button editButton;

    public File pictureFile;
    private final App app;
    private final UserService userService;
    private Observable<User> user;
    private String avatar;
    private String username;


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
            if (currUser.avatar() == null || currUser.avatar().equals("data:image/png;base64,")) {

                this.userPicture.setImage(new Image(String.valueOf(Main.class.getResource("defaultPicture.png"))));

            } else {
                userPicture.setImage(new Image(currUser.avatar()));

            }
            username = currUser.name();

        });

        return parent;
    }

    public void cancelButtonPressed(ActionEvent event) {
        final LobbyController controller = lobbyController.get();
        this.app.show(controller);
    }

    public void confirmButtonPressed(ActionEvent event) {
        updateAvatar();

        if (newUserNameTextField.getText().equals("") && passwordField.getText().equals("")) {
            updateUser(idStorage.getID(), null, null, null, avatar);
        } else if (passwordField.getText().equals("")) {
            updateUser(idStorage.getID(), newUserNameTextField.getText(), null, null, avatar);
        } else if (newUserNameTextField.getText().equals("")) {
            updateUser(idStorage.getID(), null, passwordField.getText(), repeatPasswordFiled.getText(), avatar);
        } else {
            updateUser(idStorage.getID(), newUserNameTextField.getText(), passwordField.getText(), repeatPasswordFiled.getText(), avatar);
        }
    }

    public void deleteButtonPressed(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting userPicture");
        alert.setContentText("Are you sure you want to delete " + username + "?" );
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            userService.delete(idStorage.getID())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(suc -> app.show(loginController.get()));
        }
    }

    public void changePicture(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Deleting userPicture");
            alert.setContentText("do you really want to delete your User picture?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                this.userPicture.setImage(new Image(String.valueOf(Main.class.getResource("defaultPicture.png"))));
                avatar = "data:image/png;base64,";
            }
        }
    }

    public String encodeFileToBase64Binary(File file) {
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
                new Alert(Alert.AlertType.INFORMATION, "the chosen image is to big! \nplease choose a picture which is smaller than 10kb")
                        .showAndWait();
                return;
            }
        }
        if (password != null) {
            if (!password.equals(repeatPassword)) {
                new Alert(Alert.AlertType.INFORMATION, "passwords do not match!")
                        .showAndWait();
                return;

            } else if (password.length() < 8) {
                new Alert(Alert.AlertType.INFORMATION, "the password length must be at least 8!")
                        .showAndWait();
                return;
            }
        }
        userService.userUpdate(id, name, avatar, "online", password)
                .observeOn(FX_SCHEDULER)
                .doOnError(error ->{
                    if ("HTTP 409 ".equals(error.getMessage())) {
                        new Alert(Alert.AlertType.INFORMATION, "username is already taken!")
                                .showAndWait();
                    }
                    if ("HTTP 400 ".equals(error.getMessage())) {
                        new Alert(Alert.AlertType.INFORMATION, "name must be shorter than or equal to 32 characters!")
                                .showAndWait();
                    }
                })

                .subscribe(onSuccess -> app.show(lobbyController.get()), onError -> {});
    }

    public void editPicture(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll();
        File selectedFile = fileChooser.showOpenDialog(null);
        if(selectedFile!= null) {
            this.pictureFile = selectedFile;
            userPicture.setImage(new Image(selectedFile.toURI().toString()));
        }
    }
}

