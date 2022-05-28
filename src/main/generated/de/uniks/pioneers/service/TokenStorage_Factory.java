package de.uniks.pioneers.service;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class TokenStorage_Factory implements Factory<TokenStorage> {
  @Override
  public TokenStorage get() {
    return newInstance();
  }

  public static TokenStorage_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static TokenStorage newInstance() {
    return new TokenStorage();
  }

  private static final class InstanceHolder {
    private static final TokenStorage_Factory INSTANCE = new TokenStorage_Factory();
  }
}
