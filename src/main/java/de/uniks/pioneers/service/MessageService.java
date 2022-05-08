package de.uniks.pioneers.service;

import de.uniks.pioneers.model.Message;
import de.uniks.pioneers.rest.MessageApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class MessageService {

    private final MessageApiService messageApiService;

    @Inject
    public MessageService(MessageApiService messageApiService) {
        this.messageApiService = messageApiService;
    }

    public void send(String message) {

    }

    public Observable<List<Message>> getAll() {
        return messageApiService.findAll("groups", "62756e8567968900144280a9");
    }
}
