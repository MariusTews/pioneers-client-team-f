package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateMessageDto;
import de.uniks.pioneers.dto.UpdateMemberDto;
import de.uniks.pioneers.model.Message;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

public interface MessageApiService {

    @GET("{namespace}/{parent}/messages")
    Observable<List<Message>> getAll(@Path("namespace") String namespace,
                                     @Path("parent") String parent);

    @POST("{namespace}/{parent}/messages")
    Observable<Message> sendMessage(@Path("namespace") String namespace,
                                    @Path("parent") String parent,
                                    @Body CreateMessageDto dto);

    @GET("{namespace}/{parent}/messages/{id}")
    Observable<Message> getOne(@Path("namespace") String namespace,
                               @Path("parent") String parent,
                               @Path("id") String id);

    @PATCH("{namespace}/{parent}/messages/{id}")
    Observable<Message> patchOne(@Path("namespace") String namespace,
                                 @Path("parent") String parent,
                                 @Path("id") String id,
                                 @Body UpdateMemberDto dto);

    @DELETE("{namespace}/{parent}/messages/{id}")
    Observable<Message> deleteOne(@Path("namespace") String namespace,
                                  @Path("parent") String parent,
                                  @Path("id") String id);
}
