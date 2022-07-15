package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateGroupDto;
import de.uniks.pioneers.model.Group;
import de.uniks.pioneers.rest.GroupApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class GroupService {

    private final GroupApiService groupApiService;

    @Inject
    public GroupService(GroupApiService groupApiService) {
        this.groupApiService = groupApiService;
    }

    public Observable<List<Group>> getAll() {
        return groupApiService.getAll();
    }

    public Observable<Group> createGroup( List<String> toAdd) {
        return groupApiService
                .create(new CreateGroupDto(null,toAdd));
    }
}
