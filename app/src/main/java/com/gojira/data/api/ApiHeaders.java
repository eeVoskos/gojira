package com.gojira.data.api;

import com.gojira.util.BasicAuth;

import retrofit.RequestInterceptor;
import retrofit.client.Header;

/**
 * @author Stratos Theodorou
 * @version 1.0
 * @since 14/05/2015
 */
public class ApiHeaders implements RequestInterceptor {

    @Override
    public void intercept(RequestFacade request) {
        // Add basic auth header if we have stored credentials
        Header header = BasicAuth.getBasicAuthHeader();
        if (header != null) {
            request.addHeader(header.getName(), header.getValue());
        }
    }

}
