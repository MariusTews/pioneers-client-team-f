package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateGameDto;
import de.uniks.pioneers.dto.UpdateGameDto;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.GameSettings;
import de.uniks.pioneers.rest.GamesApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {
	@Spy
	GameStorage gameStorage;

	@Spy
	MemberIDStorage memberIDStorage;

	@Mock
	GamesApiService gamesApiService;

	@InjectMocks
	GameService gameService;

	@Test
	void create() {
		when(gamesApiService.create(any())).thenReturn(Observable.just(new Game("0:30", "15:50", "1337", "testGame", "01", 3, false, new GameSettings(2, 10))));
		Game game = gameService.create("testGame", "123", 2, 10).blockingFirst();
		assertEquals("Game[createdAt=0:30, updatedAt=15:50, _id=1337, name=testGame, owner=01, members=3, started=false, settings=GameSettings[mapRadius=2, victoryPoints=10]]", game.toString());
		assertEquals("1337", gameStorage.getId());
		assertEquals("01", memberIDStorage.getId());

		verify(gamesApiService).create(new CreateGameDto("testGame", false, new GameSettings(2, 10), "123"));
	}

	@Test
	void findAllGames() {
		when(gamesApiService.findAll()).thenReturn(Observable.just(new Game("28.08.2000", "18.05.2022", "37", "myGame", "45", 3, false, new GameSettings(2, 10)), new Game("01.01.1990", "24.12.2020", "1", "first", "2", 6, false, new GameSettings(2, 10))).buffer(2));
		List<Game> games = gameService.findAllGames().blockingFirst();
		assertEquals("[Game[createdAt=28.08.2000, updatedAt=18.05.2022, _id=37, name=myGame, owner=45, members=3, started=false, settings=GameSettings[mapRadius=2, victoryPoints=10]], Game[createdAt=01.01.1990, updatedAt=24.12.2020, _id=1, name=first, owner=2, members=6, started=false, settings=GameSettings[mapRadius=2, victoryPoints=10]]]", games.toString());

		verify(gamesApiService).findAll();
	}

	@Test
	void findOneGame() {
		when(gamesApiService.findOne(any())).thenReturn(Observable.just(new Game("5", "6", "7", "8", "9", 4, false, new GameSettings(2, 10))));
		Game game = gameService.findOneGame("7").blockingFirst();
		assertEquals(game.createdAt(), "5");
		assertEquals(game.updatedAt(), "6");
		assertEquals("Game[createdAt=5, updatedAt=6, _id=7, name=8, owner=9, members=4, started=false, settings=GameSettings[mapRadius=2, victoryPoints=10]]", game.toString());

		verify(gamesApiService).findOne("7");
	}

	@Test
	void updateGame() {
		when(gamesApiService.patch(any(), any())).thenReturn(Observable.just(new Game("0:00", "0:01", "420", "chill", "69", 1, false, new GameSettings(2, 10))));
		Game game = gameService.updateGame("420", "chill", "password", "69", true, 2, 10).blockingFirst();
		assertEquals("Game[createdAt=0:00, updatedAt=0:01, _id=420, name=chill, owner=69, members=1, started=false, settings=GameSettings[mapRadius=2, victoryPoints=10]]", game.toString());

		verify(gamesApiService).patch("420", new UpdateGameDto("chill", "69", true, new GameSettings(2, 10), "password"));
	}

	@Test
	void deleteGame() {
		when(gamesApiService.delete(any())).thenReturn(Observable.just(new Game("yesterday", "now", "404", "sad", "00", 1, false, new GameSettings(2, 10))));
		Game game = gameService.deleteGame("404").blockingFirst();
		assertEquals("Game[createdAt=yesterday, updatedAt=now, _id=404, name=sad, owner=00, members=1, started=false, settings=GameSettings[mapRadius=2, victoryPoints=10]]", game.toString());

		verify(gamesApiService).delete("404");
	}
}
