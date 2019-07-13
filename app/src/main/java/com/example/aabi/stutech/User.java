package com.example.aabi.stutech;

public class User {
    private String designation, userKey;

    public User(){}
    public User(String desig, String userKey) {
        this.designation = desig;
        this.userKey = userKey;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
}
