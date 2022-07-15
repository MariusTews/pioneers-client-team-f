package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateUserDto;
import de.uniks.pioneers.dto.StatusUpdateDto;
import de.uniks.pioneers.dto.UpdateUserDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class UserService {

    private final UserApiService userApiService;

    @Inject
    public UserService(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    public Observable<User> register(String username, String avatar, String password) {
        return userApiService
                .createUser(new CreateUserDto(username, avatar, password));
    }

    public Observable<User> userUpdate(String id, String name, String avatar, List<String> friends,
                                                                            String status, String password) {
        return userApiService
                .updateUser(id, new UpdateUserDto(name, status, avatar, friends, password));
    }

    public Observable<User> statusUpdate(String id, String status) {
        return userApiService
                .statusUpdate(id, new StatusUpdateDto(status));
    }

    public Observable<List<User>> findAllUsers() {
        return this.userApiService.findAllUsers();
    }

    public Observable<User> delete(String id) {
        return userApiService.deleteUser(id);
    }

    public Observable<User>findOne(String id){
        return userApiService.findUser(id);
    }
}
