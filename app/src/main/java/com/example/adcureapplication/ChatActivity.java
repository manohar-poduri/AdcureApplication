package com.example.adcureapplication;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.DnsResolver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.adcureapplication.SendNotificationPack.APIService;
import com.example.adcureapplication.SendNotificationPack.Client;
import com.example.adcureapplication.SendNotificationPack.Data;
import com.example.adcureapplication.SendNotificationPack.MyResponse;
import com.example.adcureapplication.SendNotificationPack.NotificationSender;
import com.example.adcureapplication.SendNotificationPack.Token;

import com.example.adcureapplication.activity.DashboardActivity;
import com.example.adcureapplication.activity.VideoConferenceActivity;
import com.example.adcureapplication.web_communication.WebCall;
import com.example.adcureapplication.web_communication.WebConstants;
import com.example.adcureapplication.web_communication.WebResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import videortc.CallActivity;
import videortc.ConnectActivity;

public class ChatActivity extends AppCompatActivity implements WebResponse
{    private String token;
    private SharedPreferences sharedPreferences;
    private String room_Id;
    private static final String TAG = "ConnectActivity";
    private static final int CONNECTION_REQUEST = 1;
    private static final int PERMISSION_REQUEST = 2;
    private static final int REMOVE_FAVORITE_INDEX = 0;
    private static boolean commandLineRun;
    private ImageButton addFavoriteButton;
    private APIService apiService; TextView help;
    private String url="https://fcm.googleapis.com/fcm/send";
    private RequestQueue requestQueue;private TextView tw;
    private ListView roomListView;
    private SharedPreferences sharedPref;
    private String keyprefResolution;
    private String keyprefFps;
    private String keyprefVideoBitrateType;
    private String keyprefVideoBitrateValue;
    private String keyprefAudioBitrateType;
    private String keyprefAudioBitrateValue;
    private String keyprefRoomServerUrl;
    private String keyprefRoom;
    Boolean notify=false; String hisuid;
    private String messageReceiverID, messageReceiverName, messageReceiverImage, messageSenderID,unme;

    private TextView userName, userLastSeen;
    private CircleImageView userImage;

    private Toolbar ChatToolBar;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private ImageButton SendMessageButton, SendFilesButton;
    private EditText MessageInputText;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;

    private ImageView iv,cl;
    private String saveCurrentTime, saveCurrentDate,myuid;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);
//Toolbar toolbar=new Toolbar(this);
        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getApplicationContext().getSharedPreferences("APP_PREF_ONETOONE",MODE_PRIVATE);

        //  setClickListener();
        //   getSupportActionBar().setTitle("Quick App");
        setSharedPreference();
        myuid=mAuth.getCurrentUser().getUid();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        requestQueue= Volley.newRequestQueue(this);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        keyprefResolution = getString(R.string.pref_resolution_key);
        keyprefFps = getString(R.string.pref_fps_key);
        keyprefVideoBitrateType = getString(R.string.pref_maxvideobitrate_key);
        keyprefVideoBitrateValue = getString(R.string.pref_maxvideobitratevalue_key);
        keyprefAudioBitrateType = getString(R.string.pref_startaudiobitrate_key);
        keyprefAudioBitrateValue = getString(R.string.pref_startaudiobitratevalue_key);
        keyprefRoomServerUrl = getString(R.string.pref_room_server_url_key);
        keyprefRoom = getString(R.string.pref_room_key);
        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        hisuid = getIntent().getStringExtra("hisUid");

        messageReceiverID = getIntent().getExtras().get("numb").toString();
        messageReceiverName = getIntent().getExtras().get("name").toString();
        unme=getIntent().getExtras().get("unme").toString();

        IntializeControllers();
        // requestPermissions();

        userName.setText(messageReceiverName);


        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
//                notify=true;
//                FirebaseDatabase.getInstance().getReference().child("All Doctors").child(mAuth.getCurrentUser().getUid()).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        String usertoken=dataSnapshot.getValue(String.class);
//                        String messageText = MessageInputText.getText().toString();
//                        if (notify){
//                            sendNotifications(hisuid, messageReceiverName,messageText);
//
//                        }notify=false;
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
                SendMessage();
            }
        });

        UpdateToken();
        DisplayLastSeen();
    }
    private void UpdateToken(){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token= new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }

    private void checkStatus(){
        FirebaseUser user=mAuth.getCurrentUser();
        if (user!=null){
            String mid=user.getUid();
            SharedPreferences sp=getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putString("Current USERID",mid);
            editor.apply();
        }
    }
    // public void sendNotifications(String usertoken, String title, String message) {
//        Data data = new Data(title, message);
//        NotificationSender sender = new NotificationSender(data, usertoken);
//        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
//            @Override
//            public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
//                if (response.code() == 200) {
//                    if (response.body().success != 1) {
//                        Toast.makeText(ChatActivity.this, "Failed ", Toast.LENGTH_LONG);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MyResponse> call, Throwable t) {
//
//            }
//        });
//    }
    public void sendNotifications(String hisuid, String name, String message) {
        DatabaseReference allTokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=allTokens.orderByKey().equalTo(hisuid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    Token token=ds.getValue(Token.class);
                    Data data=new Data(myuid,name+":"+message,"New Message",hisuid,R.drawable.app_logo);
                    NotificationSender sender=new NotificationSender(data,token.getToken());
                    apiService.sendNotifcation(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                                    Toast.makeText(ChatActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        Data data = new Data(title, message);
//        NotificationSender sender = new NotificationSender(data, usertoken);
//        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
//            @Override
//            public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
//                if (response.code() == 200) {
//                    if (response.body().success != 1) {
//                        Toast.makeText(ChatActivity.this, "Failed ", Toast.LENGTH_LONG);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MyResponse> call, Throwable t) {
//
//            }
//        });
    }

    private void IntializeControllers()
    {
        ChatToolBar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(ChatToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);
        iv=(ImageView)actionBarView.findViewById(R.id.videocl);
        help = (TextView) findViewById(R.id.helpc);
        iv.setOnClickListener(connectListener);
        cl=(ImageView)actionBarView.findViewById(R.id.callc);
        userName = (TextView) findViewById(R.id.custom_profile_name);
        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);
        cl.setOnClickListener(connectListener);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toHelp();
            }
        });
        SendMessageButton = (ImageButton) findViewById(R.id.send_message_btn);
        SendFilesButton = (ImageButton) findViewById(R.id.send_files_btn);
        MessageInputText = (EditText) findViewById(R.id.input_message);

        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = (RecyclerView) findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);


        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());
    }

    private void toHelp() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        View v = LayoutInflater.from(ChatActivity.this).inflate(R.layout.help_lay,null,false);
        SearchableSpinner spinner=(SearchableSpinner)v.findViewById(R.id.sp1);
        final String[] itms2 = new String[]{"Doctors","Appointments","Consultation","Payments","Others"};
        ArrayAdapter <String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itms2);
        spinner.setTitle("Select Category");
        spinner.setAdapter(adapter2);

        String sp1=spinner.getSelectedItem().toString();

        final EditText text = v.findViewById(R.id.issue);
        builder.setView(v);
        DatabaseReference  rootRef= FirebaseDatabase.getInstance().getReference();//app/use

        builder.setCancelable(false);
        builder.setPositiveButton("send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String iss = text.getText().toString();
                if (TextUtils.isEmpty(iss)){
                    text.setError("can't send..you did'nt written your issue");
                }else{
                    Calendar calendar=Calendar.getInstance();
                    SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,YYYY");
                    saveCurrentDate=currentDate.format(calendar.getTime());
                    SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
                    saveCurrentTime=currentTime.format(calendar.getTime());
                    String   userRandomKey=saveCurrentDate+saveCurrentTime;
                    mAuth= FirebaseAuth.getInstance();

                    FirebaseUser firebaseUser=mAuth.getCurrentUser();
                    String    currentUserid=mAuth.getCurrentUser().getUid();

                    RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!(snapshot.child("Issues").child(userRandomKey).exists())){
                                final HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("issue", iss);
                                hashMap.put("user_id",currentUserid);
                                hashMap.put("category",sp1);
                                hashMap.put("time",saveCurrentTime);
                                hashMap.put("date",saveCurrentDate);
                                RootRef.child("Issues").child(userRandomKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){

                                            RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(!(snapshot.child("Users").child(currentUserid).child("My Issues").child(userRandomKey).exists())){
                                                        final HashMap<String, Object> hashMa = new HashMap<>();
                                                        hashMa.put("issue", iss);
                                                        hashMa.put("user_id",currentUserid);
                                                        hashMa.put("time",saveCurrentTime);
                                                        hashMa.put("date",saveCurrentDate);
                                                        hashMa.put("category",sp1);
                                                        RootRef.child("Users").child(currentUserid).child("My Issues").child(userRandomKey).updateChildren(hashMa).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                ////Toast.makeText(DetailsOfUser.this, "Success done", Toast.LENGTH_SHORT).show();
                                                                if (task.isSuccessful()){
                                                                    Toast.makeText(ChatActivity.this, "The team will contact you soon...", Toast.LENGTH_SHORT).show();
                                                                }
                                                                text.setText("");
                                                                dialogInterface.dismiss();

                                                            }
                                                        }) ;
                                                    } else {

                                                        dialogInterface.dismiss();

                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }
                                });
                            }else {
                                Toast.makeText(ChatActivity.this, "can not send..", Toast.LENGTH_SHORT).show();
//            Toast.makeText(DetailsOfUser.this, "Success", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(DetailsOfUser.this,PaymentActivity.class));
                                //loadingbar.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    }) ;
                }
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void DisplayLastSeen()
    {
        RootRef.child("Users").child(messageReceiverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.child("userState").hasChild("state"))
                        {
                            String state = dataSnapshot.child("userState").child("state").getValue().toString();
                            String date = dataSnapshot.child("userState").child("date").getValue().toString();
                            String time = dataSnapshot.child("userState").child("time").getValue().toString();

                            if (state.equals("online"))
                            {
                                userLastSeen.setText("online");
                            }
                            else if (state.equals("offline"))
                            {
                                userLastSeen.setText("Last Seen: " + date + " " + time);
                            }
                        }
                        else
                        {
                            userLastSeen.setText("offline");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        Messages messages = dataSnapshot.getValue(Messages.class);

                        messagesList.add(messages);

                        messageAdapter.notifyDataSetChanged();

                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



    private void SendMessage()
    {
        String messageText = MessageInputText.getText().toString();

        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "first write your message...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

            DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                    .child(messageSenderID).child(messageReceiverID).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);
            messageTextBody.put("to", messageReceiverID);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    MessageInputText.setText("");
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(keyprefRoom, unme);
        editor.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkStatus();
        String room = sharedPref.getString(keyprefRoom, "");
        room.length();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONNECTION_REQUEST && commandLineRun) {
            Log.d(TAG, "Return: " + resultCode);
            setResult(resultCode);
            commandLineRun = false;
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            String[] missingPermissions = getMissingPermissions();
            if (missingPermissions.length != 0) {
                // User didn't grant all the permissions. Warn that the application might not work
                // correctly.
                new AlertDialog.Builder(this)
                        .setMessage("Permissions missing")
                        .setPositiveButton("Yes",
                                (dialog, id) -> {
                                    // User wants to try giving the permissions again.
                                    dialog.cancel();
                                    requestPermissions();
                                })
                        .setNegativeButton("No",
                                (dialog, id) -> {
                                    // User doesn't want to give the permissions.
                                    dialog.cancel();
                                    onPermissionsGranted();
                                })
                        .show();
            } else {
                // All permissions granted.
                onPermissionsGranted();
            }
        }
    }

    private void onPermissionsGranted() {
        // If an implicit VIEW intent is launching the app, go directly to that URL.
        final Intent intent = getIntent();
        if ("android.intent.action.VIEW".equals(intent.getAction()) && !commandLineRun) {
            boolean loopback = intent.getBooleanExtra(CallActivity.EXTRA_LOOPBACK, false);
            int runTimeMs = intent.getIntExtra(CallActivity.EXTRA_RUNTIME, 0);
            boolean useValuesFromIntent =
                    intent.getBooleanExtra(CallActivity.EXTRA_USE_VALUES_FROM_INTENT, false);
            String room = sharedPref.getString(keyprefRoom, "");
            connectToRoom(room, true, loopback, useValuesFromIntent, runTimeMs);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Dynamic permissions are not required before Android M.
            onPermissionsGranted();
            return;
        }
        String[] missingPermissions = getMissingPermissions();
        if (missingPermissions.length != 0) {
            requestPermissions(missingPermissions, PERMISSION_REQUEST);
        } else {
            onPermissionsGranted();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private String[] getMissingPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return new String[0];
        }
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Failed to retrieve permissions.");
            return new String[0];
        }
        if (info.requestedPermissions == null) {
            Log.w(TAG, "No requested permissions.");
            return new String[0];
        }
        ArrayList<String> missingPermissions = new ArrayList<>();
        for (int i = 0; i < info.requestedPermissions.length; i++) {
            if ((info.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) == 0) {
                missingPermissions.add(info.requestedPermissions[i]);
            }
        }
        Log.d(TAG, "Missing permissions: " + missingPermissions);
        return missingPermissions.toArray(new String[missingPermissions.size()]);
    }
    private boolean validateUrl(String url) {
        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
            return true;
        }
        new AlertDialog.Builder(this)
                .setTitle(getText(R.string.invalid_url_title))
                .setMessage(getString(R.string.invalid_url_text, url))
                .setCancelable(false)
                .setNeutralButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .create()
                .show();
        return false;
    }

    /**
     * Get a value from the shared preference or from the intent, if it does not
     * exist the default is used.
     */
    @Nullable
    private String sharedPrefGetString(
            int attributeId, String intentName, int defaultId, boolean useFromIntent) {
        String defaultValue = getString(defaultId);
        if (useFromIntent) {
            String value = getIntent().getStringExtra(intentName);
            if (value != null) {
                return value;
            }
            return defaultValue;
        } else {
            String attributeName = getString(attributeId);
            return sharedPref.getString(attributeName, defaultValue);
        }
    }
    /**
     * Get a value from the shared preference or from the intent, if it does not
     * exist the default is used.
     */
    private int sharedPrefGetInteger(
            int attributeId, String intentName, int defaultId, boolean useFromIntent) {
        String defaultString = getString(defaultId);
        int defaultValue = Integer.parseInt(defaultString);
        if (useFromIntent) {
            return getIntent().getIntExtra(intentName, defaultValue);
        } else {
            String attributeName = getString(attributeId);
            String value = sharedPref.getString(attributeName, defaultString);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Wrong setting for: " + attributeName + ":" + value);
                return defaultValue;
            }
        }
    }
    /**
     * Get a value from the shared preference or from the intent, if it does not
     * exist the default is used.
     */
    private boolean sharedPrefGetBoolean(
            int attributeId, String intentName, int defaultId, boolean useFromIntent) {
        boolean defaultValue = Boolean.parseBoolean(getString(defaultId));
        if (useFromIntent) {
            return getIntent().getBooleanExtra(intentName, defaultValue);
        } else {
            String attributeName = getString(attributeId);
            return sharedPref.getBoolean(attributeName, defaultValue);
        }
    }
    @SuppressWarnings("StringSplitter")
    private void connectToRoom(String roomId, boolean commandLineRun, boolean loopback,
                               boolean useValuesFromIntent, int runTimeMs) {
        ChatActivity.commandLineRun = commandLineRun;
        // roomId is random for loopback.
        if (loopback) {
            roomId = Integer.toString((new Random()).nextInt(100000000));
        }
        String roomUrl = sharedPref.getString(
                keyprefRoomServerUrl, getString(R.string.pref_room_server_url_default));
        // Video call enabled flag.
        boolean videoCallEnabled = sharedPrefGetBoolean(R.string.pref_videocall_key,
                CallActivity.EXTRA_VIDEO_CALL, R.string.pref_videocall_default, useValuesFromIntent);
        // Use screencapture option.
        boolean useScreencapture = sharedPrefGetBoolean(R.string.pref_screencapture_key,
                CallActivity.EXTRA_SCREENCAPTURE, R.string.pref_screencapture_default, useValuesFromIntent);
        // Use Camera2 option.
        boolean useCamera2 = sharedPrefGetBoolean(R.string.pref_camera2_key, CallActivity.EXTRA_CAMERA2,
                R.string.pref_camera2_default, useValuesFromIntent);
        // Get default codecs.
        String videoCodec = sharedPrefGetString(R.string.pref_videocodec_key,
                CallActivity.EXTRA_VIDEOCODEC, R.string.pref_videocodec_default, useValuesFromIntent);
        String audioCodec = sharedPrefGetString(R.string.pref_audiocodec_key,
                CallActivity.EXTRA_AUDIOCODEC, R.string.pref_audiocodec_default, useValuesFromIntent);
        // Check HW codec flag.
        boolean hwCodec = sharedPrefGetBoolean(R.string.pref_hwcodec_key,
                CallActivity.EXTRA_HWCODEC_ENABLED, R.string.pref_hwcodec_default, useValuesFromIntent);
        // Check Capture to texture.
        boolean captureToTexture = sharedPrefGetBoolean(R.string.pref_capturetotexture_key,
                CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, R.string.pref_capturetotexture_default,
                useValuesFromIntent);
        // Check FlexFEC.
        boolean flexfecEnabled = sharedPrefGetBoolean(R.string.pref_flexfec_key,
                CallActivity.EXTRA_FLEXFEC_ENABLED, R.string.pref_flexfec_default, useValuesFromIntent);
        // Check Disable Audio Processing flag.
        boolean noAudioProcessing = sharedPrefGetBoolean(R.string.pref_noaudioprocessing_key,
                CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, R.string.pref_noaudioprocessing_default,
                useValuesFromIntent);
        boolean aecDump = sharedPrefGetBoolean(R.string.pref_aecdump_key,
                CallActivity.EXTRA_AECDUMP_ENABLED, R.string.pref_aecdump_default, useValuesFromIntent);
        boolean saveInputAudioToFile =
                sharedPrefGetBoolean(R.string.pref_enable_save_input_audio_to_file_key,
                        CallActivity.EXTRA_SAVE_INPUT_AUDIO_TO_FILE_ENABLED,
                        R.string.pref_enable_save_input_audio_to_file_default, useValuesFromIntent);
        // Check OpenSL ES enabled flag.
        boolean useOpenSLES = sharedPrefGetBoolean(R.string.pref_opensles_key,
                CallActivity.EXTRA_OPENSLES_ENABLED, R.string.pref_opensles_default, useValuesFromIntent);
        // Check Disable built-in AEC flag.
        boolean disableBuiltInAEC = sharedPrefGetBoolean(R.string.pref_disable_built_in_aec_key,
                CallActivity.EXTRA_DISABLE_BUILT_IN_AEC, R.string.pref_disable_built_in_aec_default,
                useValuesFromIntent);
        // Check Disable built-in AGC flag.
        boolean disableBuiltInAGC = sharedPrefGetBoolean(R.string.pref_disable_built_in_agc_key,
                CallActivity.EXTRA_DISABLE_BUILT_IN_AGC, R.string.pref_disable_built_in_agc_default,
                useValuesFromIntent);
        // Check Disable built-in NS flag.
        boolean disableBuiltInNS = sharedPrefGetBoolean(R.string.pref_disable_built_in_ns_key,
                CallActivity.EXTRA_DISABLE_BUILT_IN_NS, R.string.pref_disable_built_in_ns_default,
                useValuesFromIntent);
        // Check Disable gain control
        boolean disableWebRtcAGCAndHPF = sharedPrefGetBoolean(
                R.string.pref_disable_webrtc_agc_and_hpf_key, CallActivity.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF,
                R.string.pref_disable_webrtc_agc_and_hpf_key, useValuesFromIntent);
        // Get video resolution from settings.
        int videoWidth = 0;
        int videoHeight = 0;
        if (useValuesFromIntent) {
            videoWidth = getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_WIDTH, 0);
            videoHeight = getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_HEIGHT, 0);
        }
        if (videoWidth == 0 && videoHeight == 0) {
            String resolution =
                    sharedPref.getString(keyprefResolution, getString(R.string.pref_resolution_default));
            String[] dimensions = resolution.split("[ x]+");
            if (dimensions.length == 2) {
                try {
                    videoWidth = Integer.parseInt(dimensions[0]);
                    videoHeight = Integer.parseInt(dimensions[1]);
                } catch (NumberFormatException e) {
                    videoWidth = 0;
                    videoHeight = 0;
                    Log.e(TAG, "Wrong video resolution setting: " + resolution);
                }
            }
        }
        // Get camera fps from settings.
        int cameraFps = 0;
        if (useValuesFromIntent) {
            cameraFps = getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_FPS, 0);
        }
        if (cameraFps == 0) {
            String fps = sharedPref.getString(keyprefFps, getString(R.string.pref_fps_default));
            String[] fpsValues = fps.split("[ x]+");
            if (fpsValues.length == 2) {
                try {
                    cameraFps = Integer.parseInt(fpsValues[0]);
                } catch (NumberFormatException e) {
                    cameraFps = 0;
                    Log.e(TAG, "Wrong camera fps setting: " + fps);
                }
            }
        }
        // Check capture quality slider flag.
        boolean captureQualitySlider = sharedPrefGetBoolean(R.string.pref_capturequalityslider_key,
                CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED,
                R.string.pref_capturequalityslider_default, useValuesFromIntent);
        // Get video and audio start bitrate.
        int videoStartBitrate = 0;
        if (useValuesFromIntent) {
            videoStartBitrate = getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_BITRATE, 0);
        }
        if (videoStartBitrate == 0) {
            String bitrateTypeDefault = getString(R.string.pref_maxvideobitrate_default);
            String bitrateType = sharedPref.getString(keyprefVideoBitrateType, bitrateTypeDefault);
            if (!bitrateType.equals(bitrateTypeDefault)) {
                String bitrateValue = sharedPref.getString(
                        keyprefVideoBitrateValue, getString(R.string.pref_maxvideobitratevalue_default));
                videoStartBitrate = Integer.parseInt(bitrateValue);
            }
        }
        int audioStartBitrate = 0;
        if (useValuesFromIntent) {
            audioStartBitrate = getIntent().getIntExtra(CallActivity.EXTRA_AUDIO_BITRATE, 0);
        }
        if (audioStartBitrate == 0) {
            String bitrateTypeDefault = getString(R.string.pref_startaudiobitrate_default);
            String bitrateType = sharedPref.getString(keyprefAudioBitrateType, bitrateTypeDefault);
            if (!bitrateType.equals(bitrateTypeDefault)) {
                String bitrateValue = sharedPref.getString(
                        keyprefAudioBitrateValue, getString(R.string.pref_startaudiobitratevalue_default));
                audioStartBitrate = Integer.parseInt(bitrateValue);
            }
        }
        // Check statistics display option.
        boolean displayHud = sharedPrefGetBoolean(R.string.pref_displayhud_key,
                CallActivity.EXTRA_DISPLAY_HUD, R.string.pref_displayhud_default, useValuesFromIntent);
        boolean tracing = sharedPrefGetBoolean(R.string.pref_tracing_key, CallActivity.EXTRA_TRACING,
                R.string.pref_tracing_default, useValuesFromIntent);
        // Check Enable RtcEventLog.
        boolean rtcEventLogEnabled = sharedPrefGetBoolean(R.string.pref_enable_rtceventlog_key,
                CallActivity.EXTRA_ENABLE_RTCEVENTLOG, R.string.pref_enable_rtceventlog_default,
                useValuesFromIntent);
        // Get datachannel options
        boolean dataChannelEnabled = sharedPrefGetBoolean(R.string.pref_enable_datachannel_key,
                CallActivity.EXTRA_DATA_CHANNEL_ENABLED, R.string.pref_enable_datachannel_default,
                useValuesFromIntent);
        boolean ordered = sharedPrefGetBoolean(R.string.pref_ordered_key, CallActivity.EXTRA_ORDERED,
                R.string.pref_ordered_default, useValuesFromIntent);
        boolean negotiated = sharedPrefGetBoolean(R.string.pref_negotiated_key,
                CallActivity.EXTRA_NEGOTIATED, R.string.pref_negotiated_default, useValuesFromIntent);
        int maxRetrMs = sharedPrefGetInteger(R.string.pref_max_retransmit_time_ms_key,
                CallActivity.EXTRA_MAX_RETRANSMITS_MS, R.string.pref_max_retransmit_time_ms_default,
                useValuesFromIntent);
        int maxRetr =
                sharedPrefGetInteger(R.string.pref_max_retransmits_key, CallActivity.EXTRA_MAX_RETRANSMITS,
                        R.string.pref_max_retransmits_default, useValuesFromIntent);
        int id = sharedPrefGetInteger(R.string.pref_data_id_key, CallActivity.EXTRA_ID,
                R.string.pref_data_id_default, useValuesFromIntent);
        String protocol = sharedPrefGetString(R.string.pref_data_protocol_key,
                CallActivity.EXTRA_PROTOCOL, R.string.pref_data_protocol_default, useValuesFromIntent);
        // Start AppRTCMobile activity.
        Log.d(TAG, "Connecting to room " + roomId + " at URL " + roomUrl);
        if (validateUrl(roomUrl)) {
            Uri uri = Uri.parse(roomUrl);
            Intent intent = new Intent(this, CallActivity.class);
            intent.setData(uri);
            intent.putExtra(CallActivity.EXTRA_ROOMID, roomId);
            intent.putExtra(CallActivity.EXTRA_LOOPBACK, loopback);
            intent.putExtra(CallActivity.EXTRA_VIDEO_CALL, videoCallEnabled);
            intent.putExtra(CallActivity.EXTRA_SCREENCAPTURE, useScreencapture);
            intent.putExtra(CallActivity.EXTRA_CAMERA2, useCamera2);
            intent.putExtra(CallActivity.EXTRA_VIDEO_WIDTH, videoWidth);
            intent.putExtra(CallActivity.EXTRA_VIDEO_HEIGHT, videoHeight);
            intent.putExtra(CallActivity.EXTRA_VIDEO_FPS, cameraFps);
            intent.putExtra(CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, captureQualitySlider);
            intent.putExtra(CallActivity.EXTRA_VIDEO_BITRATE, videoStartBitrate);
            intent.putExtra(CallActivity.EXTRA_VIDEOCODEC, videoCodec);
            intent.putExtra(CallActivity.EXTRA_HWCODEC_ENABLED, hwCodec);
            intent.putExtra(CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, captureToTexture);
            intent.putExtra(CallActivity.EXTRA_FLEXFEC_ENABLED, flexfecEnabled);
            intent.putExtra(CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, noAudioProcessing);
            intent.putExtra(CallActivity.EXTRA_AECDUMP_ENABLED, aecDump);
            intent.putExtra(CallActivity.EXTRA_SAVE_INPUT_AUDIO_TO_FILE_ENABLED, saveInputAudioToFile);
            intent.putExtra(CallActivity.EXTRA_OPENSLES_ENABLED, useOpenSLES);
            intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_AEC, disableBuiltInAEC);
            intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_AGC, disableBuiltInAGC);
            intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_NS, disableBuiltInNS);
            intent.putExtra(CallActivity.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, disableWebRtcAGCAndHPF);
            intent.putExtra(CallActivity.EXTRA_AUDIO_BITRATE, audioStartBitrate);
            intent.putExtra(CallActivity.EXTRA_AUDIOCODEC, audioCodec);
            intent.putExtra(CallActivity.EXTRA_DISPLAY_HUD, displayHud);
            intent.putExtra(CallActivity.EXTRA_TRACING, tracing);
            intent.putExtra(CallActivity.EXTRA_ENABLE_RTCEVENTLOG, rtcEventLogEnabled);
            intent.putExtra(CallActivity.EXTRA_CMDLINE, commandLineRun);
            intent.putExtra(CallActivity.EXTRA_RUNTIME, runTimeMs);
            intent.putExtra(CallActivity.EXTRA_DATA_CHANNEL_ENABLED, dataChannelEnabled);
            if (dataChannelEnabled) {
                intent.putExtra(CallActivity.EXTRA_ORDERED, ordered);
                intent.putExtra(CallActivity.EXTRA_MAX_RETRANSMITS_MS, maxRetrMs);
                intent.putExtra(CallActivity.EXTRA_MAX_RETRANSMITS, maxRetr);
                intent.putExtra(CallActivity.EXTRA_PROTOCOL, protocol);
                intent.putExtra(CallActivity.EXTRA_NEGOTIATED, negotiated);
                intent.putExtra(CallActivity.EXTRA_ID, id);
            }
            if (useValuesFromIntent) {
                if (getIntent().hasExtra(CallActivity.EXTRA_VIDEO_FILE_AS_CAMERA)) {
                    String videoFileAsCamera =
                            getIntent().getStringExtra(CallActivity.EXTRA_VIDEO_FILE_AS_CAMERA);
                    intent.putExtra(CallActivity.EXTRA_VIDEO_FILE_AS_CAMERA, videoFileAsCamera);
                }
                if (getIntent().hasExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE)) {
                    String saveRemoteVideoToFile =
                            getIntent().getStringExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE);
                    intent.putExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE, saveRemoteVideoToFile);
                }
                if (getIntent().hasExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH)) {
                    int videoOutWidth =
                            getIntent().getIntExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0);
                    intent.putExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, videoOutWidth);
                }
                if (getIntent().hasExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT)) {
                    int videoOutHeight =
                            getIntent().getIntExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0);
                    intent.putExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, videoOutHeight);
                }
            }
            startActivityForResult(intent, CONNECTION_REQUEST);
        }
    }
    private final View.OnClickListener connectListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new AlertDialog.Builder(ChatActivity.this)
                    .setMessage("Do you want to start video call with your doctor ?")
                    .setPositiveButton("Yes",
                            (dialog, id) -> {
                                // User wants to try giving the permissions again.
                                dialog.cancel();

                                new WebCall(ChatActivity.this, ChatActivity.this, null, WebConstants.getRoomId, WebConstants.getRoomIdCode, false).execute();

                                validateRoomIDWebCall();

                                //   connectToRoom(unme, false, false, false, 0);
                            })
                    .setNegativeButton("No",
                            (dialog, id) -> {
                                // User doesn't want to give the permissions.
                                dialog.cancel();

                            })
                    .show();

        }
    };

    private void validateRoomIDWebCall() {
        new WebCall(this, this, null, WebConstants.validateRoomId + room_Id, WebConstants.validateRoomIdCode, true).execute();

    }
    @Override
    public void onWebResponse(String response, int callCode) {
        switch (callCode) {
            case WebConstants.getRoomIdCode:
                onGetRoomIdSuccess(response);
                break;
            case WebConstants.getTokenURLCode:
                onGetTokenSuccess(response);
                break;
            case WebConstants.validateRoomIdCode:
                onVaidateRoomIdSuccess(response);
                break;
        }

    }

    private void onVaidateRoomIdSuccess(String response) {
        Log.e("responsevalidate", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString("result").trim().equalsIgnoreCase("40001")) {
                Toast.makeText(this, jsonObject.optString("error"), Toast.LENGTH_SHORT).show();
            } else {
                savePreferences();
                getRoomTokenWebCall();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void onGetTokenSuccess(String response) {
        Log.e("responseToken", response);

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString("result").equalsIgnoreCase("0")) {
                token = jsonObject.optString("token");
                Log.e("token", token);
                Intent intent = new Intent(ChatActivity.this, VideoConferenceActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("name", messageReceiverName);
                startActivity(intent);
            } else {
                Toast.makeText(this, jsonObject.optString("error"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onGetRoomIdSuccess(String response) {
        Log.e("responseDashboard", response);

        try {
            JSONObject jsonObject = new JSONObject(response);
            room_Id = jsonObject.optJSONObject("room").optString("room_id");
            Log.d("roomId", "onGetRoomIdSuccess: " + jsonObject);
        } catch (JSONException e) {

            e.printStackTrace();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //if (!sharedPreferences.getString("room_id", "").isEmpty()) {
                room_Id=sharedPreferences.getString("room_id", "");

            }
        });
    }

    @Override
    public void onWebResponseError(String error, int callCode) {
        Log.e("errorDashboard", error);
    }

    private void setSharedPreference() {
        if (sharedPreferences != null) {
            if (!sharedPreferences.getString("name", "").isEmpty()) {
                messageReceiverName=sharedPreferences.getString("name", "");
            }
            if (!sharedPreferences.getString("room_id", "").isEmpty()) {
                room_Id=sharedPreferences.getString("room_id", "");
            }
        }
    }





    private JSONObject jsonObjectToSend() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", "Test Dev Room");
            jsonObject.put("settings", getSettingsObject());
            jsonObject.put("data", getDataObject());
            jsonObject.put("sip", getSIPObject());
            jsonObject.put("owner_ref", "fadaADADAAee");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject getSIPObject() {
        JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }

    private JSONObject getDataObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", messageReceiverName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject getSettingsObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("description", "Testing");
            jsonObject.put("scheduled", false);
            jsonObject.put("scheduled_time", "");
            jsonObject.put("duration", 50);
            jsonObject.put("participants", 10);
            jsonObject.put("billing_code", 1234);
            jsonObject.put("auto_recording", false);
            jsonObject.put("active_talker", true);
            jsonObject.put("quality", "HD");
            jsonObject.put("wait_moderator", false);
            jsonObject.put("adhoc", false);
            jsonObject.put("mode", "group");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void getRoomTokenWebCall() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", messageReceiverName);
            jsonObject.put("role", "participant");
            jsonObject.put("user_ref", "2236");
            jsonObject.put("roomId", room_Id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new WebCall(this, this, jsonObject, WebConstants.getTokenURL, WebConstants.getTokenURLCode, false).execute();

    }


    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", messageReceiverName);
        editor.putString("room_id", room_Id);
        editor.commit();

    }
}