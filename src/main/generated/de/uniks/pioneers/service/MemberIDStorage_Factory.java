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
public final class MemberIDStorage_Factory implements Factory<MemberIDStorage> {
  @Override
  public MemberIDStorage get() {
    return newInstance();
  }

  public static MemberIDStorage_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static MemberIDStorage newInstance() {
    return new MemberIDStorage();
  }

  private static final class InstanceHolder {
    private static final MemberIDStorage_Factory INSTANCE = new MemberIDStorage_Factory();
  }
}
