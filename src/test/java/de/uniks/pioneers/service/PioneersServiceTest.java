package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateBuildingDto;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.rest.PioneersApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PioneersServiceTest {
	@Mock
	PioneersApiService pioneersApiService;

	@InjectMocks
	PioneersService pioneersService;

	@Test
	public void findAllTiles() {
		when(pioneersService.findAllTiles(any()))
				.thenReturn(Observable.just(new Map("1", List.of(new Tile(1, 2, 3, "desert", 7)), null)));
		final Map result = pioneersService.findAllTiles("1").blockingFirst();

		assertEquals(result.gameId(), "1");
		assertEquals(result.tiles().get(0).x(), 1);
		assertEquals(result.tiles().get(0).y(), 2);
		assertEquals(result.tiles().get(0).z(), 3);
		assertEquals(result.tiles().get(0).type(), "desert");
		assertEquals(result.tiles().get(0).numberToken(), 7);

		verify(pioneersApiService).findAllTiles("1");
	}

	@Test
	public void findAllPlayers() {
		HashMap<String, Integer> resources = new HashMap<>() {{
			put("unknown", 3);
		}};

		HashMap<String, Integer> remainingBuildings = new HashMap<>() {{
			put("city", 1);
		}};

		when(pioneersService.findAllPlayers(any()))
				.thenReturn(Observable.just(List.of(new Player("2", "12", Color.BLACK.toString(), true,
						3, resources, remainingBuildings, 2, 2, false,false,null, null))));
		final List<Player> result = pioneersService.findAllPlayers("2").blockingFirst();
		Player player = result.get(0);

		assertEquals(player.gameId(), "2");
		assertEquals(player.userId(), "12");
		assertEquals(player.color(), Color.BLACK.toString());
		assertEquals(player.foundingRoll(), 3);
		assertEquals(player.resources(), resources);
		assertEquals(player.remainingBuildings(), remainingBuildings);

		verify(pioneersApiService).findAllPlayers("2");
	}

	@Test
	public void findOnePlayer() {
		HashMap<String, Integer> resources = new HashMap<>() {{
			put("unknown", 3);
		}};

		HashMap<String, Integer> remainingBuildings = new HashMap<>() {{
			put("city", 1);
		}};

		when(pioneersService.findOnePlayer(any(), any()))
				.thenReturn(Observable.just(new Player("2", "12", Color.BLACK.toString(), true,
						3, resources, remainingBuildings, 2, 2, false,false,null, null)));
		final Player player = pioneersService.findOnePlayer("2", "12").blockingFirst();

		assertEquals(player.gameId(), "2");
		assertEquals(player.userId(), "12");
		assertEquals(player.color(), Color.BLACK.toString());
		assertEquals(player.foundingRoll(), 3);
		assertEquals(player.resources(), resources);
		assertEquals(player.remainingBuildings(), remainingBuildings);
		assertTrue(player.active());
		assertNull(player.previousTradeOffer());

		verify(pioneersApiService).findOnePlayer("2", "12");
	}

	@Test
	public void findOneState() {
		List<ExpectedMove> expectedMoves = new ArrayList<>();
		expectedMoves.add(new ExpectedMove("founding", new ArrayList<>()));

		when(pioneersService.findOneState(any()))
				.thenReturn(Observable.just(new State("1", "12", expectedMoves, null)));
		final State state = pioneersService.findOneState("12").blockingFirst();

		assertEquals(state.gameId(), "12");
		assertEquals(state.updatedAt(), "1");
		assertEquals(state.expectedMoves(), expectedMoves);

		verify(pioneersApiService).findOneState("12");
	}

	@Test
	public void findAllBuildings() {
		when(pioneersService.findAllBuildings(any()))
				.thenReturn(Observable.just(List.of(new Building(1, 2, 3, "42", 4, "a", "b", "c"))));
		final List<Building> buildings = pioneersService.findAllBuildings("b").blockingFirst();
		Building result = buildings.get(0);

		assertEquals(result.x(), 1);
		assertEquals(result.y(), 2);
		assertEquals(result.z(), 3);
		assertEquals(result._id(), "42");
		assertEquals(result.side(), 4);
		assertEquals(result.type(), "a");
		assertEquals(result.gameId(), "b");
		assertEquals(result.owner(), "c");

		verify(pioneersApiService).findAllBuildings("b");
	}

	@Test
	public void findOneBuilding() {
		when(pioneersService.findOneBuilding(any(), any()))
				.thenReturn(Observable.just(new Building(1, 2, 3, "42", 4, "a", "b", "c")));
		final Building result = pioneersService.findOneBuilding("b", "42").blockingFirst();

		assertEquals(result.x(), 1);
		assertEquals(result.y(), 2);
		assertEquals(result.z(), 3);
		assertEquals(result._id(), "42");
		assertEquals(result.side(), 4);
		assertEquals(result.type(), "a");
		assertEquals(result.gameId(), "b");
		assertEquals(result.owner(), "c");

		verify(pioneersApiService).findOneBuilding("b", "42");
	}

	@Test
	public void moveBuild() {
		when(pioneersApiService.create(any(), any()))
				.thenReturn(Observable.just(new Move("4", "10", "100", "99", "build",
						3, "city", null, null, null, null)));
		final Move result = pioneersService.move("100", "build", 1, 2, 3, 20, "city", null, null).blockingFirst();

		assertEquals(result._id(), "10");
		assertEquals(result.createdAt(), "4");
		assertEquals(result.gameId(), "100");
		assertEquals(result.userId(), "99");
		assertEquals(result.action(), "build");
		assertEquals(result.roll(), 3);
		assertEquals(result.building(), "city");
		assertNull(result.developmentCard());
		assertNull(result.rob());

		verify(pioneersApiService).create("100", new CreateMoveDto("build", null, null, null, null,
				new CreateBuildingDto(1, 2, 3, 20, "city")));
	}

	@Test
	public void moveRoll() {
		when(pioneersApiService.create(any(), any()))
				.thenReturn(Observable.just(new Move("4", "10", "100", "99", "roll",
						3, null, null, null, null, null)));
		final Move result = pioneersService.move("100", "roll", null, null, null, null, null, null, null).blockingFirst();

		assertEquals(result._id(), "10");
		assertEquals(result.createdAt(), "4");
		assertEquals(result.gameId(), "100");
		assertEquals(result.userId(), "99");
		assertEquals(result.action(), "roll");
		assertEquals(result.roll(), 3);
		assertNull(result.building());

		verify(pioneersApiService).create("100", new CreateMoveDto("roll", null, null, null, null, null));
	}
}
