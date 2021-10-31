package com.example.admin.barcodescanneractivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

//import com.example.admin.barcodescanneractivity.Admin.ViewData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class vehicleinformation extends AppCompatActivity {
    public String selectedPartName;
    Button Continue;
    Spinner parts_info;
    String parts_selected;
    EditText et_date;
    Spinner vehicle_number;
    EditText invoice_number;
    EditText part_quantity,newtruckno;
    ImageButton datepickerdialog,add_new_truck_button;
    ArrayList<String> supervisor_phno = new ArrayList<String>();
    ArrayList<String> head_supervisor_phno = new ArrayList<String>();
    ArrayList<String> Trucklist = new ArrayList<String>();

    Spinner selectParts;
    Button codename;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> array = new ArrayList<String>();
    //    String parts[]={};
    String[] stringArray={};
    String[] stringArraytruck={};
    String selected_parts,plants;
    ProgressDialog dialog;
    String part_code,month_name;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;
    AlertDialog progress;
    SharedPreferences shLogin;
    SharedPreferences.Editor myEditLogin;
    String finalVehicleNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_vechile);



        init();
        @SuppressLint("WrongConstant") SharedPreferences sh = getSharedPreferences("PLANTADMIN",MODE_APPEND);
        plants = sh.getString("plant","");
        shLogin = getSharedPreferences("LoginSharedPref", MODE_PRIVATE);
        myEditLogin = shLogin.edit();
//         dialog = ProgressDialog.show(vehicleinformation.this, "",
//                "Loading. Please wait...", true);
//        dialog.show();

//        progressDialog = new ProgressDialog(vehicleinformation.this);
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage("Loading");
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.show();

        AlertDialog.Builder builder = new AlertDialog.Builder(vehicleinformation.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        final View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.progressdialog, viewGroup, false);
        builder.setView(dialogView);
        progress = builder.create();
        progress.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progress.show();

        sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        Calendar cal=Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        month_name = month_date.format(cal.getTime());
        //Toast.makeText(vehicleinformation.this, month_name.toString(), Toast.LENGTH_SHORT).show();
        add_new_truck_button.setOnClickListener(new addnewtruckclicksetonclicklistener());
        Fetch_truck();
        spinner_parts();
        datepickerdialog.setOnClickListener(new BtnDatePickerDialogClickListener());
        
        Continue.setOnClickListener(new btncontinue());

        db.collection(plants)
                .document("supervisor")
                .collection("all supervisors")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
                            supervisor_phno.add(document.getData().get("phno").toString());
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(vehicleinformation.this,"Can't get supervisor number for sending message", Toast.LENGTH_LONG).show();
            }
        });

        db.collection(plants)
                .document("head supervisor")
                .collection("all head supervisor")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
                            head_supervisor_phno.add(document.getData().get("phno").toString());
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(vehicleinformation.this,"Can't get head supervisor number for sending message", Toast.LENGTH_LONG).show();
            }
        });


        //dialog.dismiss();

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add calendar code here
                //calendar();

            }
        });


    }

    private void calendar() {

    }

    void init(){
       Continue = findViewById(R.id.btn_continue);
       //parts_info = findViewById(R.id.parts_spinner);
       et_date = findViewById(R.id.et_date);
       vehicle_number = findViewById(R.id.Vehicle_number);
       invoice_number = findViewById(R.id.invoice_number);
       part_quantity = findViewById(R.id.et_part_quantity);
       datepickerdialog = findViewById(R.id.btn_datepickerdialog);
       selectParts = findViewById(R.id.parts_spinner);
       add_new_truck_button = findViewById(R.id.add_truck_button);
       newtruckno = findViewById(R.id.add_new_truck);
       newtruckno.setVisibility(View.INVISIBLE);
    }


    private class addnewtruckclicksetonclicklistener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            newtruckno.setVisibility(View.VISIBLE);
        }
    }

    void Fetch_truck(){
        Trucklist.add("--Select Vehicleno--");

        //Todo: get plant name from SharedPrefs
        db.collection(plants)
                .document("truck")
                .collection("all truck")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot document: queryDocumentSnapshots.getDocuments()){
                            Log.e("type", String.valueOf(document.getData().get("number")));
                            Trucklist.add(String.valueOf(document.getData().get("number")));
                        }
                        Log.e("type",Trucklist.toString());
//                        parts = array.toArray(array);
                        stringArraytruck = Trucklist.toArray(new String[0]);
                        Log.e("converted shit",stringArraytruck.toString());
                        ArrayAdapter adapter_parts = new ArrayAdapter(vehicleinformation.this,android.R.layout.simple_spinner_item,stringArraytruck);
                        adapter_parts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        vehicle_number.setAdapter(adapter_parts);
                        progress.dismiss();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e!=null){
                            Toast.makeText(vehicleinformation.this, "Cant fetch parts", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    void Add_truck(String truckname){

       if (truckname.isEmpty()){

       }else {

           Map<String, Object> add_data = new HashMap<>();
           add_data.put("number", truckname);

           db.collection(plants)
                   .document("truck")
                   .collection("all truck").add(add_data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
               @Override
               public void onSuccess(@NonNull DocumentReference documentReference) {
                   Toast.makeText(vehicleinformation.this, "NEW TRUCK Number Added", Toast.LENGTH_SHORT).show();
               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Toast.makeText(vehicleinformation.this, "ERROR Will Number Added", Toast.LENGTH_SHORT).show();
               }
           });

       }
    }


    private class btncontinue implements View.OnClickListener{
        @Override
        public void onClick(View view) {


            newtruckno.getText().toString();

            //parts_selected = parts_info.getSelectedItem().toString();
            selected_parts = selectParts.getSelectedItem().toString();


//            if(et_date.getText().toString().equals("")||vehicle_number.getSelectedItem().toString().matches("--Select Vehicleno--")||part_quantity.getText().toString().equals("")){
//                Toast.makeText(vehicleinformation.this, "Please enter all values", Toast.LENGTH_SHORT).show();
//            }
            if(et_date.getText().toString().equals("")||(vehicle_number.getSelectedItem().toString().matches("--Select Vehicleno--")&&newtruckno.getText().toString().isEmpty())||part_quantity.getText().toString().equals("")){
                Toast.makeText(vehicleinformation.this, "Please enter all values", Toast.LENGTH_SHORT).show();
            }
            else {
                if (selected_parts.equals("--Select parts--")){
                Toast.makeText(vehicleinformation.this,"Please select parts",Toast.LENGTH_SHORT).show();
                return;
            }
                if(vehicle_number.getSelectedItem().toString().matches("--Select Vehicleno--")&& !newtruckno.getText().toString().isEmpty()){
                    finalVehicleNumber = newtruckno.getText().toString();
                }
                if(!vehicle_number.getSelectedItem().toString().matches("--Select Vehicleno--")&& newtruckno.getText().toString().isEmpty()){
                    finalVehicleNumber = vehicle_number.getSelectedItem().toString();
                }
                if(!vehicle_number.getSelectedItem().toString().matches("--Select Vehicleno--")&& !newtruckno.getText().toString().isEmpty()){
                    finalVehicleNumber = vehicle_number.getSelectedItem().toString();
                }


                //Toast.makeText(vehicleinformation.this, finalVehicleNumber.toString(), Toast.LENGTH_SHORT).show();


                Add_truck(newtruckno.getText().toString());
                //Todo: get plant name from SharedPrefs
                db.collection(plants)
                        .document("parts")
                        .collection("all parts")
                        .whereEqualTo("name",selected_parts)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                                for(DocumentSnapshot document: queryDocumentSnapshots.getDocuments()){
                                    Toast.makeText(vehicleinformation.this, document.getData().get("code").toString(), Toast.LENGTH_SHORT).show();
                                    part_code = document.getData().get("code").toString();
                                }
                                selectedPartName = part_code;

                                new AsyncSharedPref().execute();


                            }
                        });


            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_memu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.user_logout:
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                myEditLogin.putString("userType", "none");
                myEditLogin.commit();
                Intent intent = new Intent(vehicleinformation.this,LoginoptionsActivity.class);
                startActivity(intent);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class AsyncSharedPref extends AsyncTask<Void, Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
//            sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
//            myEdit = sharedPreferences.edit();

            myEdit.putInt("start_count",0 );
            myEdit.putInt("correct",0);
            myEdit.putInt("wrong",0);
            myEdit.commit();
            Log.e("shredPref: ",sharedPreferences.getAll().toString());
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Intent intent = new Intent(vehicleinformation.this, ScanCodeActivity.class);
            intent.putExtra("selectedPartName", selectedPartName);
            intent.putExtra("quantity", part_quantity.getText().toString());
            //intent.putExtra("vehicle_number", vehicle_number.getSelectedItem().toString());
            intent.putExtra("vehicle_number", finalVehicleNumber);
            intent.putExtra("invoice_number", invoice_number.getText().toString());
            intent.putExtra("date", et_date.getText().toString());
            intent.putExtra("month", month_name);
            intent.putExtra("part_name", selected_parts);
            intent.putExtra("supervisor_phno", supervisor_phno);
            intent.putExtra("head_supervisor_phno", head_supervisor_phno);

            startActivity(intent);
            finish();
        }
    }

    void spinner_parts(){


        array.add("--Select parts--");

        //Todo: get plant name from SharedPrefs
        db.collection(plants)
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
                        ArrayAdapter adapter_parts = new ArrayAdapter(vehicleinformation.this,android.R.layout.simple_spinner_item,stringArray);
                        adapter_parts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        selectParts.setAdapter(adapter_parts);
                        progress.dismiss();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e!=null){
                            Toast.makeText(vehicleinformation.this, "Cant fetch parts", Toast.LENGTH_SHORT).show();
                        }
                    }
                });




    }


    private class BtnDatePickerDialogClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            DatePickerDialog dobPickerDialog =
                    new DatePickerDialog(
                            vehicleinformation.this,
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
            et_date.setText( dayOfMonth + "-" + (month+1) + "-"+year);
        }
    }
}
