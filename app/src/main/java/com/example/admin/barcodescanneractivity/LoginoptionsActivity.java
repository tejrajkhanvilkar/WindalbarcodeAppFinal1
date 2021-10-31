package com.example.admin.barcodescanneractivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.admin.barcodescanneractivity.Admin.AdminLoginScreen;

public class LoginoptionsActivity extends AppCompatActivity {
    Button Login_User,Login_Admin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginoptions);
        init();
        requestpermission();
        Login_User.setOnClickListener(new Btnuseronclicklistner());
        Login_Admin.setOnClickListener(new Btnadminonclicklistner());

    }

    void init(){
      Login_User = findViewById(R.id.btn_LoginAsUser);
      Login_Admin = findViewById(R.id.btn_LoginAsAdmin);
    }

    private  class Btnuseronclicklistner implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(LoginoptionsActivity.this,Loginactivity.class);
            startActivity(intent);
        }
    }

    private  class Btnadminonclicklistner implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(LoginoptionsActivity.this, AdminLoginScreen.class);
            startActivity(intent);
        }
    }

    private void requestpermission(){
        ActivityCompat.requestPermissions(LoginoptionsActivity.this, new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);
    }
}


