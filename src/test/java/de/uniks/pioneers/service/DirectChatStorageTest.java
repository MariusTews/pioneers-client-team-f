package de.uniks.pioneers.service;

import de.uniks.pioneers.model.User;
import javafx.scene.control.Tab;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DirectChatStorageTest {

	private DirectChatStorage storage = new DirectChatStorage();

	@Test
	void testGroupId() {
		Assertions.assertNull(storage.getGroupId());

		String id = "1234";
		storage.setGroupId(id);

		Assertions.assertEquals(storage.getGroupId(), id);
	}

	@Test
	void testTab() {
		Assertions.assertNull(storage.getTab());

		Tab tab = new Tab("hello");
		storage.setTab(tab);

		Assertions.assertEquals(storage.getTab(), tab);
	}

	@Test
	void testUser() {
		Assertions.assertNull(storage.getUser());

		User user = new User("id","name","status","avatar");
		storage.setUser(user);

		Assertions.assertEquals(storage.getUser(),user);
	}
}
