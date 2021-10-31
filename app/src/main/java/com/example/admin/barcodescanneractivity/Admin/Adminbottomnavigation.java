package com.example.admin.barcodescanneractivity.Admin;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.admin.barcodescanneractivity.R;
import com.example.admin.barcodescanneractivity.RegistrationActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Adminbottomnavigation extends AppCompatActivity {
    FloatingActionButton adduser;
    BottomNavigationView bottomNavigationView;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminbottomnavigation);

        init();
        adduser.setOnClickListener(new adduserclickonlistener());
        bottomNavigationView.setOnNavigationItemSelectedListener(selectedListener);
        actionBar.setTitle("HOME");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000077")));


        AdminHomeScreen fragment = new AdminHomeScreen();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "");
        fragmentTransaction.commit();

    }
    void init(){
        adduser = findViewById(R.id.add_user);
        bottomNavigationView = findViewById(R.id.navigation);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Home Activity");
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.nav_home:
                    adduser.setVisibility(View.VISIBLE);
                    actionBar.setTitle("Home");
                    AdminHomeScreen fragment = new AdminHomeScreen();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content, fragment, "");
                    fragmentTransaction.commit();
                    return true;

                    case R.id.nav_settings:
                    adduser.setVisibility(View.INVISIBLE);
                    actionBar.setTitle("Users");
                   Adminprofilescreen fragment2 = new Adminprofilescreen();
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.content, fragment2, "");
                    fragmentTransaction2.commit();
                    return true;


            }
            return false;



        }


    };


    private class adduserclickonlistener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(Adminbottomnavigation.this, RegistrationActivity.class);
            startActivity(intent);
        }
    }



}
