package com.example.aabi.stutech;



public class Notifications {

    private String type, ref, user, userPhoto;
    private Object timeStamp ;


    public Notifications(){}

    public Notifications(String type, String ref, String user, String userPhoto, Object timeStamp) {
        this.type = type;
        this.ref = ref;
        this.user = user;
        this.userPhoto = userPhoto;
        this.timeStamp = timeStamp;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

}
