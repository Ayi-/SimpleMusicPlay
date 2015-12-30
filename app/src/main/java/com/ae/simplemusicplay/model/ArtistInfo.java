package com.ae.simplemusicplay.model;

import org.litepal.crud.DataSupport;

/**
 * Created by chen on 2015/12/19.
 */
public class ArtistInfo extends DataSupport {

    private String name;

    private int id;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
