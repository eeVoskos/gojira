package com.gojira.data.model;

/**
 * @author Stratos Theodorou
 * @version 1.0
 * @since 15/05/2015
 */
public class Dashboard extends Model {

    public String id;
    public String name;
    public String self;
    public String view;

    @Override
    public String toString() {
        return name;
    }

}
