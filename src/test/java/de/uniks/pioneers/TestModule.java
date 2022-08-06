package de.uniks.pioneers;


import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import de.uniks.pioneers.template.MapTemplate;
import de.uniks.pioneers.websocket.EventListener;
import de.uniks.pioneers.dto.*;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.rest.*;
import de.uniks.pioneers.service.SoundService;
import de.uniks.pioneers.service.TokenStorage;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Module
public class TestModule {

    @Provides
    SoundService soundService() {
        return new SoundService() {
            public void playSound(String action) {
            }
        };
    }

    static class TestEventListener extends EventListener {
        java.util.Map<String, ObservableEmitter<?>> emitterMap = new HashMap<>();

        public TestEventListener(TokenStorage tokenStorage, ObjectMapper mapper) {
            super(tokenStorage, mapper);
        }

        @Override
        public <T> Observable<Event<T>> listen(String pattern, Class<T> payloadType) {
            return Observable.create(emitter -> {
                emitterMap.put(pattern, emitter);
                emitter.setCancellable(() -> emitterMap.remove(pattern));
            });
        }

        public <T> void fireEvent(String pattern, Event<T> eventDto) {
            @SuppressWarnings("unchecked")
            ObservableEmitter<Event<T>> emitter = (ObservableEmitter<Event<T>>) emitterMap.get(pattern);
            if (emitter != null) {
                emitter.onNext(eventDto);
            }
        }
    }


    @Provides
    @Singleton
    EventListener eventListener() {
        return new TestEventListener(null, null);
    }

    @Provides
    @Singleton
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
                User user = new User("1", "2", "01", "me", "offline", null, null);
                return Observable.just(user);
            }

            @Override
            public Observable<List<Vote>> findVotes(String id) {
                return Observable.just(List.of(new Vote("", "", "01", "01", 1)));
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
    @Singleton
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
                return Observable.just(new ErrorResponse(null, null, null));
            }
        };
    }

    @Provides
    @Singleton
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
    @Singleton
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
    @Singleton
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
    @Singleton
    static GamesApiService gamesApiService(EventListener eventListener) {
        return new GamesApiService() {

            private final TestEventListener testEventListener = (TestEventListener) eventListener;

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

                Game game = new Game("0", "0", "01", "testGame", "10", 2, true, new GameSettings(2, 10, null, false, 0));
                Message message = new Message("1", "2", "01", "me", null);
                testEventListener.fireEvent("games.01.*.*", new Event<>("games.01.state.created", message));
                return Observable.just(game);
            }

            @Override
            public Observable<Game> delete(String id) {
                return null;
            }
        };
    }

    @Provides
    @Singleton
    static PioneersApiService pioneersApiService(EventListener eventListener) {
        return new PioneersApiService() {
            private List<ExpectedMove> expectedMoves;
            private final TestEventListener testEventListener = (TestEventListener) eventListener;

            @Override
            public Observable<Map> findAllTiles(String gameId) {
                List<Tile> tiles = new ArrayList<>();
                // radius 0
                tiles.add(new Tile(0, 0, 0, "fields", 11));

                // radius 1
                tiles.add(new Tile(1, -1, 0, "fields", 2));
                tiles.add(new Tile(-1, 0, 1, "hills", 3));
                tiles.add(new Tile(-1, 1, 0, "mountains", 4));
                tiles.add(new Tile(0, 1, -1, "forest", 5));
                tiles.add(new Tile(1, 0, -1, "pasture", 6));
                tiles.add(new Tile(0, -1, 1, "fields", 7));


                List<Harbor> harbors = new ArrayList<>();
                harbors.add(new Harbor(1, 0, -1, "lumber", 1));
                Map map = new Map("02", tiles, harbors);

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
                if (expectedMoves == null) {
                    List<String> players = new ArrayList<>();
                    players.add("01");
                    moves.add(new ExpectedMove("founding-roll", players));
                    moves.add(new ExpectedMove("founding-settlement-1", players));
                    moves.add(new ExpectedMove("founding-road-1", players));
                    moves.add(new ExpectedMove("founding-settlement-2", players));
                    moves.add(new ExpectedMove("founding-road-2", players));
                    moves.add(new ExpectedMove("roll", players));
                } else {
                    moves = expectedMoves;
                }
                return Observable.just(new State("0", "01", moves, null));
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
                if (dto.building() != null && !Objects.equals(dto.action(), "founding-roll")) {
                    CreateBuildingDto createBuildingDto = dto.building();
                    Building building;
                    State state;
                    Player player;
                    HashMap<String, Integer> resources = new HashMap<>();
                    HashMap<String, Integer> remainingBuildings = new HashMap<>();
                    switch (dto.action()) {
                        //founding phase
                        case "founding-settlement-1" -> {
                            state = new State("", gameId, List.of(
                                    new ExpectedMove("founding-road-1", List.of("01")),
                                    new ExpectedMove("founding-settlement-2", List.of("01")),
                                    new ExpectedMove("founding-road-2", List.of("01")),
                                    new ExpectedMove("roll", List.of("01"))
                            ), null);
                            expectedMoves = state.expectedMoves();
                            resources.put("lumber", 10);
                            resources.put("ore", 10);
                            resources.put("brick", 10);
                            resources.put("grain", 10);
                            resources.put("wool", 10);
                            remainingBuildings.put("settlement", 4);
                            remainingBuildings.put("city", 4);
                            remainingBuildings.put("road", 15);
                            building = new Building(createBuildingDto.x(), createBuildingDto.y(), createBuildingDto.z(), "1234", createBuildingDto.side(), "settlement", gameId, "01");
                            player = new Player(gameId, "01", "#C44F4F", true, 1, resources, remainingBuildings, 1, 0, null, null);
                        }
                        case "founding-road-1" -> {
                            state = new State("", gameId, List.of(
                                    new ExpectedMove("founding-settlement-2", List.of("01")),
                                    new ExpectedMove("founding-road-2", List.of("01")),
                                    new ExpectedMove("roll", List.of("01"))
                            ), null);
                            expectedMoves = state.expectedMoves();
                            resources.put("lumber", 10);
                            resources.put("ore", 10);
                            resources.put("brick", 10);
                            resources.put("grain", 10);
                            resources.put("wool", 10);
                            remainingBuildings.put("settlement", 4);
                            remainingBuildings.put("city", 4);
                            remainingBuildings.put("road", 14);
                            building = new Building(createBuildingDto.x(), createBuildingDto.y(), createBuildingDto.z(), "1235", createBuildingDto.side(), "road", gameId, "01");
                            player = new Player(gameId, "01", "#C44F4F", true, 1, resources, remainingBuildings, 1, 0, null, null);
                        }
                        case "founding-settlement-2" -> {
                            state = new State("", gameId, List.of(
                                    new ExpectedMove("founding-road-2", List.of("01")),
                                    new ExpectedMove("roll", List.of("01"))
                            ), null);
                            expectedMoves = state.expectedMoves();
                            resources.put("lumber", 10);
                            resources.put("ore", 10);
                            resources.put("brick", 10);
                            resources.put("grain", 10);
                            resources.put("wool", 10);
                            remainingBuildings.put("settlement", 3);
                            remainingBuildings.put("city", 4);
                            remainingBuildings.put("road", 14);
                            building = new Building(createBuildingDto.x(), createBuildingDto.y(), createBuildingDto.z(), "1236", createBuildingDto.side(), "settlement", gameId, "01");
                            player = new Player(gameId, "01", "#C44F4F", true, 1, resources, remainingBuildings, 2, 0, null, null);
                        }
                        case "founding-road-2" -> {
                            state = new State("", gameId, List.of(
                                    new ExpectedMove("roll", List.of("01"))
                            ), null);
                            expectedMoves = state.expectedMoves();
                            resources.put("lumber", 10);
                            resources.put("ore", 10);
                            resources.put("brick", 10);
                            resources.put("grain", 10);
                            resources.put("wool", 10);
                            remainingBuildings.put("settlement", 3);
                            remainingBuildings.put("city", 4);
                            remainingBuildings.put("road", 13);
                            building = new Building(createBuildingDto.x(), createBuildingDto.y(), createBuildingDto.z(), "1237", createBuildingDto.side(), "road", gameId, "01");
                            player = new Player(gameId, "01", "#C44F4F", true, 1, resources, remainingBuildings, 2, 0, null, null);
                        }
                        // normal building
                        default -> {
                            state = new State("", gameId, List.of(
                                    new ExpectedMove("build", List.of("01"))
                            ), null);
                            expectedMoves = state.expectedMoves();
                            if (dto.building().type().equals("settlement")) {
                                resources.put("lumber", 10);
                                resources.put("ore", 10);
                                resources.put("brick", 10);
                                resources.put("grain", 10);
                                resources.put("wool", 10);
                                remainingBuildings.put("settlement", 2);
                                remainingBuildings.put("city", 4);
                                remainingBuildings.put("road", 12);
                                building = new Building(createBuildingDto.x(), createBuildingDto.y(), createBuildingDto.z(), "1238", createBuildingDto.side(), "settlement", gameId, "01");
                                player = new Player(gameId, "01", "#C44F4F", true, 1, resources, remainingBuildings, 3, 0, null, null);

                            } else {
                                resources.put("lumber", 10);
                                resources.put("ore", 10);
                                resources.put("brick", 10);
                                resources.put("grain", 10);
                                resources.put("wool", 10);
                                remainingBuildings.put("settlement", 3);
                                remainingBuildings.put("city", 4);
                                remainingBuildings.put("road", 12);
                                building = new Building(createBuildingDto.x(), createBuildingDto.y(), createBuildingDto.z(), "1239", createBuildingDto.side(), "road", gameId, "01");
                                player = new Player(gameId, "01", "#C44F4F", true, 1, resources, remainingBuildings, 2, 0, null, null);
                            }
                        }
                    }
                    // fire events to update UI
                    testEventListener.fireEvent("games.01.buildings.*.*", new Event<>("games.01.updated", building));
                    testEventListener.fireEvent("games.01.state.*", new Event<>("games.01.updated", state));
                    testEventListener.fireEvent("games.01.players.*.*", new Event<>("games.01.updated", player));
                    return Observable.just(new Move("1", "2", gameId, "01", "build", 3, "building", null, null, null, null));
                }
                // roll the dice
                else if (Objects.equals(dto.action(), "roll")) {
                    State state = new State("", gameId, List.of(
                            new ExpectedMove("build", List.of("01"))
                    ), null);
                    expectedMoves = state.expectedMoves();
                    testEventListener.fireEvent("games.01.state.*", new Event<>("games.01.updated", state));
                    testEventListener.fireEvent("games.01.moves.*.created", new Event<>("games.01.updated",
                            new Move("1", "2", gameId, "01", "roll", 3, null, null, null, null, null)));

                }
                // create move with action "build"
                else if (Objects.equals(dto.action(), "build")) {
                    State state = new State("", gameId, List.of(
                            new ExpectedMove("roll", List.of("01"))
                    ), null);
                    expectedMoves = state.expectedMoves();
                    testEventListener.fireEvent("games.01.state.*", new Event<>("games.01.updated", state));
                }
                // first founding roll
                else if (Objects.equals(dto.action(), "founding-roll")) {
                    State state = new State("", gameId, List.of(
                            new ExpectedMove("founding-settlement-1", List.of("01")),
                            new ExpectedMove("founding-road-1", List.of("01")),
                            new ExpectedMove("founding-settlement-2", List.of("01")),
                            new ExpectedMove("founding-road-2", List.of("01")),
                            new ExpectedMove("roll", List.of("01"))
                    ), null);
                    expectedMoves = state.expectedMoves();
                    testEventListener.fireEvent("games.01.state.*", new Event<>("games.01.updated", state));
                }
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

            @Override
            public Observable<List<Vote>> findVotesByMapId(String id) {
                return null;
            }

            @Override
			public Observable<MapTemplate> delete(String id) {
				return Observable.just(new MapTemplate("", "", "1", "map", null, "01", 0, List.of(), List.of()));
			}

            @Override
            public Observable<Vote> vote(String id, CreateVoteDto dto) {
                return null;
            }

            @Override
            public Observable<Vote> deleteVote(String mapId, String userId) {
                return null;
            }
        };
	}
}
