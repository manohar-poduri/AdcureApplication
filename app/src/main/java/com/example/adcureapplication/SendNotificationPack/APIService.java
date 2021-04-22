package com.example.adcureapplication.SendNotificationPack;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAr_hdF-Y:APA91bFWJyjv4f-oY0ylTXbGc24GIVba4yKf7Sxrhse0cT-1NR-_GXyxcgUZPOKoU6xGrGRtIP9UDZGrB3HIkmcJ2KXMxtYEzSey3sByv5ailcgPEv5g4AIpoQXcpzCgpFQm0d-g1fGY" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}

