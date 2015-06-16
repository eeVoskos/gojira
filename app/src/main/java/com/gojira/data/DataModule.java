package com.gojira.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gojira.BuildConfig;
import com.gojira.data.api.ApiModule;
import com.gojira.util.BasicAuth;
import com.gojira.util.DateTimeConverter;
import com.gojira.util.Preferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.hawk.Hawk;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.client.Client;
import retrofit.client.Header;
import retrofit.client.OkClient;

/**
 * @author Stratos Theodorou
 * @version 1.0
 * @since 14/05/2015
 */
@Module(includes = ApiModule.class, complete = false, library = true)
public class DataModule {

    /**
     * Disk cache size to use (50MB)
     */
    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024;

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Application app) {
        OkHttpClient client = new OkHttpClient();

        // Install an HTTP cache in the internal cache directory
        File cacheDir = new File(app.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        client.setCache(cache);

        return client;
    }

    @Provides
    @Singleton
    Picasso providePicasso(Application app, OkHttpClient client) {
        // Create client for picasso with global client specs
        OkHttpClient picassoClient = client.clone();

        // Intercept image loading requests to add auth header
        picassoClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String url = chain.request().urlString();

                // Get the current server from secure storage
                String server = Hawk.get(Preferences.KEY_SERVER);

                // Add the basic auth header only in Jira server requests
                if (url.contains(server)) {
                    Request.Builder builder = chain.request().newBuilder();
                    Header header = BasicAuth.getBasicAuthHeader();
                    if (header != null) {
                        builder.addHeader(header.getName(), header.getValue());
                    }
                    return chain.proceed(builder.build());
                }

                // Skip image requests that are not for the current Jira server
                else {
                    return chain.proceed(chain.request());
                }
            }
        });

        return new Picasso.Builder(app)
                .downloader(new OkHttpDownloader(picassoClient))
                .loggingEnabled(BuildConfig.DEBUG)
                .build();
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application app) {
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new DateTimeConverter())
                .serializeNulls()
                .create();
    }

    @Provides
    @Singleton
    Client provideClient(OkHttpClient client) {
        return new OkClient(client);
    }

}
