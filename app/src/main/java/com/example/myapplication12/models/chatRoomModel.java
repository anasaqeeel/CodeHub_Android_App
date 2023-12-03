package com.example.myapplication12.models;

import com.google.firebase.Timestamp;

import java.util.List;

public class chatRoomModel {
    String chatroomId;
    String LastMessage;
    List<String> userIds;

    String chatemail;
    Timestamp lastMessageTimestamp;
    String lastMessageSenderId;
    String lastMessage;
    public chatRoomModel() {
    }
    public chatRoomModel(String chatroomId,String chatemail, List<String> userIds, Timestamp lastMessageTimestamp, String lastMessageSenderId) {
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;
        this.chatemail=chatemail;
    }
    public String getChatroomId() {
        return chatroomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }



    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getLastMessage() {
        return LastMessage;
    }

    public String getChatemail() {
        return chatemail;
    }

    public void setChatemail(String chatemail) {
        this.chatemail = chatemail;
    }

    public void setLastMessage(String lastMessage) {
        LastMessage = lastMessage;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }
}
