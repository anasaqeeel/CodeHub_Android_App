package com.example.myapplication12.models;

import com.google.firebase.Timestamp;
public class UserModel {
    private String phone;
//  private String username;
    private Timestamp createdTimestamp;
    private String userId;
    private String fcmToken;
    private String email; // Add this line to have an email field in your UserModel

    private String password;
    private String Rating;

    private String skills;
    private String Feedback;
    public UserModel() {

    }

    public UserModel(String password, String fcmToken,String email,String userId,String skills,String Rating,String Feedback) {
        this.email= email;
        this.fcmToken=fcmToken;
        this.password = password;
        //this.createdTimestamp = createdTimestamp;
        this.userId = userId;
        this.skills=skills;
        this.Rating=Rating;
        this.Feedback=Feedback;
    }

    public String getFeedback() {
        return Feedback;
    }

    public void setFeedback(String feedback) {
        Feedback = feedback;
    }

    public String getEmail() {
        return email;
    }

    public String getSkills() {
        return skills;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setEmail(String email) {
        this.email = email;
    }


    public String getPhone() {
        return phone;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }

//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}