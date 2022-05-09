package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.inject.Inject;
import java.io.IOException;

public class UserListSubController implements Controller {
	@FXML
	public ImageView userImageView;
	@FXML
	public Circle userStatusCircle;
	@FXML
	public Label userNameLabel;
	@FXML
	public Button chatButton;
	private App app;
	private User user;

	@Inject
	public UserListSubController(App app, User user){

		this.app = app;
		this.user = user;
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}

	@Override
	public Parent render() {
		final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/UserlistSubView.fxml"));
		loader.setControllerFactory(c -> this);
		final Parent parent;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		if (this.user != null){
			this.userNameLabel.setText(this.user.name());

			if (this.user.status().equals("online")){
				this.userStatusCircle.setFill(Color.GREEN);
			}

			else if (this.user.status().equals("offline")){
				this.userStatusCircle.setFill(Color.RED);
			}

			if (this.user.avatar()!=null){
				this.userImageView.setImage(new Image(this.user.avatar().toString()));
			}
		}

		return parent;
	}

	public void chatButtonPressed(ActionEvent event) {
	}
}
