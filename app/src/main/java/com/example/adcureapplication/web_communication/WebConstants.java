package com.example.adcureapplication.web_communication;

public class WebConstants {

    /* To try the app with Enablex hosted service you need to set the kTry = true */
    public  static  final  boolean kTry = true;

    /*Your webservice host URL, Keet the defined host when kTry = true */

    public static final String kBaseURL = "https://demo.enablex.io/";

    /*The following information required, Only when kTry = true, When you hosted your own webservice remove these fileds*/

    /*Use enableX portal to create your app and get these following credentials*/
    public static final String kAppId = "605e170762c8bc5872454fc4";
    public static final String kAppkey = "3yseeenaVeNaueauUy4u3aHudyHuVapaSyTa";



    public static final String getRoomId = "createRoom/";
    public static final int getRoomIdCode = 1;
    public static final String validateRoomId = "getRoom/";
    public static final int validateRoomIdCode = 2;
    public static final String getTokenURL = "createToken/";
    public static final int getTokenURLCode = 3;
}
