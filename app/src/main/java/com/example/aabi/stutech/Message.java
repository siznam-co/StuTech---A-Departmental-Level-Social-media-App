package com.example.aabi.stutech;


import com.google.firebase.database.ServerValue;

public class Message {
    private String message, senderName, receiverId, senderId;
    Object timeStamp;
    public Message() {
    }

    public Message(String message,String senderId, String senderName, String receiverId) {
        this.message = message;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
