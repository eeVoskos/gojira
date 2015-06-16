package com.gojira.data.io;

import com.gojira.data.model.Dashboard;

import java.util.List;

/**
 * @author Stratos Theodorou
 * @version 1.0
 * @since 14/05/2015
 */
public class DashboardsResponse {

    public int startAt;
    public int maxResults;
    public int total;
    public List<Dashboard> dashboards;

}
