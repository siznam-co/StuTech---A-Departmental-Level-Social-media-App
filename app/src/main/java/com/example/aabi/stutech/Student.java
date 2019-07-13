package com.example.aabi.stutech;

import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.List;

public class Student {

    private String name, email, designation,roll, semester, batch, section, userPhoto, studentKey, userId;
    private Object timeStamp ;
    private List<String> SubjectList = new ArrayList<String>();


    public Student(String name, String email, String designation, String roll, String semester, String batch, String section, String userId, String s, List<String> subjectList) {
        this.name = name;
        this.email = email;
        this.designation = designation;
        this.roll = roll;
        this.semester = semester;
        this.batch = batch;
        this.section = section;
        this.userId = userId;
        this.userPhoto = s;
        this.timeStamp = ServerValue.TIMESTAMP;
        this.SubjectList.addAll(subjectList);
    }
    public Student(){}

    public List<String> getSubjectList() {
        return SubjectList;
    }

    public void setSubjectList(List<String> subjectList) {
        SubjectList = subjectList;
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

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getStudentKey() {
        return studentKey;
    }

    public void setStudentKey(String studentKey) {
        this.studentKey = studentKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setSemester() {
    }
}