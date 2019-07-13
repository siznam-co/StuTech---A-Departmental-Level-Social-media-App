package com.example.aabi.stutech;

import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.List;

class Teacher {
    public String name, email, designation,teacherId, teacherKey, userPhoto, userId, semester;
    private List<String> SubjectList = new ArrayList<String>();
    private List<String> section = new ArrayList<String>();
    private Object timeStamp ;

    public Teacher(){}
    public Teacher(String name, String email, String designation, String teacherId, String userId, List<String> section, String link, String userSemester, List<String> testList) {
        this.name = name;
        this.email = email;
        this.designation = designation;
        this.teacherId = teacherId;
        this.userId = userId;
        this.section.addAll(section);
        this.timeStamp = ServerValue.TIMESTAMP;
        this.userPhoto = link;
        this.semester = userSemester;
        this.SubjectList.addAll(testList);
    }

    public List<String> getSubjectList() {
        return SubjectList;
    }

    public void setSubjectList(List<String> subjectList) {
        SubjectList = subjectList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<String> getSection() {
        return section;
    }

    public void setSection(List<String> section) {
        this.section = section;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherKey() {
        return teacherKey;
    }

    public void setTeacherKey(String teacherKey) {
        this.teacherKey = teacherKey;
    }
}
