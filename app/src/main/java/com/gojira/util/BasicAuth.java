package com.gojira.util;

import android.util.Base64;

import com.orhanobut.hawk.Hawk;

import retrofit.client.Header;

/**
 * @author Stratos Theodorou
 * @version 1.0
 * @since 10/06/2015
 */
public class BasicAuth {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    public static Header getBasicAuthHeader() {
        // Add basic auth header if we have stored credentials
        if (Hawk.contains(Preferences.KEY_USERNAME) && Hawk.contains(Preferences.KEY_PASSWORD)) {

            // Get credentials from secure storage
            String username = Hawk.get(Preferences.KEY_USERNAME);
            String password = Hawk.get(Preferences.KEY_PASSWORD);

            // Create basic auth header
            String credentials = username + ":" + password;
            String header = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

            // Add auth header in the request
            return new Header(AUTHORIZATION_HEADER, header);
        }
        return null;
    }

}
