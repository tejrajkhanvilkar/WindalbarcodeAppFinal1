package com.example.admin.barcodescanneractivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import java.security.Permissions;

public class compnentsinformation extends AppCompatActivity {

    public static String receivedpartnumber;
    public static String selectedPartName;
    AlertDialog.Builder builder;
    Button oldwagon, newwagon,ssoold, ssonew, twentyeight_hpframe,thritysix_hpframe,threed_frame,utul_frame;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
           setContentView(R.layout.components_information);


           receivedpartnumber = " ";
        selectedPartName = "";


        builder = new AlertDialog.Builder(this);

//        oldwagon = findViewById(R.id.old_wagon);
//        newwagon = findViewById(R.id.newWagon);
//        ssoold = findViewById(R.id.sso_old);
//        ssonew = findViewById(R.id.sso_new);
//        twentyeight_hpframe = findViewById(R.id._28_Hp_frame);
//        thritysix_hpframe = findViewById(R.id._36_Hp_frame);
//        threed_frame = findViewById(R.id._3d_frame);
//        utul_frame = findViewById(R.id.utul_frame);
//
//        oldwagon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
//                selectedPartName = "SJ10081";
//            }
//        });
//
//        newwagon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
//                selectedPartName = "SJ25659P21";
//            }
//        });
//
//        ssoold.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
//                selectedPartName = "SJ30237";
//            }
//        });
//
//        ssonew.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
//                selectedPartName = "SJ30238";
//            }
//        });
//
//        twentyeight_hpframe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
//                selectedPartName = "SJ32344P21";
//            }
//        });
//
//        thritysix_hpframe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
//                selectedPartName = "SJ26990P21";
//            }
//        });
//
//        threed_frame.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
//                selectedPartName = "SJ26439P21";
//            }
//        });
//
//        utul_frame.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
//                selectedPartName = "SJ30398P21";
//            }
//        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        String a = receivedpartnumber;
        if(a==" ") {
            //Toast.makeText(compnentsinformation.this, "Empty", Toast.LENGTH_SHORT).show();
        }else {

            if (receivedpartnumber.equals(selectedPartName)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(compnentsinformation.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                final View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.greendialog, viewGroup, false);
                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


                dialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        alertDialog.dismiss();
                        onBackPressed();
                    }
                });
                alertDialog.show();

            } else {

                if (checkSmsPermission() == true) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(compnentsinformation.this);
                    ViewGroup viewGroup = findViewById(android.R.id.content);
                    final View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.reddialog, viewGroup, false);
                    builder.setView(dialogView);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    SmsManager mySmsManager = SmsManager.getDefault();
                    mySmsManager.sendTextMessage("7447297382", null, "Wrong Part", null, null);
                    Toast.makeText(this, "SMS sent.", Toast.LENGTH_LONG).show();

                    dialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            alertDialog.dismiss();
                            onBackPressed();
                        }
                    });
                    alertDialog.show();

                }else {

                }

            }
            a = " ";
            receivedpartnumber = " ";
            selectedPartName = "";
        }
    }
    private Boolean checkSmsPermission() {

        boolean result = ContextCompat.checkSelfPermission(compnentsinformation.this, Manifest.permission.SEND_SMS) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }


    private void requestpermission(){
        ActivityCompat.requestPermissions(compnentsinformation.this, new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);
    }
}
