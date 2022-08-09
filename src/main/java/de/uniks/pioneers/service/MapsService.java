package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateMapTemplateDto;
import de.uniks.pioneers.template.HarborTemplate;
import de.uniks.pioneers.template.MapTemplate;
import de.uniks.pioneers.rest.MapsApiService;
import de.uniks.pioneers.template.TileTemplate;
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

    public Observable<MapTemplate> deleteMapTemplate(String id) {
        return this.mapsApiService.delete(id);
    }
}
