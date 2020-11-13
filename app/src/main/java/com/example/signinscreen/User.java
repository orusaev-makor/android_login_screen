package com.example.signinscreen;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User {
    @SerializedName("user_details")
    private UserDetails userDetails;

    private String username;

    private int id;

    private List<User> users;

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public List<User> getUsers() {
        return users;
    }

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

}
