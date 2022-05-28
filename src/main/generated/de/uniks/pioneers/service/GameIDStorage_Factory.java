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
public final class GameIDStorage_Factory implements Factory<GameIDStorage> {
  @Override
  public GameIDStorage get() {
    return newInstance();
  }

  public static GameIDStorage_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GameIDStorage newInstance() {
    return new GameIDStorage();
  }

  private static final class InstanceHolder {
    private static final GameIDStorage_Factory INSTANCE = new GameIDStorage_Factory();
  }
}
