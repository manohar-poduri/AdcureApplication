package com.example.adcureapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DetailsOfUser extends AppCompatActivity implements PaymentResultListener {

    private static final int DATE_DIALOG_ID = 1;
    private int Year,Month,day;
    String currentUserid; private String  saveCurrentdate,saveCurentTime,userRandomKey;
    private Button payBtn,dte,tim;
    private EditText uName,uNum,uMail; private String value;
    private RadioButton mle,fmle;
    private DatabaseReference rootRef;boolean cli=false;
    private TextView specCon,dctrCon,addrCon,amntCon;
    String spe,dctr,addr,amnt,nme,dnum;
    private FirebaseAuth mAuth;
    private RadioGroup radioGroup; private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_of_user);
        rootRef= FirebaseDatabase.getInstance().getReference();//app/use
        mAuth= FirebaseAuth.getInstance();
        radioGroup=(RadioGroup)findViewById(R.id.rg) ;
        specCon=(TextView)findViewById(R.id.con_spec);
        dctrCon=(TextView)findViewById(R.id.con_dctr);
        addrCon=(TextView)findViewById(R.id.con_addr);
        amntCon=(TextView)findViewById(R.id.con_amnt);
        uMail=(EditText)findViewById(R.id.mail_u);
        uNum=(EditText)findViewById(R.id.num_u);
           loadingbar=new ProgressDialog(this);
        uName=(EditText)findViewById(R.id.u_name);
         mle=(RadioButton)findViewById(R.id.male_u);
         fmle=(RadioButton)findViewById(R.id.fem_u);
         payBtn=(Button)findViewById(R.id.btn_pay);
        final Intent in=getIntent();
        spe=in.getStringExtra("Dspe").toString();
        dctr=in.getStringExtra("Dname").toString();
        addr=in.getStringExtra("Dadr").toString();
        amnt=in.getStringExtra("Damnt").toString();
        dnum=in.getStringExtra("Dnum").toString();
        dte = (Button) findViewById(R.id.bt_setdate);
        tim= (Button) findViewById(R.id.bt_setime);
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        if (firebaseUser  != null){
            currentUserid=mAuth.getCurrentUser().getUid();
        }
        specCon.setText(spe);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i){
                    case R.id.male_u:
                        value="Male";
                        break;
                    case R.id.fem_u:
                        value="Female";
                        break;

                }
                Toast.makeText(getApplicationContext(), "Gender "+value, Toast.LENGTH_SHORT).show();
            }
        });
        nme=uName.getText().toString();
dctrCon.setText("by "+dctr);
addrCon.setText("at "+addr);
amntCon.setText(amnt);

        Checkout.preload(getApplicationContext());
payBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

VerifyDetails();
    }
});
        Button bt_setdate=(Button) findViewById(R.id.bt_setdate);
        bt_setdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        final Button eReminderTime=(Button) findViewById(R.id.bt_setime);

        eReminderTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(DetailsOfUser.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        eReminderTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time

                mTimePicker.show();

            }
        });
    }
    public void startPayment() {
        /**
         * You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        String nme=uName.getText().toString();
        String mail=uMail.getText().toString();
        String date=dte.getText().toString();
        String time=tim.getText().toString();
        String num=uNum.getText().toString();
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", nme);
            options.put("description", "Demoing Charges");
            //You can omit the image option to fetch the image from dashboard
         //   options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png");
            options.put("currency", "INR");



            double total = Double.parseDouble(amnt);
            total = total * 100;
            options.put("amount", total);

            JSONObject preFill = new JSONObject();
            preFill.put("email", mail);
            preFill.put("contact", num);

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
         storeDetails();
        Toast.makeText(this, "Payment successfully done! " + razorpayPaymentID, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment error please try again", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("OnPaymentError", "Exception in onPaymentError", e);
        }
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                //Edit------------------------------------------//
                final Calendar c = Calendar.getInstance();
                Year  = c.get(Calendar.YEAR);
                Month = c.get(Calendar.MONTH);
                day   = c.get(Calendar.DAY_OF_MONTH);
                //Edit------------------------------------------//
                return new DatePickerDialog(this, datePickerListener,
                        Year, Month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            Year = selectedYear;
            Month = selectedMonth;
            day = selectedDay;

            final Calendar c = Calendar.getInstance();

            String bookingdate = String.valueOf(Year) + "-" + String.valueOf(Month+1) + "-" + String.valueOf(day);

            Button bt_setdate=(Button) findViewById(R.id.bt_setdate);
            bt_setdate.setText(bookingdate);

        }
    };
    private void VerifyDetails(){
      String nme=uName.getText().toString();
      String mail=uMail.getText().toString();
        String date=dte.getText().toString();
        String time=tim.getText().toString();
      String num=uNum.getText().toString();
       // Toast.makeText(this, date, Toast.LENGTH_SHORT).show();
        String male=mle.getText().toString();
        String female=fmle.getText().toString();
     if(TextUtils.isEmpty(nme) && TextUtils.isEmpty(mail) &&  TextUtils.isEmpty(num)){
         Toast.makeText(this, "Please,Fill the form..", Toast.LENGTH_SHORT).show();
         uName.setError("Name Can't be empty");
         uMail.setError("Mail Can't be empty");
         uNum.setError("Number Can't be empty");

        }  else if (date.contains("Select Date")){
         Toast.makeText(this, "Choose date", Toast.LENGTH_SHORT).show();
     } else if (time.equals("Select time")){
         Toast.makeText(this, "Choose Time", Toast.LENGTH_SHORT).show();
     }else if(TextUtils.isEmpty(nme)){
      uName.setError("Name Can't be empty");
      }else  if(TextUtils.isEmpty(mail)){
          uMail.setError("Mail Can't be empty");
      }else  if(TextUtils.isEmpty(num)){
          uNum.setError("Number Can't be empty");
      }
    else if(radioGroup.getCheckedRadioButtonId()==-1){

         Toast.makeText(this, "Select Gender.", Toast.LENGTH_SHORT).show();     }

     else{

      startPayment();

     }

    }

    private void storeDetails() {

       final String nme=uName.getText().toString();
        final String mail=uMail.getText().toString();
        final  String date=dte.getText().toString();
        final String time=tim.getText().toString();
        final String num=uNum.getText().toString();
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,YYYY");
        saveCurrentdate=currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurentTime=currentTime.format(calendar.getTime());
        userRandomKey=saveCurrentdate+saveCurentTime;
        mAuth= FirebaseAuth.getInstance();

rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        if(!(snapshot.child("Users").child(currentUserid).child("Appointments").child(userRandomKey).exists())){
            final HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("name", nme);
            hashMap.put("email_id", mail);
            hashMap.put("gender",value );
            hashMap.put("date", date);
            hashMap.put("time", time);
            hashMap.put("number", num);
            hashMap.put("user_id", currentUserid);
            hashMap.put("doctor_Number", dnum);
            hashMap.put("specialist_Type",spe );
            hashMap.put("doctor_Name", dctr);
            hashMap.put("hospital_Address", addr);hashMap.put("amount", amnt);
            rootRef.child("Users").child(currentUserid).child("Appointments").child(userRandomKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!(snapshot.child("All Doctors").child(dnum).child("My Appointments").child(userRandomKey).exists())){
                                    rootRef.child("All Doctors").child(dnum).child("My Appointments").child(userRandomKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            ////Toast.makeText(DetailsOfUser.this, "Success done", Toast.LENGTH_SHORT).show();
                                            Intent intent=new Intent(DetailsOfUser.this, ChatActivity.class);intent.putExtra("numb",dnum);
                                            intent.putExtra("name",dctr);
                                            intent.putExtra("unme",nme);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }) ;
                                } else {
                                    Toast.makeText(DetailsOfUser.this, "Success done", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(DetailsOfUser.this, ChatActivity.class);
                                    intent.putExtra("numb",dnum);
                                    intent.putExtra("name",dctr);
                                    startActivity(intent);
                                    finish();
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
}