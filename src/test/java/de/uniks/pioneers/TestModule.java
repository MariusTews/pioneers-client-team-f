package de.uniks.pioneers;


import dagger.Module;
import dagger.Provides;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.dto.CreateUserDto;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.dto.LoginDto;
import de.uniks.pioneers.dto.LoginResult;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.AuthApiService;
import de.uniks.pioneers.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;
@Module
public class TestModule {
	@Provides
	static EventListener eventListener(){
		return new EventListener(null,null){
			@Override
			public <T> Observable<Event<T>> listen(String pattern, Class<T> type){return Observable.empty();}

			@Override
			public void send(Object message){}
		};
	}

	@Provides
	static UserApiService userApiService(){
		return new UserApiService() {
			@Override
			public Observable<User> createUser(CreateUserDto dto) {
				return Observable.empty();
			}

			@Override
			public Observable<List<User>> findAllUsers() {
				return Observable.just(List.of());
			}

			@Override
			public Observable<User> findUser(String id) {
				return Observable.empty();
			}

			@Override
			public Observable<User> updateUser(String id) {
				return Observable.empty();
			}

			@Override
			public Observable<User> deleteUser(String id) {
				return Observable.empty();
			}
		};
	}
}
