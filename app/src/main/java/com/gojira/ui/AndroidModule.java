package com.gojira.ui;

import com.gojira.app.ApplicationModule;

import dagger.Module;

/**
 * @author Stratos Theodorou
 * @version 1.0
 * @since 14/05/2015
 */
@Module(injects = {
        SplashActivity.class,
        LoginActivity.class,
        MainActivity.class,
        ProjectListFragment.class,
        DashboardListFragment.class,
        ProjectListFragment.ProjectRecyclerAdapter.class
}, addsTo = ApplicationModule.class, library = true)
public class AndroidModule {
}
