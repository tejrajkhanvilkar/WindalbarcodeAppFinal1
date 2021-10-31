package com.example.admin.barcodescanneractivity.Admin;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.barcodescanneractivity.Admin.Adapter.view_data_adpater;
import com.example.admin.barcodescanneractivity.Admin.Datamodel.view_data_datamodel;
import com.example.admin.barcodescanneractivity.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewData extends AppCompatActivity {
    ImageButton datebutton;
    TextView Date;
    RecyclerView VIEWUSER_RECYCLERVIEW;
    String plants;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_data);
        init();
        datebutton.setOnClickListener( new BtnDatePickerDialogClickListener());
        VIEWUSER_RECYCLERVIEW.setHasFixedSize(true);
        VIEWUSER_RECYCLERVIEW.setLayoutManager(new LinearLayoutManager(getParent()));
//        Fetch_data();



    }

    void init(){
        datebutton= findViewById(R.id.datepickerdialog);
        Date = findViewById(R.id.selected_date);
        VIEWUSER_RECYCLERVIEW =findViewById(R.id.viewdata_recyclerview);
        @SuppressLint("WrongConstant") SharedPreferences sh = getSharedPreferences("PLANTADMIN",MODE_APPEND);
        plants = sh.getString("plant","");
    }

    private class BtnDatePickerDialogClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            DatePickerDialog dobPickerDialog =
                    new DatePickerDialog(
                            ViewData.this,
                            new DOBPickUpListener(),
                            2021,
                            9,
                            1
                    );
            dobPickerDialog.show();
        }

    }

     private class DOBPickUpListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            Date.setText(dayOfMonth +"-" + (month+1) + "-"+year);
            if (Date.getText().toString().isEmpty()){
                Toast.makeText(ViewData.this,"Please Select date to view data for ",Toast.LENGTH_LONG).show();
            }else {
                Fetch_data(Date.getText().toString());
            }

        }
    }

    public void Fetch_data(String date){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        //Todo: get plant name from SharedPrefs
        firebaseFirestore.collection(plants)
                .document("data")
                .collection("all data").whereEqualTo("date",date).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
             @Override
             public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                 ArrayList<view_data_datamodel> view_data_datamodelArrayList = new ArrayList<view_data_datamodel>();
                 if(queryDocumentSnapshots.isEmpty()){
                     view_data_adpater viewDataAdpater = new view_data_adpater(view_data_datamodelArrayList);
                     VIEWUSER_RECYCLERVIEW.setAdapter(viewDataAdpater);
                 }
                 for(DocumentSnapshot document: queryDocumentSnapshots.getDocuments()){
                     view_data_datamodel view_data_datamodel = document.toObject(com.example.admin.barcodescanneractivity.Admin.Datamodel.view_data_datamodel.class);
                     view_data_datamodelArrayList.add(view_data_datamodel);
                     view_data_adpater viewDataAdpater = new view_data_adpater(view_data_datamodelArrayList);
                     VIEWUSER_RECYCLERVIEW.setAdapter(viewDataAdpater);
                 }
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {

             }
         });


    }

}
