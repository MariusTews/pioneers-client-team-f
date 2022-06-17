package de.uniks.pioneers.service;

import de.uniks.pioneers.dto.CreateMessageDto;
import de.uniks.pioneers.model.Message;
import de.uniks.pioneers.rest.MessageApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {
    @Mock
    MessageApiService messageApiService;

    @InjectMocks
    MessageService messageService;

    @Test
    public void getAllMessagesTest() {
        when(messageApiService.findAll(any(), any()))
                .thenReturn(Observable
                        .just(new Message("0", "0", "1", "11", "Hi"),
                                new Message("1", "1", "2", "11", "Jo")).buffer(2));

        final List<Message> result = messageService.getAllMessages("g", "001").blockingFirst();

        assertEquals("Message[createdAt=0, updatedAt=0, _id=1, sender=11, body=Hi]", result.get(0).toString());
        assertEquals(result.get(0).createdAt(), "0");
        assertEquals(result.get(0).updatedAt(), "0");
        assertEquals("Message[createdAt=1, updatedAt=1, _id=2, sender=11, body=Jo]", result.get(1).toString());

        verify(messageApiService).findAll("g", "001");
    }

    @Test
    public void sendTest() {
        when(messageApiService.create(any(), any(), any()))
                .thenReturn(Observable.just(new Message("0", "0", "1", "11", "Hi")));

        final Message result = messageService.send("g", "1", "Hi").blockingFirst();

        assertEquals("Message[createdAt=0, updatedAt=0, _id=1, sender=11, body=Hi]", result.toString());

        verify(messageApiService).create("g", "1", new CreateMessageDto("Hi"));
    }

    @Test
    public void deleteTest() {
        when(messageApiService.delete(any(), any(), any()))
                .thenReturn(Observable.just(new Message("0", "0", "1", "11", "Hi")));

        final Message result = messageService.delete("g", "1", "1").blockingFirst();

        assertEquals("Message[createdAt=0, updatedAt=0, _id=1, sender=11, body=Hi]", result.toString());

        verify(messageApiService).delete("g", "1", "1");
    }
}
