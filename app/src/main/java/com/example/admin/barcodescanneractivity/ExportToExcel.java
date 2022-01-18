package com.example.admin.barcodescanneractivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ExportToExcel extends AppCompatActivity {
    Spinner select_month, select_plant;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> array = new ArrayList<String>();
    String[] stringArray={};
    StringBuilder data;
    Button export;
    String selected_plant,selected_month;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_to_excel);

        getSupportActionBar().setTitle("Export to Excel");

        init();
        month_spinner();
        plant_spinner();

        progressDialog = new ProgressDialog(ExportToExcel.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                progressDialog.dismiss();
//            }
//        }, 3000);

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                export_to_excel();
            }
        });

    }

    void init(){
        select_month = findViewById(R.id.select_month);
        select_plant = findViewById(R.id.select_plant);
        export = findViewById(R.id.export);
    }

    void plant_spinner(){

        array.add("All");

        db.collection("plants")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot document: queryDocumentSnapshots.getDocuments()){
                            //Log.e("type", String.valueOf(document.getData().get("name")));
                            array.add(String.valueOf(document.getData().get("name")));
                        }
                        Log.e("type",array.toString());
//                        parts = array.toArray(array);
                        stringArray = array.toArray(new String[0]);
                        Log.e("converted shit",stringArray.toString());
                        ArrayAdapter adapter_parts = new ArrayAdapter(ExportToExcel.this,android.R.layout.simple_spinner_item,stringArray);
                        adapter_parts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        select_plant.setAdapter(adapter_parts);

                        progressDialog.dismiss();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e!=null){
                            Toast.makeText(ExportToExcel.this, "Cant fetch parts", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



//        ArrayAdapter adapter_parts = new ArrayAdapter(ExportToExcel.this,android.R.layout.simple_spinner_item,plantArray);
//        adapter_parts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        select_plant.setAdapter(adapter_parts);
    }

    void month_spinner(){

        String[] monthArray = {"All","January","February","March","April","May","June","July","August","September","October","November","December"};
        ArrayAdapter adapter_parts = new ArrayAdapter(ExportToExcel.this,android.R.layout.simple_spinner_item,monthArray);
        adapter_parts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select_month.setAdapter(adapter_parts);
    }

    public void abd(){
        Log.i("finallist",data.toString());
        try {
            FileOutputStream out = openFileOutput("windal_data.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();

            //Context context = getContext();
            File filelocation = new File(getFilesDir(), "windal_data.csv");
            Uri path = FileProvider.getUriForFile(ExportToExcel.this, "com.example.admin.barcodescanneractivity", filelocation);
            Intent fileintent = new Intent(Intent.ACTION_SEND);
            fileintent.setType("text/csv");
            fileintent.putExtra(Intent.EXTRA_SUBJECT, "Data");
            fileintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileintent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileintent, "Send mail"));


        } catch (Exception e) {
            e.printStackTrace();

        }

    }



    void export_to_excel() {
        selected_plant = select_plant.getSelectedItem().toString();
        selected_month = select_month.getSelectedItem().toString();

        data = new StringBuilder();
        data.append("Plant, Date, Vehicle Number, Invoice Number, Part Name, Part Code, Part Quantity, Correct Barcode, Wrong Barcode, Time, USER Email"+"\n");


        if(select_plant.getSelectedItem().toString().matches("All")){
            if(select_month.getSelectedItem().toString().matches("All")){
                fetchAllPlantAllmonths();
            }else{
                fetchAllPlantSelectedmonths();
            }
        }else{
            if(select_month.getSelectedItem().toString().matches("All")){
                db.collection(selected_plant)
                        .document("data")
                        .collection("all data")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                                for(DocumentSnapshot document: queryDocumentSnapshots.getDocuments()){
                                    Log.e("documet is: ", document.getData().toString());
                                    //data.append("\n"+ document.getData().get("plant")+","+document.getData().get("date")+","+document.getData().get("vehicle_number")+","+document.getData().get("invoice_number")+","+document.getData().get("part_name")+","+document.getData().get("part_code")+","+document.getData().get("part_quantity")+","+document.getData().get("correct_barcode")+","+document.getData().get("wrong_barcode"));
                                    data.append("\n"+ document.getData().get("plant")+","+document.getData().get("date")+","+document.getData().get("vehicle_number")+","+document.getData().get("invoice_number")+","+document.getData().get("part_name")+","+document.getData().get("part_code")+","+document.getData().get("part_quantity")+","+document.getData().get("correct_barcode")+","+document.getData().get("wrong_barcode")+","+document.getData().get("time")+","+document.getData().get("user_email"));
                                }
                                abd();
                            }
                        });

            }else{
                db.collection(selected_plant)
                        .document("data")
                        .collection("all data")
                        .whereEqualTo("month", selected_month)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                                for(DocumentSnapshot document: queryDocumentSnapshots.getDocuments()){
                                    Log.e("documet is: ", document.getData().toString());
                                    data.append("\n"+ document.getData().get("plant")+","+document.getData().get("date")+","+document.getData().get("vehicle_number")+","+document.getData().get("invoice_number")+","+document.getData().get("part_name")+","+document.getData().get("part_code")+","+document.getData().get("part_quantity")+","+document.getData().get("correct_barcode")+","+document.getData().get("wrong_barcode"));
                                }
                                abd();
                            }
                        });

            }



        }

//        data = new StringBuilder();
//        data.append("Plant, Date, Vehicle Number, Invoice Number, Part Name, Part Code, Part Quantity, Correct Barcode, Wrong Barcode"+"\n");
//
//        db.collection("chakan")
//                .document("data")
//                .collection("all data")
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
//                        for(DocumentSnapshot document: queryDocumentSnapshots.getDocuments()){
//                            Log.e("documet is: ", document.getData().toString());
//                            data.append("\n"+ document.getData().get("plant")+","+document.getData().get("date")+","+document.getData().get("vehicle_number")+","+document.getData().get("invoice_number")+","+document.getData().get("part_name")+","+document.getData().get("part_code")+","+document.getData().get("part_quantity")+","+document.getData().get("correct_barcode")+","+document.getData().get("wrong_barcode"));
//                        }
//                        abd();
//                    }
//                });
    }

    void fetchAllPlantAllmonths(){
        array.remove("All");
        int i;
        for(i=0;i<array.size(); i += 1){
           // array.get(i);
            db.collection(array.get(i).toString())
                    .document("data")
                    .collection("all data")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                            for(DocumentSnapshot document: queryDocumentSnapshots.getDocuments()){
                                Log.e("documet is: ", document.getData().toString());
                                data.append("\n"+ document.getData().get("plant")+","+document.getData().get("date")+","+document.getData().get("vehicle_number")+","+document.getData().get("invoice_number")+","+document.getData().get("part_name")+","+document.getData().get("part_code")+","+document.getData().get("part_quantity")+","+document.getData().get("correct_barcode")+","+document.getData().get("wrong_barcode"));
                            }
                            abd();
                        }
                    });
        }

    }

    void fetchAllPlantSelectedmonths(){
        array.remove("All");
        int i;
        for(i=0;i<array.size(); i += 1){
            // array.get(i);
            db.collection(array.get(i).toString())
                    .document("data")
                    .collection("all data")
                    .whereEqualTo("month",selected_month)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                            for(DocumentSnapshot document: queryDocumentSnapshots.getDocuments()){
                                Log.e("documet is: ", document.getData().toString());
                                data.append("\n"+ document.getData().get("plant")+","+document.getData().get("date")+","+document.getData().get("vehicle_number")+","+document.getData().get("invoice_number")+","+document.getData().get("part_name")+","+document.getData().get("part_code")+","+document.getData().get("part_quantity")+","+document.getData().get("correct_barcode")+","+document.getData().get("wrong_barcode"));
                            }
                            abd();
                        }
                    });
        }

    }

}