package com.example.aabi.stutech;

import android.net.Uri;

import com.google.firebase.database.ServerValue;

public class Likes {
    private String userId, userName, userPhoto;
    private Object timeStamp ;

    public Likes(String uid, String displayName, String userPhoto) {
        this.userId = uid;
        this.userName = displayName;
        this.timeStamp = ServerValue.TIMESTAMP;
        this.userPhoto = userPhoto;

    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public Likes(){}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}
