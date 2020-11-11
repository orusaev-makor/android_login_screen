package com.example.signinscreen;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("user_details")
    private UserDetails userDetails;

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

}
