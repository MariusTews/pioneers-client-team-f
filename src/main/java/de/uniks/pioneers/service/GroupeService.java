package de.uniks.pioneers.service;

import de.uniks.pioneers.model.Groupe;
import de.uniks.pioneers.rest.GroupeApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class GroupeService {

    private final GroupeApiService groupeApiService;

    @Inject
    public GroupeService(GroupeApiService groupeApiService) {
        this.groupeApiService = groupeApiService;
    }

    public Observable<List<Groupe>> getAll() {
        return groupeApiService.getAll();
    }


}
