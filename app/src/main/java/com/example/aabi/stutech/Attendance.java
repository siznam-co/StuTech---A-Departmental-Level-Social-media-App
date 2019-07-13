package com.example.aabi.stutech;

public class Attendance {
    private String subject, marked;

    public Attendance(String currentSubject, String marked) {
        this.subject = currentSubject;
        this.marked = marked;
    }
    public Attendance(){}

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMarked() {
        return marked;
    }

    public void setMarked(String marked) {
        this.marked = marked;
    }
}
