package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateMessageDto;
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

    public Observable<List<Message>> getAllMessages(String namespace, String parent) {
        return messageApiService.findAll(namespace, parent);
    }

    public Observable<Message> send(String namespace, String parent, String message) {
        return messageApiService.create(namespace, parent, new CreateMessageDto(message));
    }

    public Observable<Message> delete(String namespace, String parent, String id) {
        return messageApiService.delete(namespace, parent, id);
    }
}
