package com.example.aabi.stutech;

public class Attendance {
    private String userId, marked;

    public Attendance(String userId, String marked) {
        this.userId = userId;
        this.marked = marked;
    }
    public Attendance(){}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMarked() {
        return marked;
    }

    public void setMarked(String marked) {
        this.marked = marked;
    }
}
