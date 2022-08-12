package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateMapTemplateDto;
import de.uniks.pioneers.template.HarborTemplate;
import de.uniks.pioneers.dto.CreateVoteDto;
import de.uniks.pioneers.model.Vote;
import de.uniks.pioneers.rest.MapsApiService;
import de.uniks.pioneers.template.TileTemplate;
import de.uniks.pioneers.template.MapTemplate;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class MapsService {
    private final MapsApiService mapsApiService;

    @Inject
    public MapsService(MapsApiService mapsApiService) {
        this.mapsApiService = mapsApiService;
    }

    public Observable<MapTemplate> createMapTemplate(String name, List<TileTemplate> tiles, List<HarborTemplate> harbors) {
        return this.mapsApiService.createMapTemplate(new CreateMapTemplateDto(name, null, tiles, harbors));
    }

    public Observable<List<MapTemplate>> findAllMaps() {
        return this.mapsApiService.findAllMaps();
    }

    public Observable<List<Vote>> findVotesByMapId(String id) {
        return this.mapsApiService.findVotesByMapId(id);
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
