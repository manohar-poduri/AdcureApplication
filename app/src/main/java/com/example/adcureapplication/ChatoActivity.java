package com.example.adcureapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ChatoActivity extends AppCompatActivity {
    String nme;
    private TextView nmeDctr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chato);
        Intent intent = getIntent();
         nme=intent.getStringExtra("name");
         nmeDctr=(TextView)findViewById(R.id.name_of_dctr);
         nmeDctr.setText(nme);

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        SharedPreferences sh=getSharedPreferences("my",MODE_PRIVATE);
//        String sr=sh.getString("name","");
//     nmeDctr.setText(sr);
//        Toast.makeText(this, nme, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        SharedPreferences sharedPreferences=getSharedPreferences("my",MODE_PRIVATE);
//        SharedPreferences.Editor myEdit=sharedPreferences.edit();
//        myEdit.putString("name",nme);
//        myEdit.apply();
//    }
}