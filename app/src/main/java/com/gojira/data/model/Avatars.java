package com.gojira.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Stratos Theodorou
 * @version 1.0
 * @since 15/05/2015
 */
public class Avatars extends Model {

    @SerializedName("16x16")
    public String tiny;

    @SerializedName("24x24")
    public String small;

    @SerializedName("32x32")
    public String medium;

    @SerializedName("48x48")
    public String large;

}
