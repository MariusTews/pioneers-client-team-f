package de.uniks.pioneers;


import dagger.Module;
import dagger.Provides;
import de.uniks.pioneers.Template.MapTemplate;
import de.uniks.pioneers.dto.*;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.rest.*;
import de.uniks.pioneers.Websocket.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Module
public class TestModule {

	public static final PublishSubject<Event<Member>> memberSubject = PublishSubject.create();
	public static final PublishSubject<Event<Game>> gameSubject = PublishSubject.create();
	public static final PublishSubject<Event<User>> userSubject = PublishSubject.create();

	public static final PublishSubject<Event<State>> stateSubject = PublishSubject.create();
	public static final PublishSubject<Event<Building>> buildingSubject = PublishSubject.create();
	public static final PublishSubject<Event<Move>> moveSubject = PublishSubject.create();
	public static final PublishSubject<Event<Player>> playerSubject = PublishSubject.create();
	public static final PublishSubject<Event<Message>> messageSubject = PublishSubject.create();
	public static final PublishSubject<Event<Group>> groupSubject = PublishSubject.create();


	@Provides
	@Singleton
	EventListener eventListener() {
		EventListener eventListener = mock(EventListener.class);

		when(eventListener.listen("users.*.*", User.class)).thenReturn(userSubject);
		when(eventListener.listen("games.*.*", Game.class)).thenReturn(gameSubject);

		when(eventListener.listen("games.01.members.*.*", Member.class)).thenReturn(memberSubject);
		when(eventListener.listen("games.01.*.*", Message.class)).thenReturn(messageSubject);
		when(eventListener.listen("games.01.messages.*.*", Message.class)).thenReturn(messageSubject);

		when(eventListener.listen("games.01.state.*", State.class)).thenReturn(stateSubject);
		when(eventListener.listen("games.01.moves.*.created", Move.class)).thenReturn(moveSubject);
		when(eventListener.listen("games.01.players.*.*", Player.class)).thenReturn(playerSubject);
		when(eventListener.listen("games.01.buildings.*.*", Building.class)).thenReturn(buildingSubject);

		when(eventListener.listen("group.*.*", Group.class)).thenReturn(groupSubject);
		when(eventListener.listen("global.627cf3c93496bc00158f3859.messages.*.*", Message.class)).thenReturn(messageSubject);

		return eventListener;
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
				members.add(new Member("0", "0", "01", "01", true, null, false));
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
				return Observable.just(new Game("0", "0", "01", "testGame", "01", 1, false, new GameSettings(2, 10, null, false, 0)));
			}

			@Override
			public Observable<Game> findOne(String id) {
				return Observable.just(new Game("0", "1", "01", "TestGame", "7", 1, false, new GameSettings(1, 3, null, false, 0)));
			}

			@Override
			public Observable<Game> patch(String id, UpdateGameDto dto) {
				return Observable.just(new Game("0", "0", "01", "testGame", "10", 2, true, new GameSettings(2, 10, null, false, 0)));
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
				List<Harbor> harbors = new ArrayList<>();
				harbors.add(new Harbor(1, 0, -1, null, 1));
				Map map = new Map("02", titles, harbors);

				return Observable.just(map);
			}

			@Override
			public Observable<List<Player>> findAllPlayers(String gameId) {
				List<Player> players = new ArrayList<>();
				HashMap<String, Integer> res = new HashMap<>();
				res.put("lumber", 0);
				players.add(new Player("01", "01", "#0000ff", true, 1, res, null, 0, 0, null, null));
				return Observable.just(players);
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

	@Provides
	static MapsApiService mapsApiService() {
		return new MapsApiService() {
			@Override
			public Observable<List<MapTemplate>> findAllMaps() {
				return Observable.just(List.of(new MapTemplate("", "", "1", "map", null, "01", 0, List.of(), List.of())));
			}
		};
	}
}
