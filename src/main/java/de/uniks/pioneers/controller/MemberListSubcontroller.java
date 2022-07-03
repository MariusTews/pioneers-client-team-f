package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.inject.Inject;
import java.io.IOException;

public class MemberListSubcontroller implements Controller {
	@FXML
	public ImageView idAvatar;
	@FXML
	public Label idUsername;
	@FXML
	public Label idReady;
	private final Member member;
	private final User user;
	public Circle circleId;

	@Inject
	public MemberListSubcontroller(Member member, User user) {
		this.member = member;
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
		final FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/MemberListSubView.fxml"));
		loader.setControllerFactory(c -> this);
		final Parent parent;
		try {
			parent = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}


		// set username and avatar
		if (this.member != null) {
			if (!member.spectator()) {
				//makes Label invisible to make GameLobby look more like the mockups
				this.idReady.setVisible(false);
				if (member.color() != null) {
					this.idUsername.setText(user.name());
					this.idUsername.setTextFill(Color.web(member.color()));
				} else {
					this.idUsername.setText(user.name());
				}

				if (user.avatar() != null) {
					this.idAvatar.setImage(new Image(user.avatar()));
				}
				// set ready
				if (this.member.ready()) {
					this.idReady.setText("-ready-");
					this.idReady.setTextFill(Color.DARKGREEN);
					this.circleId.setFill(Color.web("#00D100"));
				} else {
					this.idReady.setText("-not ready-");
					this.idReady.setTextFill(Color.FIREBRICK);
					this.circleId.setFill(Color.RED);
				}
			} else {
				//makes label visible to make GameLobby look more like the mockups
				this.idReady.setVisible(true);
				if (member.color() != null) {
					this.idUsername.setText("(Spectator)");
					this.idUsername.setTextFill(Color.web(member.color()));
					this.idReady.setText(user.name());
					this.circleId.opacityProperty().set(0);
					this.idReady.setTextFill(Color.web(member.color()));
				} else {
					this.idUsername.setText("(Spectator)");
					this.circleId.opacityProperty().set(0);
					this.idReady.setText(user.name());
				}

				if (user.avatar() != null) {
					this.idAvatar.setImage(new Image(user.avatar()));
				}
			}
		}

		return parent;
	}
}
