package com.example.adcureapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    private DatabaseReference rootRef; private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);  rootRef= FirebaseDatabase.getInstance().getReference();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAuth= FirebaseAuth.getInstance();
                FirebaseUser firebaseUser=mAuth.getCurrentUser();
                if (firebaseUser  != null){
                    String   currentUserid=mAuth.getCurrentUser().getUid();
                }
                currentUser=mAuth.getCurrentUser();
                if(firebaseUser!=null){
                    verifyUserExistance();
                }else
                {
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                }
            }
        },3000);
    }

    private void verifyUserExistance() {   mAuth= FirebaseAuth.getInstance();
        mAuth= FirebaseAuth.getInstance();

        String useruid=mAuth.getCurrentUser().getPhoneNumber();
        rootRef.child("All Doctors").child(useruid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    sendUserToMainActivity();
                    Toast.makeText(SplashActivity.this, "Welcome..", Toast.LENGTH_SHORT).show();
                }
                else{
                    sendUserToLoginActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void sendUserToMainActivity() {
        Intent mintent=new Intent(SplashActivity.this,HomeActivity.class);
        mintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mintent);
        finish();
    }

    private void sendUserToLoginActivity() {
        Intent intent=new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}