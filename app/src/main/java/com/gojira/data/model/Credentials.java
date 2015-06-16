package com.gojira.data.model;

import android.util.Base64;

/**
 * @author Stratos Theodorou
 * @version 1.0
 * @since 14/05/2015
 */
public class Credentials extends Model {

    public String username;
    public String password;

    public Credentials() {
        super();
    }

    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getBasicAuthHeader() {
        String credentials = username + ":" + password;
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }

}
