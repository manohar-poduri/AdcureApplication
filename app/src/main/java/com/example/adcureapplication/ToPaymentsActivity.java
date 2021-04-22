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

import org.w3c.dom.Text;

public class ToPaymentsActivity extends AppCompatActivity {

    private DatabaseReference productRef,spe;
    private RecyclerView recyclerView;String currentUserid;private FirebaseAuth mAuth;
    private TextView tv;  private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_payments);
        recyclerView=(RecyclerView)findViewById(R.id.rv5);
        recyclerView.setHasFixedSize(true);

        dialog=new ProgressDialog(this);
  tv=(TextView)findViewById(R.id.tvs2);
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
                    FirebaseRecyclerAdapter<Appointment, PaymntsView> adapter =
                            new FirebaseRecyclerAdapter<Appointment, PaymntsView>(options) {

                                @Override
                                protected void onBindViewHolder(@NonNull final PaymntsView holder, int position, @NonNull final Appointment model) {
                                    holder.nme.setText(model.getDoctor_Name());
                                    holder.spe.setText( model.getSpecialist_Type());
                                    holder.amnt.setText( model.getAmount());


                                    holder.btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent=new Intent(getApplicationContext(), DispalyingPaymert.class);
                                            String nme= String.valueOf(holder.nme.getText());


                                            String time= model.getTime();

                                            String dte= model.getDate();
                                            String amnt= String.valueOf(holder.amnt.getText());
                                              String name=model.getName();
                                              String num=model.getNumber();
                                              String mail=model.getEmail_id();
                                              String spe=String.valueOf(holder.spe.getText());
                                         intent.putExtra("unme",name);
                                            intent.putExtra("name",nme);
                                            intent.putExtra("amount",amnt);
                                            intent.putExtra("time",time);
                                            intent.putExtra("date",dte);
                                            intent.putExtra("numb",num);
                                            intent.putExtra("mail",mail);

                                            intent.putExtra("spe",spe);
intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            dialog.dismiss();                                        ////    Toast.makeText(ConsultNowActivity2.this, ""+dctr_num, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @NonNull
                                @Override
                                public PaymntsView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_payments, parent, false);
                                    PaymntsView viewHolder = new PaymntsView(view);
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