package com.gojira.data.model;

/**
 * @author Stratos Theodorou
 * @version 1.0
 * @since 15/05/2015
 */
public class Project extends Model {

    public String id;
    public String name;
    public String self;
    public String key;
    public Avatars avatarUrls;

    @Override
    public String toString() {
        return name;
    }

}
