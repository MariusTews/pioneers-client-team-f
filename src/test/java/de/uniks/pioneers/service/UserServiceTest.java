package de.uniks.pioneers.service;

import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserApiService userApiService;

    @InjectMocks
    UserService userService;

    @Test
    void register() {
        when(userApiService.createUser(any())).thenReturn(Observable.just(new User("01", "Alice", "online", null)));
        User user = userService.register("Alice", null, "testtest").blockingFirst();
        assertEquals("User[_id=01, name=Alice, status=online, avatar=null]", user.toString());
    }

    @Test
    void userUpdate() {
        when(userApiService.updateUser(any(), any())).thenReturn(Observable.just((new User("01", "Alice", "offline", null))));
        User user = userService.userUpdate("01", "Alice", null, "offline", "testtest").blockingFirst();
        assertEquals("User[_id=01, name=Alice, status=offline, avatar=null]", user.toString());
    }

    @Test
    void statusUpdate() {
        when(userApiService.statusUpdate(any(), any())).thenReturn(Observable.just((new User("01", "Alice", "offline", null))));
        User user = userService.statusUpdate("01", "offline").blockingFirst();
        assertEquals("User[_id=01, name=Alice, status=offline, avatar=null]", user.toString());
    }

    @Test
    void findAllUsers() {


        when(userApiService.findAllUsers()).thenReturn(Observable.just(new User("01", "Alice", "offline", null), new User("10", "Bob", "online", null)).buffer(2));
        List<User> user = userService.findAllUsers().blockingFirst();
        assertEquals("[User[_id=01, name=Alice, status=offline, avatar=null], User[_id=10, name=Bob, status=online, avatar=null]]", user.toString());
    }

    @Test
    void delete() {
        when(userApiService.deleteUser(any())).thenReturn(Observable.just((new User("07", "Alice", "offline", null))));
        User user = userService.delete("07").blockingFirst();
        assertEquals("User[_id=07, name=Alice, status=offline, avatar=null]", user.toString());
    }

    @Test
    void findOne() {
        when(userApiService.findUser(any())).thenReturn(Observable.just((new User("10", "Alice", "online", null))));
        User user = userService.findOne("10").blockingFirst();
        assertEquals("User[_id=10, name=Alice, status=online, avatar=null]", user.toString());
    }
}