package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateAchievementDto;
import de.uniks.pioneers.dto.UpdateAchievementDto;
import de.uniks.pioneers.model.Achievement;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

import static de.uniks.pioneers.Constants.USER_ACHIEVEMENTS_BY_ID_URL;
import static de.uniks.pioneers.Constants.USER_ACHIEVEMENTS_URL;

public interface AchievementsApiService {

    @GET(USER_ACHIEVEMENTS_URL)
    Observable<List<Achievement>> listUserAchievements(@Path("userId") String id);

    @GET(USER_ACHIEVEMENTS_BY_ID_URL)
    Observable<List<Achievement>> getUserAchievement(
            @Path("userId") String userId,
            @Path("id") String id
    );

    @PUT(USER_ACHIEVEMENTS_BY_ID_URL)
    Observable<Achievement> putAchievement(
            @Path("userId") String userId,
            @Path("id") String id,
            @Body CreateAchievementDto createAchievementDto
    );

    @PATCH(USER_ACHIEVEMENTS_BY_ID_URL)
    Observable<Achievement> updateAchievement(
            @Path("userId") String userId,
            @Path("id") String id,
            @Body UpdateAchievementDto updateAchievementDto
    );
}
