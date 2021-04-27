package com.example.adcureapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyAppointments extends AppCompatActivity {
    private DatabaseReference productRef,spe;
    private RecyclerView recyclerView;
    String currentUserid;
    private FirebaseAuth mAuth;
    private TextView tw;  private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);
        recyclerView=(RecyclerView)findViewById(R.id.rv4);
        recyclerView.setHasFixedSize(true);
        tw=(TextView)findViewById(R.id.tvs);

        dialog=new ProgressDialog(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final Intent in=getIntent();
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        if (firebaseUser  != null){
            currentUserid=mAuth.getCurrentUser().getUid();
        }
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
                    FirebaseRecyclerAdapter<Appointment, AppView> adapter =
                            new FirebaseRecyclerAdapter<Appointment, AppView>(options) {

                                @Override
                                protected void onBindViewHolder(@NonNull final AppView holder, int position, @NonNull final Appointment model) {
                                    holder.nme.setText(model.getDoctor_Name());
                                    holder.spe.setText( model.getSpecialist_Type());
                                    holder.amnt.setText( model.getAmount());
                                    holder.dte.setText(model.getDate());
                                    holder.tme.setText(model.getTime());


                                    holder.btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent=new Intent(getApplicationContext(), ChatActivity.class);
                                            String nme= String.valueOf(holder.nme.getText());


                                            String time= String.valueOf(holder.tme.getText());

                                            String dte= String.valueOf(holder.dte.getText());
                                            String amnt= String.valueOf(holder.amnt.getText());
                                            String dctr_num=model.getDoctor_Number();
                             String name=model.getName();
                                            intent.putExtra("unme",name);
                                            intent.putExtra("name",nme);
                                             intent.putExtra("amount",amnt);
                                            intent.putExtra("time",time);
                                            intent.putExtra("date",dte);
                                            intent.putExtra("numb",dctr_num);
                                            startActivity(intent);
                                            dialog.dismiss();                                        ////    Toast.makeText(ConsultNowActivity2.this, ""+dctr_num, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @NonNull
                                @Override
                                public AppView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_appointments, parent, false);
                                    AppView viewHolder = new AppView(view);
                                    return viewHolder;
                                }
                            };
                    recyclerView.setAdapter(adapter);
                    adapter.startListening();
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }else{
                    dialog.dismiss();
                    tw.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });dialog.show();
    }
}