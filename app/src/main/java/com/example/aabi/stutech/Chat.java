package com.example.aabi.stutech;

public class Chat {
    private String designation, userKey, userName, userPhoto, userId;

    public Chat(){}

    public Chat(String desig, String userKey, String userName, String userPhoto, String userId) {
        this.designation = desig;
        this.userKey = userKey;
        this.userName = userName;
        this.userPhoto = userPhoto;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
