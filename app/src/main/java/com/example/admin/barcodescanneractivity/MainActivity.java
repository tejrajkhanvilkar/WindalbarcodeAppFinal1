package com.example.admin.barcodescanneractivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.barcodescanneractivity.Admin.Adminbottomnavigation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    public static TextView resulttextview;
    Button scanbutton, buttontoast;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentuser = firebaseAuth.getCurrentUser();
    SharedPreferences sh;
    SharedPreferences.Editor myEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth != null){
            currentuser = firebaseAuth.getCurrentUser();
        }
        ActionBar actionBar= getSupportActionBar();
        actionBar.hide();

        sh = getSharedPreferences("LoginSharedPref", MODE_PRIVATE);
        myEdit = sh.edit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user == null) {
                    Intent intent = new Intent(MainActivity.this, LoginoptionsActivity.class);
                    startActivity(intent);
                    finish();
                }


//                else if ( user != null ) {
//                    String uid = user.getUid();
//                    if (uid.matches("2ce1HTmrUzemVG7uEDV1gxQDMgG2")) {
//                        Intent Homeintent = new Intent(MainActivity.this, AdminMainActivity.class);
//                        Homeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(Homeintent);
//
//                        finish();
//
//                    }
                   else {
                       if(sh.getString("userType","none").matches("user")){
                           Intent Homeintent = new Intent(MainActivity.this, vehicleinformation.class);
                           Homeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                           startActivity(Homeintent);
                           finish();
                       }else if(sh.getString("userType","none").matches("admin")){
                           Intent Homeintent = new Intent(MainActivity.this, Adminbottomnavigation.class);
                           Homeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                           startActivity(Homeintent);
                           finish();
                       }
                       else if(sh.getString("userType","none").matches("none")){
                           Intent Homeintent = new Intent(MainActivity.this, LoginoptionsActivity.class);
                           Homeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                           startActivity(Homeintent);
                           finish();
                       }


                    }


                }


        },2000);


    }
}
