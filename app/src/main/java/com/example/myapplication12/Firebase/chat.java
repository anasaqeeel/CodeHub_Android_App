package com.example.myapplication12.Firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class chat extends FirebaseMessagingService{
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "token: "+ token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("FCM", "Message: "+ remoteMessage.getNotification().getBody() );

    }
}

   // private String encodeImage(Bitmap bitmap){
     //   int width=150;
       // int height=bitmap.getHeight()*width/bitmap.getWidth();
   //     Bitmap preview=Bitmap.createScaledBitmap(bitmap,width,height,false);
     //   ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
       // preview.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
  //      byte[] bytes=byteArrayOutputStream.toByteArray();
    //    return Base64.encodeToString(bytes, Base64.DEFAULT);
    //}