package de.uniks.pioneers.service;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import de.uniks.pioneers.rest.GroupApiService;
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
public final class GroupService_Factory implements Factory<GroupService> {
  private final Provider<GroupApiService> groupApiServiceProvider;

  public GroupService_Factory(Provider<GroupApiService> groupApiServiceProvider) {
    this.groupApiServiceProvider = groupApiServiceProvider;
  }

  @Override
  public GroupService get() {
    return newInstance(groupApiServiceProvider.get());
  }

  public static GroupService_Factory create(Provider<GroupApiService> groupApiServiceProvider) {
    return new GroupService_Factory(groupApiServiceProvider);
  }

  public static GroupService newInstance(GroupApiService groupApiService) {
    return new GroupService(groupApiService);
  }
}
