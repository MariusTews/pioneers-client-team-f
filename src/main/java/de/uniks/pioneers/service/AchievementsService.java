package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateAchievementDto;
import de.uniks.pioneers.dto.UpdateAchievementDto;
import de.uniks.pioneers.model.Achievement;
import de.uniks.pioneers.rest.AchievementsApiService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;

import static de.uniks.pioneers.Constants.ACHIEVEMENT_UNLOCK_VALUES;

public class AchievementsService {

    protected CompositeDisposable disposable;

    private final AchievementsApiService achievementsApiService;

    final IDStorage idStorage;

    private final HashMap<String, Integer> achievementsProgress = new HashMap<>();

    @Inject
    public AchievementsService(AchievementsApiService achievementsApiService, IDStorage idStorage) {
        this.achievementsApiService = achievementsApiService;
        this.idStorage = idStorage;
    }

    public void init() {
        disposable = new CompositeDisposable();
    }

    public Observable<List<Achievement>> listUserAchievements() {
        return achievementsApiService.listUserAchievements(idStorage.getID());
    }

    public Observable<List<Achievement>> initUserAchievements() {
        return achievementsApiService.listUserAchievements(idStorage.getID())
                .doOnNext(achievements -> {
                            for (Achievement achievement : achievements) {
                                int achievementProgress = (int) achievement.progress();
                                achievementsProgress.put(achievement.id(), achievementProgress);

                            }
                        }
                );
    }

    public Observable<Achievement> putOrUpdateAchievement(String id, int progress) {
        if (achievementsProgress.containsKey(id)) {
            return this.updateAchievement(id, progress);
        } else {
            return this.putAchievement(id, progress);
        }
    }

    private Observable<Achievement> putAchievement(String id, int progress) {
        String unlocked = null;
        // check if an achievement gets unlocked
        if (ACHIEVEMENT_UNLOCK_VALUES.get(id) <= progress) {
            unlocked = java.time.LocalDateTime.now().toString();
        }
        achievementsProgress.put(id, progress);
        return achievementsApiService.putAchievement(idStorage.getID(), id, new CreateAchievementDto(unlocked, progress));
    }

    private Observable<Achievement> updateAchievement(String id, int progress) {
        String unlocked = null;
        int actProgress = achievementsProgress.get(id);
        int newProgress = achievementsProgress.get(id) + progress;
        // check if an achievement gets unlocked
        if (actProgress < ACHIEVEMENT_UNLOCK_VALUES.get(id) &&
                newProgress >= ACHIEVEMENT_UNLOCK_VALUES.get(id)) {
            unlocked = java.time.LocalDateTime.now().toString();
        }
        achievementsProgress.replace(id, newProgress);
        return achievementsApiService.updateAchievement(idStorage.getID(), id, new UpdateAchievementDto(unlocked, newProgress));
    }
}
