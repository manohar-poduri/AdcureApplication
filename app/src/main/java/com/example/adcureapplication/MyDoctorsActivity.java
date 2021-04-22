package com.example.adcureapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MyDoctorsActivity extends AppCompatActivity {
    private DatabaseReference productRef,spe;
    private RecyclerView recyclerView;String currentUserid;private FirebaseAuth mAuth;
    private TextView tv;  private ProgressDialog dialog;  private DatabaseReference rootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_doctors);
        recyclerView=(RecyclerView)findViewById(R.id.r);
        recyclerView.setHasFixedSize(true);
        rootRef= FirebaseDatabase.getInstance().getReference();//app/use
tv=(TextView)findViewById(R.id.tvs1);
        dialog=new ProgressDialog(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final Intent in=getIntent();
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        if (firebaseUser  != null){
            currentUserid=mAuth.getCurrentUser().getUid();
        }            currentUserid=mAuth.getCurrentUser().getUid();

        productRef = FirebaseDatabase.getInstance().getReference().child("Users").
                child(currentUserid).child("Appointments");

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dsnapshot) {
                if (dsnapshot.exists()) {

                    FirebaseRecyclerOptions<Appointment> options =
                            new FirebaseRecyclerOptions.Builder<Appointment>()
                                    .setQuery(productRef
                                            ,Appointment.class).build();
                    FirebaseRecyclerAdapter<Appointment, MyDoctorView> adapter =
                            new FirebaseRecyclerAdapter<Appointment, MyDoctorView>(options) {

                                @Override
                                protected void onBindViewHolder(@NonNull final MyDoctorView holder, int position, @NonNull final Appointment model) {
                                    holder.nme.setText(model.getDoctor_Name());
                                    holder.spe.setText( model.getSpecialist_Type());
                                    holder.amnt.setText( model.getAmount());
                                    holder.dte.setText(model.getDate());

holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(MyDoctorsActivity.this);
        View v = LayoutInflater.from(MyDoctorsActivity.this).inflate(R.layout.feedback,null,false);
        final EditText cmnt = v.findViewById(R.id.cmnt);
        builder.setView(v);
        builder.setCancelable(false);
        builder.setPositiveButton("feedback", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String cmn = cmnt.getText().toString();
                if (TextUtils.isEmpty(cmn)){
                  cmnt.setError("Write your comment");
                }else{
                    Calendar calendar=Calendar.getInstance();
                    SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,YYYY");
                  String  saveCurrentdate=currentDate.format(calendar.getTime());
                    SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
                 String   saveCurentTime=currentTime.format(calendar.getTime());
                 String   userRandomKey=saveCurrentdate+saveCurentTime;
                    mAuth= FirebaseAuth.getInstance();

                    rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!(snapshot.child("Users").child(currentUserid).child("Appointments").child(userRandomKey).exists())){
                                final HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("Comment", cmn);
                                hashMap.put("Doctor_Name",holder.nme.getText().toString());
                                hashMap.put("Doctor_Number",model.getDoctor_Number());

                                rootRef.child("Users").child(currentUserid).child("Feedbacks").child(userRandomKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){

                                            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(!(snapshot.child("All Doctors").child(model.getDoctor_Number()).child("My Feedbacks").child(userRandomKey).exists())){
                                                        final HashMap<String, Object> hashMa = new HashMap<>();
                                                        hashMa.put("comment", cmn);
                                                        hashMa.put("user_id",model.getUser_id());
                                                        hashMa.put("user_name",model.getName());
                                                        rootRef.child("All Doctors").child(model.getDoctor_Number()).child("My Feedbacks").child(userRandomKey).updateChildren(hashMa).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                ////Toast.makeText(DetailsOfUser.this, "Success done", Toast.LENGTH_SHORT).show();
                                                                cmnt.setText("");
                                                                dialogInterface.dismiss();

                                                            }
                                                        }) ;
                                                    } else {
                                                        Toast.makeText(MyDoctorsActivity.this, "Success done", Toast.LENGTH_SHORT).show();
                                                        cmnt.setText("");
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
});
       }
                                @NonNull
                                @Override
                                public MyDoctorView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_doctors, parent, false);
                                    MyDoctorView viewHolder = new MyDoctorView(view);
                                    return viewHolder;
                                }
                            };
                    recyclerView.setAdapter(adapter);
                    adapter.startListening();
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }else {
                    dialog.dismiss();
                    tv.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });dialog.show();
    }
}