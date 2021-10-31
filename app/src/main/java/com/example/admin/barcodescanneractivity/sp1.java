package com.example.admin.barcodescanneractivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class sp1 extends AppCompatActivity {

    Button txt_continut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp1);
//
//        SharedPreferences sharedPreferences = getSharedPreferences("guru",MODE_PRIVATE);
//
//        SharedPreferences.Editor myEdit = sharedPreferences.edit();
//
//        myEdit.putInt("start_count",0 );
//        myEdit.apply();

        txt_continut = findViewById(R.id.txt_cotninue);
        txt_continut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("guru",MODE_PRIVATE);

                SharedPreferences.Editor myEdit = sharedPreferences.edit();

                myEdit.putInt("start_count",0 );
                myEdit.apply();

                Intent intent = new Intent(sp1.this, sp2.class);
                startActivity(intent);
            }
        });



    }
}