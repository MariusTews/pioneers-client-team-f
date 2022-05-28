package de.uniks.pioneers.service;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.rest.MessageApiService;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class MessageService_Factory implements Factory<MessageService> {
  private final Provider<MessageApiService> messageApiServiceProvider;

  public MessageService_Factory(Provider<MessageApiService> messageApiServiceProvider) {
    this.messageApiServiceProvider = messageApiServiceProvider;
  }

  @Override
  public MessageService get() {
    return newInstance(messageApiServiceProvider.get());
  }

  public static MessageService_Factory create(
      Provider<MessageApiService> messageApiServiceProvider) {
    return new MessageService_Factory(messageApiServiceProvider);
  }

  public static MessageService newInstance(MessageApiService messageApiService) {
    return new MessageService(messageApiService);
  }
}
