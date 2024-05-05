package com.example.myapplication12.utilities;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.myapplication12.models.UserModel;


public class androidutil {

    public static  void showToast(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, UserModel model){
        intent.putExtra("email",model.getEmail());
        intent.putExtra("pass",model.getPassword());
        intent.putExtra("userId",model.getUserId());
        intent.putExtra("fcmToken",model.getFcmToken());
        intent.putExtra("skills",model.getSkills());

    }

    public static UserModel getUserModelFromIntent(Intent intent){
        UserModel userModel = new UserModel();
        userModel.setEmail(intent.getStringExtra("email"));
        userModel.setPassword(intent.getStringExtra("password"));
        userModel.setFcmToken(intent.getStringExtra("fcmToken"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setSkills(intent.getStringExtra("skills"));

        return userModel;
    }
    public static UserModel getUserModelFrom(Intent intent){
        UserModel userModel = new UserModel();
        userModel.setEmail(intent.getStringExtra("email"));
        userModel.setPassword(intent.getStringExtra("password"));
        userModel.setFcmToken(intent.getStringExtra("fcmToken"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setSkills(intent.getStringExtra("skills"));
//        userModel.setHourly_Rate(intent.getStringExtra("hourly rate"));

        return userModel;
    }

    //public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
      //  Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    //}
}

