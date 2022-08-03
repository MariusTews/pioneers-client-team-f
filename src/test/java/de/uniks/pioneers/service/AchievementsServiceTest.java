package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateAchievementDto;
import de.uniks.pioneers.dto.UpdateAchievementDto;
import de.uniks.pioneers.model.Achievement;
import de.uniks.pioneers.rest.AchievementsApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AchievementsServiceTest {


    @Mock
    IDStorage idStorage;

    @Mock
    AchievementsApiService achievementsApiService;

    @InjectMocks
    AchievementsService achievementsService;

    @Test
    void testInitUserAchievements() {
        when(idStorage.getID()).thenReturn("1");
        String unlocked = java.time.LocalDateTime.now().toString();
        Achievement achievement1 = new Achievement("", "", "1", "first-road", unlocked, 3);
        Achievement achievement2 = new Achievement("", "", "1", "road-builder", null, 99);
        Achievement achievement3 = new Achievement("", "", "1", "city-builder", null, 2);
        List<Achievement> achievementList = new ArrayList<>();
        achievementList.add(achievement1);
        achievementList.add(achievement2);
        achievementList.add(achievement3);

        //test initUserAchievements
        achievementsService.init();
        when(achievementsApiService.listUserAchievements("1")).thenReturn(Observable.just(achievementList));
        List<Achievement> result = achievementsService.initUserAchievements().blockingFirst();
        assertEquals(result, achievementList);
        verify(achievementsApiService).listUserAchievements("1");

    }

    @Test
    void testUpdateAchievementNotUnlocked() {
        when(idStorage.getID()).thenReturn("1");
        String unlocked = java.time.LocalDateTime.now().toString();
        Achievement achievement1 = new Achievement("", "", "1", "first-road", unlocked, 3);
        Achievement achievement2 = new Achievement("", "", "1", "road-builder", null, 99);
        Achievement achievement3 = new Achievement("", "", "1", "city-builder", null, 2);
        List<Achievement> achievementList = new ArrayList<>();
        achievementList.add(achievement1);
        achievementList.add(achievement2);
        achievementList.add(achievement3);

        //test initUserAchievements
        achievementsService.init();
        when(achievementsApiService.listUserAchievements("1")).thenReturn(Observable.just(achievementList));
        List<Achievement> result = achievementsService.initUserAchievements().blockingFirst();
        assertEquals(result, achievementList);

        //test update achievement not unlocked
        when(achievementsApiService.updateAchievement("1", "city-builder", new UpdateAchievementDto(null, 3)))
                .thenReturn(Observable.just(new Achievement("", "", "1", "city-builder", null, 3)));
        Achievement result1 = achievementsService.putOrUpdateAchievement("city-builder", 1).blockingFirst();
        assertEquals(result1, new Achievement("", "", "1", "city-builder", null, 3));
        verify(achievementsApiService).updateAchievement("1", "city-builder", new UpdateAchievementDto(null, 3));
    }

    @Test
    void testUpdateAchievementUnlocked() {
        when(idStorage.getID()).thenReturn("1");
        String unlocked = java.time.LocalDateTime.now().toString();
        Achievement achievement1 = new Achievement("", "", "1", "first-road", unlocked, 3);
        Achievement achievement2 = new Achievement("", "", "1", "road-builder", null, 99);
        Achievement achievement3 = new Achievement("", "", "1", "city-builder", null, 2);
        List<Achievement> achievementList = new ArrayList<>();
        achievementList.add(achievement1);
        achievementList.add(achievement2);
        achievementList.add(achievement3);

        //test initUserAchievements
        achievementsService.init();
        when(achievementsApiService.listUserAchievements("1")).thenReturn(Observable.just(achievementList));
        List<Achievement> result = achievementsService.initUserAchievements().blockingFirst();
        assertEquals(result, achievementList);

        //test update achievement unlocked
        when(achievementsApiService.updateAchievement(any(), any(), any()))
                .thenReturn(Observable.just(new Achievement("", "", "1", "road-builder", unlocked, 100)));
        Achievement result2 = achievementsService.putOrUpdateAchievement("road-builder", 1).blockingFirst();
        assertEquals(result2, new Achievement("", "", "1", "road-builder", unlocked, 100));
        verify(achievementsApiService).updateAchievement(any(), any(), any());
    }

    @Test
    void testPutAchievement() {
        when(idStorage.getID()).thenReturn("1");
        String unlocked = java.time.LocalDateTime.now().toString();
        Achievement achievement1 = new Achievement("", "", "1", "first-road", unlocked, 3);
        Achievement achievement2 = new Achievement("", "", "1", "road-builder", null, 99);
        Achievement achievement3 = new Achievement("", "", "1", "city-builder", null, 2);
        List<Achievement> achievementList = new ArrayList<>();
        achievementList.add(achievement1);
        achievementList.add(achievement2);
        achievementList.add(achievement3);

        //test initUserAchievements
        achievementsService.init();
        when(achievementsApiService.listUserAchievements("1")).thenReturn(Observable.just(achievementList));
        List<Achievement> result = achievementsService.initUserAchievements().blockingFirst();
        assertEquals(result, achievementList);

        //test put achievement
        when(achievementsApiService.putAchievement("1", "settlement-builder", new CreateAchievementDto(null, 1)))
                .thenReturn(Observable.just(new Achievement("", "", "1", "settlement-builder", null, 1)));
        Achievement result3 = achievementsService.putOrUpdateAchievement("settlement-builder", 1).blockingFirst();
        assertEquals(result3, new Achievement("", "", "1", "settlement-builder", null, 1));
        verify(achievementsApiService).putAchievement("1", "settlement-builder", new CreateAchievementDto(null, 1));


    }
}
