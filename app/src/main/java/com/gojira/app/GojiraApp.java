package com.gojira.app;

import android.app.Application;
import android.content.Context;

import com.gojira.ui.AndroidModule;
import com.gojira.util.CrashReportingTree;
import com.gojira.BuildConfig;
import com.orhanobut.hawk.Hawk;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import timber.log.Timber;

/**
 * @author Stratos Theodorou
 * @version 1.0
 * @since 14/05/2015
 */
public class GojiraApp extends Application {

    private ObjectGraph graph;

    @Override
    public void onCreate() {
        super.onCreate();

        // Setup logging
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        // Init Hawk for secure storage
        Hawk.init(this);

        // Prepare object graph for dependency injection
        graph = ObjectGraph.create(getModules(this).toArray());
        graph.inject(this);
    }

    protected List<Object> getModules(GojiraApp app) {
        return Arrays.asList(
                new ApplicationModule(app),
                new AndroidModule()
        );
    }

    public ObjectGraph getGraph() {
        return graph;
    }

    public static GojiraApp get(Context context) {
        return (GojiraApp) context.getApplicationContext();
    }
}
