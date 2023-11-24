package com.example.myapplication12.utilities;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;

public class firebaeUtil {

    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }
    public static boolean isLoggedIn(){
        if(currentUserId()!=null){
            return true;
        }
        return false;
    }


    //wrong logic as it should be email instead of currentuserid
    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("Login_Details").document(currentUserId());
    }

    public static CollectionReference allChatRoomCollectionref(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("Login_Details");
    }

//    public static DocumentReference getChatroomReference(String chatroomId){
//        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
//    }
//
//    public static CollectionReference getChatroomMessageReference(String chatroomId){
//        return getChatroomReference(chatroomId).collection("chats");
//    }
//
//    public static String getChatroomId(String userId1,String userId2){
//        if(userId1.hashCode()<userId2.hashCode()){
//            return userId1+"_"+userId2;
//        }else{
//            return userId2+"_"+userId1;
//        }
//    }
//
//    public static CollectionReference allChatroomCollectionReference(){
//        return FirebaseFirestore.getInstance().collection("chatrooms");
//    }
//

    public static int getOtherUserFromChatroom(List<String> userIds){
        if(userIds.get(0).equals(firebaeUtil.currentUserId())){
            //return otheruser
            return 1;
        }else{
            return 0;
        }
    }
//
    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("hh:mm:ss aa").format(timestamp.toDate());
    }
//
//    public static void logout(){
//        FirebaseAuth.getInstance().signOut();
//    }
//
//    public static StorageReference  getCurrentProfilePicStorageRef(){
//        return FirebaseStorage.getInstance().getReference().child("profile_pic")
//                .child(FirebaseUtil.currentUserId());
//    }
//
//    public static StorageReference  getOtherProfilePicStorageRef(String otherUserId){
//        return FirebaseStorage.getInstance().getReference().child("profile_pic")
//                .child(otherUserId);
//    }
    public  static DocumentReference getchatRoomRefrence(String chatroomid){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomid);

    }
    public static CollectionReference getChatroomMessageRef(String chatroomid){
        return getchatRoomRefrence(chatroomid).collection("chats");
    }
    public static String getchatroomid(String userid1, String userid2){
        if(userid1.hashCode()<userid2.hashCode()){
            return userid1+"_"+userid2;
        }
        else{
            return userid2+"_"+userid1;

        }
    }


}