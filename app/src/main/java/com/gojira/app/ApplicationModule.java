package com.gojira.app;

import android.app.Application;

import com.gojira.data.DataModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Stratos Theodorou
 * @version 1.0
 * @since 14/05/2015
 */
@Module(injects = GojiraApp.class, includes = DataModule.class, library = true)
public class ApplicationModule {

    private final GojiraApp app;

    public ApplicationModule(GojiraApp app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return app;
    }

}
