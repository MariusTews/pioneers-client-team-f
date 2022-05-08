package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateUserDto;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

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

    public Observable<List<User>> findAllUsers() {
        return this.userApiService.findAllUsers();
    }

    public Observable<User> getUser(String id) {
        return userApiService.findUser(id);
    }
}
