package com.example.admin.barcodescanneractivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class sp2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp2);

        SharedPreferences sharedPreferences = getSharedPreferences("guru",MODE_PRIVATE);

        int a = sharedPreferences.getInt("start_count",0);
        int b = a+1;
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putInt("start_count",b);
        myEdit.apply();
        int c = sharedPreferences.getInt("start_count",0);
        Toast.makeText(sp2.this, "start value: "+String.valueOf(a)+"\n"+"final value: "+String.valueOf(c), Toast.LENGTH_LONG).show();

        finish();
        startActivity(getIntent());

    }
}