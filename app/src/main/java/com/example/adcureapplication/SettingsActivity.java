package com.example.adcureapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private TextView cancelbtn,updatebtn,changeprofie;
    private EditText userNameChange,phonenumChange,addressChange;
    private CircleImageView changeImage;
    private String myUri="";    private String doctorRandomKey, DownloadUri;
    private ProgressDialog dialog;
    private StorageTask uploadTask;
    private Uri imageUri;private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private String checker="";
    private String currentUserid;    private Button securityQuesti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        storageReference= FirebaseStorage.getInstance().getReference().child("Profile Pictures");
        cancelbtn=(TextView)findViewById(R.id.close_settings_btn);
        updatebtn=(TextView)findViewById(R.id.update_settings_btn);
        changeprofie=(TextView) findViewById(R.id.profile_change_image);
        phonenumChange=(EditText)findViewById(R.id.change_phone_number);
        dialog=new ProgressDialog(this);

        userNameChange=(EditText)findViewById(R.id.change_full_name);
        addressChange=(EditText)findViewById(R.id.change_address);
        changeImage=(CircleImageView)findViewById(R.id.settings_profile_image);
        mAuth= FirebaseAuth.getInstance();
         FirebaseUser firebaseUser=mAuth.getCurrentUser(); //currentUserid=mAuth.getCurrentUser().getUid();
        if (firebaseUser  != null){
           // currentUserid=mAuth.getCurrentUser().getUid();
        }
     //   updateInfoProfile(userNameChange,phonenumChange,addressChange);
currentUserid=mAuth.getCurrentUser().getPhoneNumber();
        updateInfoDisplayProfile(changeImage,userNameChange,phonenumChange,addressChange);
    cancelbtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    });

    updatebtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(checker.equals("clicked")){
                userInfoSaved();
            }else{
                updateOnlyUserInfo();
            }
        }
    });
    changeprofie.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            checker="clicked";

            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(SettingsActivity.this);
        }
    });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null) {
        CropImage.ActivityResult result=CropImage.getActivityResult(data);
        imageUri=result.getUri();

        changeImage.setImageURI(imageUri);
    }else {
        Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
        finish();
    }
    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String ,Object>userMap=new HashMap<>();
        userMap.put("name",userNameChange.getText().toString());
        userMap.put("address",addressChange.getText().toString());
        userMap.put("phone",phonenumChange.getText().toString());
        ref.child(currentUserid).child("details").updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
        Toast.makeText(SettingsActivity.this,"PROFILE INFO UPDATED SUCCESSFULLY",Toast.LENGTH_SHORT).show();
        finish();
    }

    private void userInfoSaved() {
    if (TextUtils.isEmpty(userNameChange.getText().toString())){
        Toast.makeText(this,"Name is mandatory",Toast.LENGTH_SHORT).show();
    }else if (TextUtils.isEmpty(phonenumChange.getText().toString())){
        Toast.makeText(this,"Phone Number is mandatory",Toast.LENGTH_SHORT).show();
    } else if (TextUtils.isEmpty(addressChange.getText().toString())){
        Toast.makeText(this,"Type your address",Toast.LENGTH_SHORT).show();
    }else if(checker.equals("clicked")){

      uploadImage();

        }
    }
 private void StoreInfo() throws FileNotFoundException {
     DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users");
     dialog.setTitle("Update Profile");
     dialog.setMessage("Please wait,while we are updating your profile..");
     dialog.setCanceledOnTouchOutside(false);
     dialog.show();
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,YYYY");
//        saveCurrentdate=currentDate.format(calendar.getTime());
//        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
//        saveCurentTime=currentTime.format(calendar.getTime());
//        doctorRandomKey=saveCurrentdate+saveCurentTime;
        final StorageReference filePath=storageReference.child(currentUserid+".jpg");
     InputStream inputStream=new FileInputStream(new File(String.valueOf(imageUri)));
        final UploadTask uploadTask=filePath.putStream(inputStream);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String msg=e.toString();
                Toast.makeText(SettingsActivity.this,"Error:l",Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SettingsActivity.this,"Doctor Image Uploaded Successfully...",Toast.LENGTH_SHORT).show();
                Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw  task.getException();
                        }
                        DownloadUri=filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            DownloadUri=task.getResult().toString();
                            Toast.makeText(SettingsActivity.this,"got the Doctor Image saved to databse...",Toast.LENGTH_SHORT).show();
//                            saveProductInfoToDatabase();

                            final HashMap<String,Object> userMap=new HashMap<>();

                            userMap.put("name",userNameChange.getText().toString());
                            userMap.put("address",addressChange.getText().toString());
                            userMap.put("phoneOrder",phonenumChange.getText().toString());
                            userMap.put("image",myUri);

                        ref.child(currentUserid).child("details").updateChildren(userMap).
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
//
startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
                                            }
                                        } });
                        }
                    }
                });
            }
        }); {

        }
    }

  private void uploadImage() {
    final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait,while we are updating your profile..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri!=null){
            final StorageReference reference=storageReference.child(currentUserid+".jpg");
            uploadTask=reference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloaduri=task.getResult();
                        myUri=downloaduri.toString();
                        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String ,Object>userMap=new HashMap<>();
                        userMap.put("name",userNameChange.getText().toString());
                        userMap.put("address",addressChange.getText().toString());
                        userMap.put("phoneOrder",phonenumChange.getText().toString());
                        userMap.put("image",myUri);
                        ref.child(currentUserid).child("details").updateChildren(userMap);

                        progressDialog.dismiss();
                        startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
                        Toast.makeText(SettingsActivity.this,"PROFILE INFO UPDATED SUCCESSFULLY",Toast.LENGTH_SHORT).show();
                    finish();
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(  SettingsActivity.this,"Errortr",Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }else {
            Toast.makeText( this,"Image is not selected",Toast.LENGTH_SHORT).show();

        }

    }
//    private void updateInfoProfile( final EditText userNameChange, final EditText phonenumChange, final EditText addressChange) {
//        DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid).child("details");
//        userRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()){
//                    if (snapshot.child("name").exists()){
//                        String name=snapshot.child("name").getValue().toString();
//                        String phone=snapshot.child("phone").getValue().toString();
//
//                        String address=snapshot.child("address").getValue().toString();
//
//
//                        userNameChange.setText(name);
//                        phonenumChange.setText(phone);
//                        addressChange.setText(address);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void updateInfoDisplayProfile(final CircleImageView changeImage, final EditText userNameChange, final EditText phonenumChange, final EditText addressChange) {
        DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid).child("details");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("image").exists()){
                        String image=snapshot.child("image").getValue().toString();
                        String name=snapshot.child("name").getValue().toString();
                        String phone=snapshot.child("phone").getValue().toString();

                        String address=snapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(changeImage);
                        userNameChange.setText(name);
                        phonenumChange.setText(phone);
                        addressChange.setText(address);
                    }else {
                    String name=snapshot.child("name").getValue().toString();
                    String phone=snapshot.child("phone").getValue().toString();
                        String address=snapshot.child("address").getValue().toString();

                        userNameChange.setText(name);
                    phonenumChange.setText(phone);
                    addressChange.setText(address);}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}