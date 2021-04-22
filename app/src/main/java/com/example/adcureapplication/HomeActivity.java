package com.example.adcureapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import videortc.ConnectActivity;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
  private FloatingActionButton fabtn;
    private AppBarConfiguration mAppBarConfiguration;
    private DatabaseReference productRef,likeref,useref;
    private RecyclerView recyclerView;
    private TextView nmebtn,dertxt,neptxt,gentxt,pedtxt,gyntxt,cartxt;
    private FirebaseUser currentUser; private FirebaseAuth mAuth;
    private ViewPager viewPager;
    private CollectionAdapter collectionAdapter;
    LinearLayout sliderDotspanel;
    private int dotscount;private DatabaseReference rootRef;
    private ImageView[] dots;
    private TextView editProfile;
Boolean Likechecker=false;
    private RequestQueue requestQueue;
    private String url="https://fcm.googleapis.com/fcm/send";
    private String type="";
    RecyclerView.LayoutManager layoutManager;
private Button allSym;
private LinearLayout gen,card,nep,ped,der,gyn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        rootRef= FirebaseDatabase.getInstance().getReference();
        final Intent intent=getIntent();
//        Bundle bundle=intent.getExtras();
//        if(bundle!=null){
//            type=getIntent().getExtras().get("Admin").toString();
//
//        }

        cartxt=(TextView)findViewById(R.id.card_txt);
        gentxt=(TextView)findViewById(R.id.gen_txt);
        pedtxt=(TextView)findViewById(R.id.ped_txt);
        neptxt=(TextView)findViewById(R.id.nep_txt);
        gyntxt=(TextView)findViewById(R.id.gyn_txt);
        dertxt=(TextView)findViewById(R.id.der_txt);

gen=(LinearLayout)findViewById(R.id.gen);
gen.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent1=new Intent(HomeActivity.this,ConsultNowActivity2.class);
        intent1.putExtra("name",gentxt.getText().toString());
        startActivity(intent1);
    }
});
        card=(LinearLayout)findViewById(R.id.car);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(HomeActivity.this,ConsultNowActivity2.class);
                intent1.putExtra("name",cartxt.getText().toString());
                startActivity(intent1);
            }
        });
        nep=(LinearLayout)findViewById(R.id.neph);
        nep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(HomeActivity.this,ConsultNowActivity2.class);
                intent1.putExtra("name",neptxt.getText().toString());
                startActivity(intent1);
            }
        });
        der=(LinearLayout)findViewById(R.id.der);
        der.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(HomeActivity.this,ConsultNowActivity2.class);
                intent1.putExtra("name",dertxt.getText().toString());
                startActivity(intent1);
            }
        });
        gyn=(LinearLayout)findViewById(R.id.gye);
        gyn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(HomeActivity.this,ConsultNowActivity2.class);
                intent1.putExtra("name",gyntxt.getText().toString());
                startActivity(intent1);
            }
        });
        ped=(LinearLayout)findViewById(R.id.ped);
        ped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(HomeActivity.this,ConsultNowActivity2.class);
                intent1.putExtra("name",pedtxt.getText().toString());
                startActivity(intent1);
            }
        });
        allSym=(Button)findViewById(R.id.all_speci);

        allSym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(HomeActivity.this,ConsultNowActivity.class);
                startActivity(intent1);
            }
        });
       // fabtn=(FloatingActionButton)findViewById(R.id.fab);
        nmebtn=(TextView)findViewById(R.id.name_btn);
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        CollectionAdapter collectionAdapter=new CollectionAdapter(this);
        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);

        viewPager.setAdapter(collectionAdapter);
        dotscount = collectionAdapter.getCount();
        dots = new ImageView[dotscount];

        for(int i = 0; i < dotscount; i++){

            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new Mytime(), 2000, 4000);



//        recyclerView=(RecyclerView)findViewById(R.id.recyclerVieww);
//        productRef= FirebaseDatabase.getInstance().getReference().child("Posts");
//        useref= FirebaseDatabase.getInstance().getReference().child("Users");
//
//        likeref= FirebaseDatabase.getInstance().getReference().child("Likes");
//        if(!type.equals("Admin")) {
//            nmebtn.setText("Hello " + Prevalent.currentUserData.getName());
//        }
         Paper.init(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
//        fabtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(!type.equals("Admin")){
//                startActivity(new Intent(HomeActivity.this,AddPostActivity.class));
//            }}
//        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(
                this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        navigationView.setNavigationItemSelectedListener(this);
        View headerView=navigationView.getHeaderView(0);
        TextView usernameText=headerView.findViewById(R.id.userProfileName);
        CircleImageView imageView=headerView.findViewById(R.id.profileImage);
        editProfile=(TextView)headerView.findViewById(R.id.EditProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,SettingsActivity.class));
            }
        });
//        if(!type.equals("Admin")){
//            usernameText.setText(Prevalent.currentUserData.getName());
//            Picasso.get().load(Prevalent.currentUserData.getImage()).placeholder(R.drawable.profile).into(imageView);
//        }


//        recyclerView.setHasFixedSize(true);
//        layoutManager=new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home2, menu);
        return true;
    }

  public void pharma(View view){
      Intent viewIntent =
              new Intent("android.intent.action.VIEW",
                      Uri.parse("http://adcure.co.in/adcure/order-medicines/"));
      startActivity(viewIntent);
  }
  public void patha(View view){
      Intent viewIntent =
              new Intent("android.intent.action.VIEW",
                      Uri.parse("http://adcure.co.in/adcure/order-medicines/category.php?cid=7"));
      startActivity(viewIntent);
  }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
         if(id==R.id.nav_search){

                Intent intent=new Intent(HomeActivity.this, ConsultNowActivity.class);
                intent.putExtra("Admin",type);
                startActivity(intent);



        }else if(id==R.id.nav_appoi){
//            if(!type.equals("Admin")){
                Intent intent=new Intent(HomeActivity.this, MyAppointments.class);
                intent.putExtra("Admin",type);
                startActivity(intent);
//
//
//            }
         } else if(id==R.id.n){
             startActivity(new Intent(HomeActivity.this, MyDoctorsActivity.class));
             }
         else if(id==R.id.path){
             Intent viewIntent =
                     new Intent("android.intent.action.VIEW",
                             Uri.parse("http://adcure.co.in/adcure/order-medicines/category.php?cid=7"));
             startActivity(viewIntent);         }
         else if(id==R.id.phar){
             Intent viewIntent =
                     new Intent("android.intent.action.VIEW",
                             Uri.parse("http://adcure.co.in/adcure/order-medicines/"));
             startActivity(viewIntent);         }
         else if(id==R.id.paymnts){
             startActivity(new Intent(HomeActivity.this, ToPaymentsActivity.class));
         }
        else if(id==R.id.nav_logout){


             Paper.book().destroy();
             mAuth=FirebaseAuth.getInstance();
             mAuth.signOut();
                Intent intent=new Intent(HomeActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
              startActivity(intent);
            finish();

        }
        DrawerLayout drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

//    @Override
//    protected void onStart() {
//
//        super.onStart();
//        mAuth= FirebaseAuth.getInstance();
//
//        currentUser=mAuth.getCurrentUser();
//        if(currentUser==null){
//          sendUserToLoginActivity();
//        } else{
//           verifyUserExistance();
//        }
//    }
    private void verifyUserExistance() {   mAuth= FirebaseAuth.getInstance();
        String useruid=mAuth.getCurrentUser().getUid();
        rootRef.child("Users").child(useruid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Toast.makeText(HomeActivity.this, "Welcome..", Toast.LENGTH_SHORT).show();
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

    private void sendUserToLoginActivity() {
        Intent intent=new Intent(HomeActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    public class Mytime extends TimerTask{
        @Override
        public void run() {
            HomeActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(viewPager.getCurrentItem()==0){
                        viewPager.setCurrentItem(1);
                    }else if(viewPager.getCurrentItem()==1){
                        viewPager.setCurrentItem(2);
//                    }else  if(viewPager.getCurrentItem()==2){
//                        viewPager.setCurrentItem(3);
                    }else {
                        viewPager.setCurrentItem(0);

                    }
                }
            });

        }
    }
}
