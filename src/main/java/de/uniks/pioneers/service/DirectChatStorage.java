package de.uniks.pioneers.service;

import javafx.scene.control.Tab;

import javax.inject.Inject;

public class DirectChatStorage {

	private String groupId;

	private String userId;

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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Tab getTab() {
		return tab;
	}

	public void setTab(Tab tab) {
		this.tab = tab;
	}
}
