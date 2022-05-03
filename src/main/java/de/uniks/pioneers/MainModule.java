package de.uniks.pioneers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import de.uniks.pioneers.rest.UserApiService;
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.inject.Singleton;

import static de.uniks.pioneers.Constants.BASE_URL;

@Module
public class MainModule {

    @Provides
    @Singleton
    static ObjectMapper mapper() {
        return new ObjectMapper()
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
    }

    @Provides
    @Singleton
    //TODO 4.1:19:53
    static Retrofit retrofit(ObjectMapper mapper) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                //TODO 4.1:19:53
                .addCallAdapterFactory(RxJava3CallAdapterFactory.createAsync())
                .build();
    }

    @Provides
    static UserApiService userApiService(Retrofit retrofit) {
        return retrofit.create(UserApiService.class);
    }
}
