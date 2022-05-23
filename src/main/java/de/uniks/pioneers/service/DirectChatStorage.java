package de.uniks.pioneers.service;

import de.uniks.pioneers.model.User;
import javafx.scene.control.Tab;

import javax.inject.Inject;

public class DirectChatStorage {

	private String groupId;

	private User user;

	private Tab tab;

	@Inject
	public DirectChatStorage(){
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Tab getTab() {
		return tab;
	}

	public void setTab(Tab tab) {
		this.tab = tab;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
