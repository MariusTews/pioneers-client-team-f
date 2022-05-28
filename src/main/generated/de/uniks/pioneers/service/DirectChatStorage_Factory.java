package de.uniks.pioneers.service;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class DirectChatStorage_Factory implements Factory<DirectChatStorage> {
  @Override
  public DirectChatStorage get() {
    return newInstance();
  }

  public static DirectChatStorage_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DirectChatStorage newInstance() {
    return new DirectChatStorage();
  }

  private static final class InstanceHolder {
    private static final DirectChatStorage_Factory INSTANCE = new DirectChatStorage_Factory();
  }
}
