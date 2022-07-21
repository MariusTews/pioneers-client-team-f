package de.uniks.pioneers.service;

import de.uniks.pioneers.Template.MapTemplate;
import de.uniks.pioneers.rest.MapsApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class MapsService {
    private final MapsApiService mapsApiService;

    @Inject
    public MapsService(MapsApiService mapsApiService) {
        this.mapsApiService = mapsApiService;
    }

    public Observable<List<MapTemplate>> findAllMaps() {
        return this.mapsApiService.findAllMaps();
    }
}
