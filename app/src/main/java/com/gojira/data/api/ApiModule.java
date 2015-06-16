package com.gojira.data.api;

import android.content.SharedPreferences;

import com.gojira.BuildConfig;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.converter.GsonConverter;

/**
 * Contains API-specific injections.
 *
 * @author Stratos Theodorou
 * @version 1.0
 * @since 14/05/2015
 */
@Module(complete = false, library = true)
public class ApiModule {

    @Provides
    @Singleton
    Endpoint provideEndpoint() {
        return new FlexibleEndpoint();
    }

    @Provides
    @Singleton
    RestAdapter provideRestAdapter(Endpoint endpoint, Client client, Gson gson, SharedPreferences preferences) {
        return new RestAdapter.Builder()
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.HEADERS : RestAdapter.LogLevel.NONE)
                .setClient(client)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(new ApiHeaders())
                .setEndpoint(endpoint)
                .build();
    }

    @Provides
    @Singleton
    JiraService provideJiraService(RestAdapter restAdapter) {
        return restAdapter.create(JiraService.class);
    }

}
