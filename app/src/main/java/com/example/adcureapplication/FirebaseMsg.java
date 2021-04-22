package com.example.adcureapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FirebaseMsg extends FirebaseMessagingService {
    private RequestQueue requestQueue;
    private String url="https://fcm.googleapis.com/fcm/send";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String titl=remoteMessage.getNotification().getTitle();

        String body=remoteMessage.getNotification().getBody();
        Map<String,String>extraData=remoteMessage.getData();
        String festName=extraData.get("festivalName");
        String catefory=extraData.get("Category");

        NotificationCompat.Builder noti=new NotificationCompat.Builder(this,"TAC")
                .setContentTitle(titl)
                .setContentText(body)

                .setSmallIcon(R.drawable.app_logo);

        Intent intent;
        if(catefory.equals("Dussera")){
             intent=new Intent(this,MainActivity.class);


        }else {
            intent=new Intent(this,MainActivity.class);
        }
        intent.putExtra("fesName",festName);
        intent.putExtra("category",catefory);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,10,intent,PendingIntent.FLAG_UPDATE_CURRENT);
  noti.setContentIntent(pendingIntent);
  NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        int id=(int)System.currentTimeMillis();
  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
      NotificationChannel channel=new NotificationChannel("TAC","demo", NotificationManager.IMPORTANCE_HIGH);
      notificationManager.createNotificationChannel(channel);

  } notificationManager.notify(id,noti.build());
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        requestQueue= Volley.newRequestQueue(this);
        JSONObject mainobj=new JSONObject();
        try {

            mainobj.put("to","/topics/"+"news");
            JSONObject notifiobj=new JSONObject();
            notifiobj.put("title","Adcure");
            notifiobj.put("body","Thank you for using our app..");
            JSONObject extaObj=new JSONObject();
            extaObj.put("festivalName","Dussera");
            extaObj.put("Category","festival");
            mainobj.put("notification",notifiobj);
            mainobj.put("data",extaObj);
            JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, url,
                    mainobj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header=new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAAPyCkZxA:APA91bE5XghnZ2DMKtFtP1RHL8b7wbtf7GvDMv41EebXo7QiiVAgsfAAWphpXXIbvGUFbojBd1u0m7whgGJv1_aBJ1vS-vJo5ziD1XeN_gRV7FhWQoeXGKhetgdNhrGiB-bZdhVrTqpe");
                    return header;
                }
            }; requestQueue.add(request);
        }catch (
                JSONException e){
            e.printStackTrace();
        }
    }

}
