package com.example.adcureapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DispalyingPaymert extends AppCompatActivity {
private TextView spe,unme,amnt,dnme,tme,dte,num,mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispalying_paymert);
        spe=(TextView)findViewById(R.id.sptyp);
        unme=(TextView)findViewById(R.id.unme);
        amnt=(TextView)findViewById(R.id.amnt);
        dnme=(TextView)findViewById(R.id.dnme);
        tme=(TextView)findViewById(R.id.tme);
        dte=(TextView)findViewById(R.id.dte);
        num=(TextView)findViewById(R.id.unum);
        mail=(TextView)findViewById(R.id.umail);
        Intent intent=getIntent();
        String spec=intent.getStringExtra("spe");
        String unmee=intent.getStringExtra("unme");

        String amntt=intent.getStringExtra("amount");
        String dnmee=intent.getStringExtra("name");
        String tmee=intent.getStringExtra("time");
        String dtee=intent.getStringExtra("date");
        String nume=intent.getStringExtra("numb");
        String maile=intent.getStringExtra("mail");

unme.setText("Given Name : "+unmee);
amnt.setText("Paid Amount : "+amntt);
dnme.setText("Doctor Name : "+dnmee);
tme.setText("Payment Time : "+tmee);
dte.setText("Date of Payment : "+dtee);
num.setText("Given Number : "+nume);
mail.setText("Given MailId : "+maile);
spe.setText("Specialist Type : "+spec);
        //  String spe=String.valueOf(holder.spe.getText());
//        intent.putExtra("unme",name);
//        intent.putExtra("name",nme);
//        intent.putExtra("amount",amnt);
//        intent.putExtra("time",time);
//        intent.putExtra("date",dte);
//        intent.putExtra("numb",num);
//        intent.putExtra("mail",mail);
//
//        intent.putExtra("spe",spe);
    }
}