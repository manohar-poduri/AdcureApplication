package com.example.adcureapplication.SendNotificationPack;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.adcureapplication.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFireBaseMessagingService extends FirebaseMessagingService {
    String title,message;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
            super.onMessageReceived(remoteMessage);
            SharedPreferences sp=getSharedPreferences("SP_USER",MODE_PRIVATE);
            String savecrntusr=sp.getString("CURRENT_USERID","NONE");
            String sent=remoteMessage.getData().get("sent");
            String user=remoteMessage.getData().get("user");
            FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();
            if (fuser!=null && sent.equals(fuser.getUid())){
                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
                    sendOandAboveNoti(remoteMessage);
                }else {
                    sendNormalNotif(remoteMessage);
                }
            }
            title=remoteMessage.getData().get("Title");
            message=remoteMessage.getData().get("Message");

//        NotificationCompat.Builder builder =
//                new NotificationCompat.Builder(getApplicationContext())
//                        .setSmallIcon(R.drawable.ic_launcher_background)
//                        .setContentTitle(title)
//                        .setContentText(message);
//        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(0, builder.build());
    }

    private void sendOandAboveNoti(RemoteMessage remoteMessage) {
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, ChatActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("hisUid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        startActivity(intent);
        Uri snduri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder =
                 new NotificationCompat.Builder(this)
                        .setSmallIcon(Integer.parseInt(icon))
                        .setContentTitle(title)
                        .setContentText(body)
                .setAutoCancel(true)
                .setSound(snduri);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if (i>0) {
            j = i;
        }

        notificationManager.notify(j, builder.build());
    }
    private void sendNormalNotif(RemoteMessage remoteMessage) {
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, ChatActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("hisUid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        startActivity(intent);
        Uri snduri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
     OreoAndAbove oreoAndAbove=new OreoAndAbove(this);
     Notification.Builder builder=oreoAndAbove.getNotifications(title,body,pendingIntent,snduri,icon);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if (i>0) {
            j = i;
        }

        oreoAndAbove.getManager().notify(j, builder.build());
    }

}
