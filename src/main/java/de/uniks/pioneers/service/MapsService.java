package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateVoteDto;
import de.uniks.pioneers.model.Vote;
import de.uniks.pioneers.template.MapTemplate;
import de.uniks.pioneers.rest.MapsApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class MapsService {
    private final MapsApiService mapsApiService;

    @Inject
    public MapsService(MapsApiService mapsApiService) {
        this.mapsApiService = mapsApiService;
    }

    public Observable<List<MapTemplate>> findAllMaps() {
        return this.mapsApiService.findAllMaps();
    }

    public Observable<MapTemplate> deleteMapTemplate(String id) {
        return this.mapsApiService.delete(id);
    }

    public Observable<Vote> voteMap(String id, int score) {
        return this.mapsApiService.vote(id, new CreateVoteDto(score));
    }

    public Observable<Vote> deleteVote(String mapId, String userId) {
        return this.mapsApiService.deleteVote(mapId, userId);
    }
}
