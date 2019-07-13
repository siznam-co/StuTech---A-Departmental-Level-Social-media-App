package com.example.aabi.stutech;

import com.google.firebase.database.ServerValue;

public class Post {


    private String postKey;
    private String title;
    private String description;
    private String fileURL;
    private String userId;
    private String userPhoto;
    private Object timeStamp ;
    private String subjectName;
    private String userName;
    private String fileType;
    private String fileName;

    // make sure to have an empty constructor inside ur model class
    public Post() {
    }

    public Post(String title, String description, String downloadLink, String userId, String userPhoto, String subjectName, String name, String Type, String filename) {
        this.title = title;
        this.description = description;
        this.fileURL = downloadLink;
        this.userId = userId;
        this.userPhoto = userPhoto;
        this.timeStamp = ServerValue.TIMESTAMP;
        this.subjectName = subjectName;
        this.userName = name;
        this.fileType = Type;
        this.fileName = filename;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}

