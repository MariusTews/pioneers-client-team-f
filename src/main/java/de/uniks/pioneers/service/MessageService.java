package de.uniks.pioneers.service;

import de.uniks.pioneers.rest.MessageApiService;

import javax.inject.Inject;

public class MessageService {

    private final MessageApiService messageApiService;

    @Inject
    public MessageService(MessageApiService messageApiService) {
        this.messageApiService = messageApiService;
    }

    //TODO: send a message to all user
    public void send(String message) {

    }
}
