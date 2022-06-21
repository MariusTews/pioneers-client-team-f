package de.uniks.pioneers;


import dagger.Module;
import dagger.Provides;
import de.uniks.pioneers.Websocket.EventListener;
import de.uniks.pioneers.dto.*;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.rest.*;
import io.reactivex.rxjava3.core.Observable;

import java.util.ArrayList;
import java.util.List;

@Module
public class TestModule {
	@Provides
	static EventListener eventListener() {
		return new EventListener(null, null) {
			@Override
			public <T> Observable<Event<T>> listen(String pattern, Class<T> type) {
				return Observable.empty();
			}

			@Override
			public void send(Object message) {
			}
		};
	}

	@Provides
	static UserApiService userApiService() {
		return new UserApiService() {
			@Override
			public Observable<User> createUser(CreateUserDto dto) {
				return Observable.just(new User("1234", "12345", "01", "Alice", "online", null, null));
			}

			@Override
			public Observable<List<User>> findAllUsers() {
				ArrayList<User> users = new ArrayList<>();
				users.add(new User("1234", "12345", "01", "Alice", "online", null, null));
				return Observable.just(users);
			}

			@Override
			public Observable<User> findUser(String id) {
				return Observable.empty();
			}

			@Override
			public Observable<User> updateUser(String id, UpdateUserDto dto) {
				return Observable.just(new User("1234", "12345", "01", "Alice", "online", null, null));
			}

			@Override
			public Observable<User> statusUpdate(String id, StatusUpdateDto dto) {
				return Observable.just(new User("1234", "12345", "01", "Alice", "online", null, null));
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
				return Observable.just(new LoginResult("0", "0", "01", "Alice", "online", null, null, "123", "321"));
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
				return Observable.empty();
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
	static MessageApiService messageApiService() {
		return new MessageApiService() {
			@Override
			public Observable<List<Message>> findAll(String namespace, String parent) {
				return Observable.empty();
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
	GameMembersApiService gameMembersApiService() {
		return new GameMembersApiService() {
			@Override
			public Observable<List<Member>> findAll(String gameId) {
				ArrayList<Member> members = new ArrayList<>();
				members.add(new Member("0", "0", "01", "10", true, null, false));
				members.add(new Member("0", "0", "01", "15", true, null, false));
				return Observable.just(members);
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
				return Observable.empty();
			}

			@Override
			public Observable<Member> delete(String gameId, String userId) {
				return null;
			}
		};
	}

	@Provides
	static GamesApiService gamesApiService() {
		return new GamesApiService() {
			@Override
			public Observable<List<Game>> findAll() {
				return Observable.empty();
			}

			@Override
			public Observable<Game> create(CreateGameDto dto) {
				return Observable.just(new Game("0", "0", "01", "testGame", "01", 1, false, new GameSettings(2, 10)));
			}

			@Override
			public Observable<Game> findOne(String id) {
				return Observable.empty();
			}

			@Override
			public Observable<Game> patch(String id, UpdateGameDto dto) {
				return Observable.just(new Game("0", "0", "01", "testGame", "10", 2, true, new GameSettings(2, 10)));
			}

			@Override
			public Observable<Game> delete(String id) {
				return null;
			}
		};
	}

	@Provides
	static PioneersApiService pioneersApiService() {
		return new PioneersApiService() {
			@Override
			public Observable<Map> findAllTiles(String gameId) {
				List<Tile> titles = new ArrayList<>();
				titles.add(new Tile(-2, 2, 0, "fields", 5));
				titles.add(new Tile(-2, 1, 1, "desert", 5));
				titles.add(new Tile(-2, 0, 2, "hills", 5));
				titles.add(new Tile(-1, 2, -1, "mountains", 5));
				titles.add(new Tile(-1, 1, 0, "forest", 5));
				titles.add(new Tile(-1, 0, 1, "pasture", 5));
				titles.add(new Tile(-1, -1, 2, "fields", 5));
				titles.add(new Tile(0, 2, -2, "fields", 5));
				titles.add(new Tile(0, 1, -1, "fields", 5));
				titles.add(new Tile(0, 0, 0, "fields", 5));
				titles.add(new Tile(0, -1, 1, "fields", 5));
				titles.add(new Tile(0, -2, 2, "fields", 5));
				titles.add(new Tile(1, 1, -2, "fields", 5));
				titles.add(new Tile(1, 0, -1, "fields", 5));
				titles.add(new Tile(1, -1, 0, "fields", 5));
				titles.add(new Tile(1, -2, 1, "fields", 5));
				titles.add(new Tile(2, 0, -2, "fields", 5));
				titles.add(new Tile(2, -1, -1, "fields", 5));
				titles.add(new Tile(2, -2, 0, "fields", 5));
				Map map = new Map("02", titles, null);

				return Observable.just(map);
			}

			@Override
			public Observable<List<Player>> findAllPlayers(String gameId) {
				return Observable.empty();
			}

			@Override
			public Observable<Player> findOnePlayer(String gameId, String userId) {
				return Observable.empty();
			}

			@Override
			public Observable<Player> updatePlayer(String gameId, String userId, UpdatePlayerDto dto) {
				return null;
			}


			@Override
			public Observable<State> findOneState(String gameId) {
				List<ExpectedMove> moves = new ArrayList<>();
				List<String> players = new ArrayList<>();
				players.add("01");
				moves.add(new ExpectedMove("founding-settlement-1", players));
				return Observable.just(new State("0", "02", moves, null));
			}

			@Override
			public Observable<List<Building>> findAllBuildings(String gameId) {
				return Observable.empty();
			}

			@Override
			public Observable<Building> findOneBuilding(String gameId, String buildingId) {
				return Observable.empty();
			}

			@Override
			public Observable<Move> create(String gameId, CreateMoveDto dto) {
				return Observable.empty();
			}

			@Override
			public Observable<List<Move>> findAllMoves(String gameId) {
				return null;
			}

			@Override
			public Observable<Move> findOneMove(String gameId, String moveId) {
				return null;
			}
		};
	}
}
