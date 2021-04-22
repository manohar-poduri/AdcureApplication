package com.example.adcureapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private FirebaseUser currentUser; private FirebaseAuth mAuth;
    private EditText emaillogin,pswrdLogin;
    private TextView forgetPswrd,needAccount,otpLogin;
    private Button loginBtn,loginPhone;
      private DatabaseReference rootRef;
    String number;

    private  com.rey.material.widget.CheckBox checkBox;

    private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootRef= FirebaseDatabase.getInstance().getReference();
        needAccount=(TextView)findViewById(R.id.need_new_acnt);
        emaillogin=(EditText)findViewById(R.id.login_email);
        pswrdLogin=(EditText)findViewById(R.id.login_pswrd);
        loginBtn=(Button)findViewById(R.id.login_btn);
        otpLogin=(TextView)findViewById(R.id.to_otp);

        otpLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,OtpActivity.class));
            }
        });
  mAuth=FirebaseAuth.getInstance();
//        currentUser=mAuth.getCurrentUser();

        //   checkBox=( com.rey.material.widget.CheckBox)findViewById(R.id.remembe_me_check);
        Paper.init(this);
        number=getIntent().getStringExtra("number");
        loadingbar=new ProgressDialog(this);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alloUserToLogin();
            }
        });


         needAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToRegisterActivity();
            }
        });

    }





    private void sendUserToLoginActivity() {
        Intent intent=new Intent(MainActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    private void alloUserToLogin() {
        final String email=emaillogin.getText().toString();
        final String pswrd=pswrdLogin.getText().toString();
        number=getIntent().getStringExtra("number");

        if(TextUtils.isEmpty(email) && TextUtils.isEmpty(pswrd)){
             emaillogin.setError("Please write your MailId");
            pswrdLogin.setError("Please write your Password");

        }else if(TextUtils.isEmpty(email)){
             emaillogin.setError("Please write your MailId");
        } else if(TextUtils.isEmpty(pswrd)){
            pswrdLogin.setError("Please write your Password");
         } else if(!(email.contains("gmail.com"))) {
          emaillogin.setError("Enter your Correct Email address");

        } else
        {
            loadingbar.setTitle("Sign in");
            loadingbar.setMessage("Please wait...");
            loadingbar.setCanceledOnTouchOutside(true);
//            if(checkBox.isChecked()){
//                Paper.book().write(Prevalent.usersPhoneKey,email);
//                Paper.book().write(Prevalent.usersPasswordKey,pswrd);
//            }
            mAuth.signInWithEmailAndPassword(email,pswrd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                if(mAuth.getCurrentUser().isEmailVerified()){
                                    final String currentUserid=mAuth.getCurrentUser().getUid();
                             rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                                     if (!(snapshot.child("Users").child(currentUserid).exists())) {
                                         HashMap<String, Object> hashMap = new HashMap<>();
                                         hashMap.put("name", email);

                                         rootRef.child("Users").child(currentUserid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                             @Override
                                             public void onComplete(@NonNull Task<Void> task) {
                                                 if(task.isSuccessful()){
                                                sendUserToMainActivity();
                                                     Intent mintent=new Intent(MainActivity.this,HomeActivity.class);
                                                     mintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                     startActivity(mintent);
                                                     finish();
                                                     Toast.makeText(MainActivity.this, "Loggedin successfully..", Toast.LENGTH_SHORT).show();
                                                     loadingbar.dismiss();
                                                 }else{
                                                     Toast.makeText(MainActivity.this, "check your internet connection", Toast.LENGTH_SHORT).show();
                                                     loadingbar.dismiss();
                                                 }
                                             }
                                         });
                                     }else if (task.isSuccessful()){
                                          sendUserToMainActivity();

                                         Toast.makeText(MainActivity.this, "Loggedin successfully..", Toast.LENGTH_SHORT).show();
                                         loadingbar.dismiss();
                                     }else{
                                         Toast.makeText(MainActivity.this, "check your internet connection", Toast.LENGTH_SHORT).show();
                                         loadingbar.dismiss();
                                     }
                                 }

                                 @Override
                                 public void onCancelled(@NonNull DatabaseError error) {

                                 }
                             });


                                }else{
                                    emaillogin.setError("Please verify your mail id");
                                    Toast.makeText(MainActivity.this, "Please verify your mail id", Toast.LENGTH_SHORT).show();
                                    loadingbar.dismiss();
                                }

                            }else{
                                try {
                                    throw task.getException();
                                }  catch (FirebaseNetworkException e){
                                    Toast.makeText(MainActivity.this, "Check your internet Connection", Toast.LENGTH_SHORT).show();
                                }catch (FirebaseAuthInvalidUserException e){
                                    emaillogin.setError("Not Found");
                                    Toast.makeText(MainActivity.this, "you have not registered with this mail address", Toast.LENGTH_SHORT).show();
                                    emaillogin.requestFocus();
                                }catch (FirebaseAuthInvalidCredentialsException e){
                                    Toast.makeText(MainActivity.this, "Enter your correct password", Toast.LENGTH_SHORT).show();
                                    pswrdLogin.setError("Wrong password");
                                    pswrdLogin.requestFocus();
                                }

                                catch(Exception e) {
                                    e.printStackTrace();
                                }
                                loadingbar.dismiss();
                            }
                        }
                    });loadingbar.show();
        }}



    private void sendUserToMainActivity() {
        Intent mintent=new Intent(MainActivity.this,HomeActivity.class);
        mintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mintent);
        finish();
    }

    private void sendUserToRegisterActivity() {

        Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
    public void reset(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.reset,null,false);
        final EditText mail = v.findViewById(R.id.remail);
        builder.setView(v);
        builder.setCancelable(false);
        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String umail = mail.getText().toString();
                if (TextUtils.isEmpty(umail)){
                    Toast.makeText(MainActivity.this, "mail cant be empty",
                            Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.sendPasswordResetEmail(umail).addOnCompleteListener(MainActivity.this,
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(MainActivity.this, "Mail sent",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this, "failed to send", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
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