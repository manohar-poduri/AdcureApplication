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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ConsultNowActivity2 extends AppCompatActivity {
    private DatabaseReference productRef,spe;
    private RecyclerView recyclerView;
    private TextView tv,tw;  private ProgressDialog  dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consult_now2);
        recyclerView=(RecyclerView)findViewById(R.id.rv2);
        recyclerView.setHasFixedSize(true);
tw=(TextView)findViewById(R.id.tvs4);
dialog=new ProgressDialog(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final Intent in=getIntent();
        String iu=in.getStringExtra("name").toString();
        tv=(TextView)findViewById(R.id.spec_name);   tv.setText("Consultation with a "+iu);
     productRef = FirebaseDatabase.getInstance().getReference().child("Doctors").
                   child(iu);

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dsnapshot) {
                if (dsnapshot.exists()) {

                    FirebaseRecyclerOptions<DoctorDetails> options =
                            new FirebaseRecyclerOptions.Builder<DoctorDetails>()
                                    .setQuery(productRef
                                            ,DoctorDetails.class).build();
                    FirebaseRecyclerAdapter<DoctorDetails, DoctorViewHolder> adapter =
                            new FirebaseRecyclerAdapter<DoctorDetails, DoctorViewHolder>(options) {

                                @Override
                                protected void onBindViewHolder(@NonNull final DoctorViewHolder holder, int position, @NonNull final DoctorDetails model) {
                                    holder.doctorname.setText(model.getName());
                                    holder.doctorSpecialist.setText( model.getSpecialist());
                                    holder.doctorExp.setText( model.getExperience()+" years of experience");
                                    holder.dctrAmnt.setText( model.getPrice());
                                    holder.dctrQual.setText(model.getQualification());
                                    holder.dctrHsptl.setText("At "+model.getHospital_Name());
                                    holder.doctorAdre.setText(model.getHospital_Address());

                                    holder.btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent=new Intent(getApplicationContext(),DetailsOfUser.class);
                                            String nme= String.valueOf(holder.doctorname.getText());
                                            String spe= String.valueOf(holder.doctorSpecialist.getText());
                                            String adr= String.valueOf(holder.doctorAdre.getText());
                                            String hsptl= String.valueOf(holder.dctrHsptl.getText());
                                            String amnt= String.valueOf(holder.dctrAmnt.getText());
                                            String dctr_num=model.getNumber();

                                             intent.putExtra("Dname",nme);
                                             intent.putExtra("Dspe",spe);
                                            intent.putExtra("Dadr",adr);
                                            intent.putExtra("Damnt",amnt);
                                            intent.putExtra("Dhsptl",hsptl);
                                            intent.putExtra("Dnum",dctr_num);
                                            startActivity(intent);
                                            dialog.dismiss();                                        ////    Toast.makeText(ConsultNowActivity2.this, ""+dctr_num, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @NonNull
                                @Override
                                public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_doctors, parent, false);
                                    DoctorViewHolder viewHolder = new DoctorViewHolder(view);
                                    return viewHolder;
                                       }
                            };
                    recyclerView.setAdapter(adapter);
                    adapter.startListening();
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }else {
                    dialog.dismiss();
                    tw.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}