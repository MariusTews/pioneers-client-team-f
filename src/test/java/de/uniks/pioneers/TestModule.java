package de.uniks.pioneers;


import dagger.Module;
import dagger.Provides;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.dto.*;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.rest.*;
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
			public Observable<User> updateUser(String id, UpdateUserDto dto) {
				return null;
			}

			@Override
			public Observable<User> statusUpdate(String id, StatusUpdateDto dto) {
				return null;
			}

			@Override
			public Observable<User> deleteUser(String id) {
				return Observable.empty();
			}
		};
	}

	@Provides
	static AuthApiService authApiService() {
		return new AuthApiService() {
			@Override
			public Observable<LoginResult> login(LoginDto dto) {
				return null;
			}

			@Override
			public Observable<LoginResult> refresh(RefreshDto dto) {
				return null;
			}

			@Override
			public Observable<ErrorResponse> logout() {
				return null;
			}
		};
	}

	@Provides
	static GroupApiService groupApiService() {
		return new GroupApiService() {
			@Override
			public Observable<List<Group>> getAll() {
				return null;
			}

			@Override
			public Observable<Group> create(CreateGroupDto dto) {
				return null;
			}

			@Override
			public Observable<Group> getOne(String id) {
				return null;
			}

			@Override
			public Observable<Group> patch(String id) {
				return null;
			}

			@Override
			public Observable<Group> delete(String id) {
				return null;
			}
		};
	}

	@Provides
	static MessageApiService messageApiService(){
		return new MessageApiService() {
			@Override
			public Observable<List<Message>> findAll(String namespace, String parent) {
				return null;
			}

			@Override
			public Observable<Message> create(String namespace, String parent, CreateMessageDto dto) {
				return null;
			}

			@Override
			public Observable<Message> findOne(String namespace, String parent, String id) {
				return null;
			}

			@Override
			public Observable<Message> patch(String namespace, String parent, String id, UpdateMemberDto dto) {
				return null;
			}

			@Override
			public Observable<Message> delete(String namespace, String parent, String id) {
				return null;
			}
		};
	}

	@Provides
	GameMembersApiService gameMembersApiService(){
		return new GameMembersApiService() {
			@Override
			public Observable<List<Member>> findAll(String gameId) {
				return null;
			}

			@Override
			public Observable<Member> create(String gameId, CreateMemberDto dto) {
				return null;
			}

			@Override
			public Observable<Member> findOne(String gameId, String userId) {
				return null;
			}

			@Override
			public Observable<Member> patch(String gameId, String userId, UpdateMemberDto dto) {
				return null;
			}

			@Override
			public Observable<Member> delete(String gameId, String userId) {
				return null;
			}
		};
	}

	@Provides
	static GamesApiService gamesApiService (){
		return new GamesApiService() {
			@Override
			public Observable<List<Game>> findAll() {
				return null;
			}

			@Override
			public Observable<Game> create(CreateGameDto dto) {
				return null;
			}

			@Override
			public Observable<Game> findOne(String id) {
				return null;
			}

			@Override
			public Observable<Game> patch(String id, UpdateGameDto dto) {
				return null;
			}

			@Override
			public Observable<Game> delete(String id) {
				return null;
			}
		};
	}
	@Provides
	static  PioneersApiService pioneersApiService() {
		return new PioneersApiService() {
			@Override
			public Observable<Map> findAllTiles(String gameId) {
				return null;
			}

			@Override
			public Observable<List<Player>> findAllPlayers(String gameId) {
				return null;
			}

			@Override
			public Observable<Player> findOnePlayer(String gameId, String userId) {
				return null;
			}

			@Override
			public Observable<State> findOneState(String gameId) {
				return null;
			}

			@Override
			public Observable<List<Building>> findAllBuildings(String gameId) {
				return null;
			}

			@Override
			public Observable<Building> findOneBuilding(String gameId, String buildingId) {
				return null;
			}

			@Override
			public Observable<Move> create(String gameId, CreateMoveDto dto) {
				return null;
			}
		};
	}
}
