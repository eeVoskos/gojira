package com.gojira.util;

import android.util.Log;

import timber.log.Timber;

/**
 * @author Stratos Theodorou
 * @version 1.0
 * @since 14/05/2015
 */
public class CrashReportingTree extends Timber.Tree {

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        // Only log errors
        if (priority == Log.ERROR) {
            // TODO Report crash to a web service or something
        }
    }

}
