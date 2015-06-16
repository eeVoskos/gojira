package com.gojira.data.api;

import com.gojira.util.Preferences;
import com.orhanobut.hawk.Hawk;

import retrofit.Endpoint;

/**
 * @author Stratos Theodorou
 * @version 1.0
 * @since 05/06/2015
 */
public class FlexibleEndpoint implements Endpoint {

    @Override
    public String getUrl() {
        return Hawk.get(Preferences.KEY_SERVER);
    }

    @Override
    public String getName() {
        return "default";
    }
}
