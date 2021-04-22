package com.example.adcureapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ConsultNowActivity extends AppCompatActivity {
   ViewPager vp;
    private int dotscount;
    private DatabaseReference productRef,spe;
    private RecyclerView recyclerView;
    DatabaseReference temp;
    private ImageView bckarw;
    private TextView helpTxt;
    private ImageView[] dots;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consult_now);
      //  recyclerView=(RecyclerView)findViewById(R.id.recyclerVieww);
           //   recyclerView.setHasFixedSize(true);
 // recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vp=(ViewPager)findViewById(R.id.viewPager1);
        CollectionAdapter1 collectionAdapter1= new CollectionAdapter1(this);
        vp.setAdapter(collectionAdapter1);
        dotscount = collectionAdapter1.getCount();
        dots = new ImageView[dotscount];
bckarw=(ImageView)findViewById(R.id.arw_bck);
helpTxt=(TextView)findViewById(R.id.help_txt);
bckarw.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        onBackPressed();
        finish();
    }
});
helpTxt.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
toHelp();
    }
});

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new Mytimer(), 2000, 4000);

        productRef = FirebaseDatabase.getInstance().getReference().child("Specialists");

       final ArrayList<String> m=new ArrayList<>();

productRef.addChildEventListener(new ChildEventListener() {
    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        String sp=snapshot.getKey();
        m.add(sp);
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }

});

      RecyclerView recyclerVie=(RecyclerView)findViewById(R.id.recyclerVieww);
               recyclerVie.setHasFixedSize(true);
             recyclerVie.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

  MyADapter adapter=new MyADapter(m, getApplicationContext());

               recyclerVie.setAdapter(adapter);
    }
    public class Mytimer extends TimerTask {
        @Override
        public void run() {
            ConsultNowActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(vp.getCurrentItem()==0){
                        vp.setCurrentItem(1);
                    }else if(vp.getCurrentItem()==1){
                        vp.setCurrentItem(2);
//                    }else  if(viewPager.getCurrentItem()==2){
//                        viewPager.setCurrentItem(3);
                    }else {
                        vp.setCurrentItem(0);

                    }
                }
            });

        }
    }
    private void toHelp() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(ConsultNowActivity.this);
        View v = LayoutInflater.from(ConsultNowActivity.this).inflate(R.layout.help_lay,null,false);
        SearchableSpinner spinner=(SearchableSpinner)v.findViewById(R.id.sp1);
        final String[] itms2 = new String[]{"Doctors","Appointments","Consultation","Payments","Others"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itms2);
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
                    String saveCurrentDate = currentDate.format(calendar.getTime());
                    SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
                    String saveCurrentTime=currentTime.format(calendar.getTime());
                    String   userRandomKey=saveCurrentDate+saveCurrentTime;
                   FirebaseAuth mAuth= FirebaseAuth.getInstance();

                    FirebaseUser firebaseUser=mAuth.getCurrentUser();
                    String    currentUserid=mAuth.getCurrentUser().getUid();
DatabaseReference RootRef;
RootRef=FirebaseDatabase.getInstance().getReference();
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
                                                                    Toast.makeText(ConsultNowActivity.this, "The team will contact you soon...", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(ConsultNowActivity.this, "can not send..", Toast.LENGTH_SHORT).show();
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

}
