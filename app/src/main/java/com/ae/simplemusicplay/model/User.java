package com.ae.simplemusicplay.model;

import org.litepal.crud.DataSupport;

/**
 * Created by AE on 2015/12/29.
 */
public class User extends DataSupport {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
