package com.example.admin.barcodescanneractivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DynamicParts extends AppCompatActivity {

    Spinner selectParts;
    Button codename;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> array = new ArrayList<String>();
//    String parts[]={};
    String[] stringArray={};
    String selected_parts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_parts);

        init();
        ProgressDialog dialog = ProgressDialog.show(DynamicParts.this, "",
                "Loading. Please wait...", true);
        dialog.show();
        //selectParts();
        spinner_parts();
        codename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_parts = selectParts.getSelectedItem().toString();
                db.collection("chakan")
                        .document("parts")
                        .collection("all parts")
                        .whereEqualTo("name",selected_parts)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                                for(DocumentSnapshot document: queryDocumentSnapshots.getDocuments()){
                                    Toast.makeText(DynamicParts.this, document.getData().get("code").toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

    }

    void init(){
        selectParts = findViewById(R.id.parts_spinner2);
        codename = findViewById(R.id.btn_show);
    }

    private void selectParts() {



    }

    void spinner_parts(){


//        ProgressDialog dialog = ProgressDialog.show(DynamicParts.this, "",
//                "Loading. Please wait...", true);
//        dialog.show();
        array.add("--Select parts--");

        db.collection("chakan")
                .document("parts")
                .collection("all parts")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot document: queryDocumentSnapshots.getDocuments()){
                            Log.e("type", String.valueOf(document.getData().get("name")));
                            array.add(String.valueOf(document.getData().get("name")));
                        }
                        Log.e("type",array.toString());
//                        parts = array.toArray(array);
                        stringArray = array.toArray(new String[0]);
                        Log.e("converted shit",stringArray.toString());
                        ArrayAdapter adapter_parts = new ArrayAdapter(DynamicParts.this,android.R.layout.simple_spinner_item,stringArray);
                        adapter_parts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        selectParts.setAdapter(adapter_parts);
                    }

                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e!=null){
                    Toast.makeText(DynamicParts.this, "Cant fetch parts", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        dialog.dismiss();

    }
}