package com.gojira.data.api;

import com.gojira.data.io.DashboardsResponse;
import com.gojira.data.io.LoginResponse;
import com.gojira.data.io.SessionResponse;
import com.gojira.data.model.Credentials;
import com.gojira.data.model.Project;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * @author Stratos Theodorou
 * @version 1.0
 * @see <a href="https://docs.atlassian.com/jira/REST/latest/">JIRA REST API documentation</a>
 * @since 14/05/2015
 */
public interface JiraService {

    @GET("/rest/auth/1/session")
    void session(Callback<SessionResponse> callback);

    @POST("/rest/auth/1/session")
    void login(@Body Credentials credentials, Callback<LoginResponse> callback);

    @GET("/rest/api/latest/project")
    void projects(Callback<List<Project>> callback);

    @GET("/rest/api/latest/dashboard")
    void dashboards(Callback<DashboardsResponse> callback);

}
