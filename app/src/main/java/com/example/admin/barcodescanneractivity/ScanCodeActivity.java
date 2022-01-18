package com.example.admin.barcodescanneractivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class ScanCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    int MY_PERMISSIONS_REQUEST_CAMERA=0;
    AlertDialog.Builder builder;
    int number;
    String selectedPartName;
    int quantity;
    String vehicle_number, invoice_number,date,part_name;
    String month_name;
    int s1;
    TextView scannedItems_txt;
    TextView totalItems_txt;
    TextView summary_date;
    TextView summary_vehiclenumber;
    TextView summary_invoicenumber;
    TextView summary_partquantity;
    TextView summary_partname;
    TextView summary_correct;
    TextView summary_wrong;
    SmsManager mySmsManager = SmsManager.getDefault();
    int correct;
    SharedPreferences sh;
    SharedPreferences.Editor myEdit;
    AlertDialog alertDialog;
    AlertDialog alertDialogWrong;
    AlertDialog alertDialogWrongSummary;
    AlertDialog alertDialogCorrectSummary;
    AlertDialog alertDialogCorrect;
    AlertDialog alertDialogSummary;
    int wrong;
    FirebaseFirestore db;
    String plants;
    ArrayList<String> supervisor_phno;
    ArrayList<String> head_supervisor_phno;
    ArrayList<String> all_phno;
    String loginemail;
    ZXingScannerView scannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        db = FirebaseFirestore.getInstance();
        builder = new AlertDialog.Builder(this);
        Intent intent = getIntent();
        selectedPartName= intent.getStringExtra("selectedPartName");
        quantity = Integer.parseInt(intent.getStringExtra("quantity"));
        vehicle_number = intent.getStringExtra("vehicle_number");
        invoice_number = intent.getStringExtra("invoice_number");
        date = intent.getStringExtra("date");
        month_name = intent.getStringExtra("month");
        part_name = intent.getStringExtra("part_name");
        //intent.putExtra("supervisor_phno", supervisor_phno);
        supervisor_phno = intent.getStringArrayListExtra("supervisor_phno");
        head_supervisor_phno = intent.getStringArrayListExtra("head_supervisor_phno");

        Toast.makeText(ScanCodeActivity.this, "phno list:"+supervisor_phno.toString(), Toast.LENGTH_SHORT).show();
        @SuppressLint("WrongConstant") SharedPreferences sh = getSharedPreferences("PLANTADMIN",MODE_APPEND);
        plants = sh.getString("plant","");
        loginemail = sh.getString("login_email","");

//        db.collection(plants)
//                .document("supervisor")
//                .collection("all supervisors")
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
//                        for(DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
//                            supervisor_phno.add(document.getData().get("phno").toString());
//                        }
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(ScanCodeActivity.this,"Can't get supervisor number for sending message", Toast.LENGTH_LONG).show();
//            }
//        });




    }

    @Override
    public void handleResult(Result result) {
        //MainActivity.resulttextview.setText(result.getText());
       // compnentsinformation.receivedpartnumber = result.getText();
        //onBackPressed();
//        Intent intent = new Intent(ScanCodeActivity.this, compnentsinformation.class);
//        startActivity(intent);


        sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        s1 = sh.getInt("start_count",9 );
        s1++;
        myEdit = sh.edit();

        if(s1==quantity){
            //Toast.makeText(ScanCodeActivity.this, "Scanning done", Toast.LENGTH_SHORT).show();
            //onBackPressed();
            if(result.getText().matches(selectedPartName)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ScanCodeActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                final View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.greendialog, viewGroup, false);
                builder.setView(dialogView);
                alertDialogCorrectSummary = builder.create();
                alertDialogCorrectSummary.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                scannedItems_txt = dialogView.findViewById(R.id.items_scanned);
                scannedItems_txt.setText(String.valueOf(s1));
                totalItems_txt = dialogView.findViewById(R.id.items_total);
                totalItems_txt.setText(String.valueOf(quantity));



                dialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


//                        //correct++;
//                        correct = sh.getInt("correct",0 );
//                        correct += 1;
//                       // SharedPreferences.Editor myEdit = sh.edit();
//                        myEdit.putInt("correct",correct);
//                        myEdit.apply();
                       new MyTaskCorrectSummary().execute();
//                        Log.e("result","Task "+String.valueOf(s1)+": "+"correct: "+String.valueOf(sh.getInt("correct",0 ))+"wrong: "+String.valueOf(sh.getInt("wrong",0 )));
//                        // Toast.makeText(ScanCodeActivity.this, "correct: "+String.valueOf(correct)+"\n"+"wrong: "+String.valueOf(wrong),Toast.LENGTH_SHORT).show();
//                        alertDialog.dismiss();
//                        summarydialog();


                        //onBackPressed();
                    }
                });
                alertDialogCorrectSummary.setCanceledOnTouchOutside(false);
                alertDialogCorrectSummary.setCancelable(false);
                alertDialogCorrectSummary.show();

            }else{
                if (checkSmsPermission() == true) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ScanCodeActivity.this);
                    ViewGroup viewGroup = findViewById(android.R.id.content);
                    final View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.reddialog, viewGroup, false);
                    builder.setView(dialogView);
                    alertDialogWrongSummary = builder.create();
                    alertDialogWrongSummary.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    scannedItems_txt = dialogView.findViewById(R.id.items_scanned);
                    scannedItems_txt.setText(String.valueOf(s1));
                    totalItems_txt = dialogView.findViewById(R.id.items_total);
                    totalItems_txt.setText(String.valueOf(quantity));


//                    SmsManager mySmsManager = SmsManager.getDefault();
//                    mySmsManager.sendTextMessage("7447297382", null, "Part with wrongly applied barcode found in vehicle no. "+vehicle_number.toString(), null, null);
//                    Toast.makeText(this, "SMS sent.", Toast.LENGTH_LONG).show();

                    dialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            //wrong++;
//                            wrong = sh.getInt("wrong",0 );
//                            wrong += 1;
//                           // SharedPreferences.Editor myEdit = sh.edit();
//                            myEdit.putInt("wrong",wrong);
//                            myEdit.apply();
                            new MyTaskWrongSummary().execute();
//                            Log.e("result","Task "+String.valueOf(s1)+": "+"correct: "+String.valueOf(sh.getInt("correct",0 ))+"wrong: "+String.valueOf(sh.getInt("wrong",0 )));
//                            alertDialog.dismiss();
//                            summarydialog();

                            //onBackPressed();

                        }
                    });
                    alertDialogWrongSummary.setCanceledOnTouchOutside(false);
                    alertDialogWrongSummary.setCancelable(false);
                    alertDialogWrongSummary.show();

                }else {
                    Toast.makeText(this, "No permission for SMS", Toast.LENGTH_LONG).show();
                }
            }


        }else{
           // Toast.makeText(ScanCodeActivity.this, "Scanning not done", Toast.LENGTH_SHORT).show();

            if(result.getText().matches(selectedPartName)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(ScanCodeActivity.this);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            final View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.greendialog, viewGroup, false);
            builder.setView(dialogView);
            alertDialogCorrect = builder.create();
            alertDialogCorrect.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


            scannedItems_txt = dialogView.findViewById(R.id.items_scanned);
            scannedItems_txt.setText(String.valueOf(s1));
            totalItems_txt = dialogView.findViewById(R.id.items_total);
            totalItems_txt.setText(String.valueOf(quantity));



            dialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //SharedPreferences.Editor myEdit = sh.edit();
//                    myEdit.putInt("start_count",s1);
//                    myEdit.apply();
//
//                    //correct++;
//                    correct = sh.getInt("correct",0 );
//                    correct += 1;
//                    //SharedPreferences.Editor myEdit = sh.edit();
//                    myEdit.putInt("correct",correct);
//                    myEdit.apply();
                    new MyTaskCorrect().execute();
//                    Log.e("result","Task "+String.valueOf(s1)+": "+"correct: "+String.valueOf(sh.getInt("correct",0 ))+"wrong: "+String.valueOf(sh.getInt("wrong",0 )));
//                    alertDialog.dismiss();
//                    finish();
//                    startActivity(getIntent());
                    //onBackPressed();
                }
            });
            alertDialogCorrect.setCanceledOnTouchOutside(false);
            alertDialogCorrect.setCancelable(false);
            alertDialogCorrect.show();

        }else{
            if (checkSmsPermission() == true) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ScanCodeActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                final View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.reddialog, viewGroup, false);
                builder.setView(dialogView);
                alertDialogWrong = builder.create();
                alertDialogWrong.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                scannedItems_txt = dialogView.findViewById(R.id.items_scanned);
                scannedItems_txt.setText(String.valueOf(s1));
                totalItems_txt = dialogView.findViewById(R.id.items_total);
                totalItems_txt.setText(String.valueOf(quantity));


//                SmsManager mySmsManager = SmsManager.getDefault();
//                mySmsManager.sendTextMessage("7447297382", null, "Part with wrongly applied barcode found in vehicle no. "+vehicle_number.toString(), null, null);
//                Toast.makeText(this, "SMS sent.", Toast.LENGTH_LONG).show();

                dialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                       // SharedPreferences.Editor myEdit = sh.edit();
//                        myEdit.putInt("start_count",s1);
//                        myEdit.apply();
//
//                        //wrong++;
//                        wrong = sh.getInt("wrong",0 );
//                        wrong += 1;
//                        //SharedPreferences.Editor myEdit = sh.edit();
//                        myEdit.putInt("wrong",wrong);
//                        myEdit.apply();
                        new MyTaskWrong().execute();
                        //String str_result= new MyTaskWrong().execute().get();
//                        Log.e("result","Task "+String.valueOf(s1)+": "+"correct: "+String.valueOf(sh.getInt("correct",0 ))+"wrong: "+String.valueOf(sh.getInt("wrong",0 )));
//                        alertDialog.dismiss();
//                        //onBackPressed();
//                        finish();
//                        startActivity(getIntent());
                    }
                });
                alertDialogWrong.setCanceledOnTouchOutside(false);
                alertDialogWrong.setCancelable(false);
                alertDialogWrong.show();

            }else {
                Toast.makeText(this, "No permission for SMS", Toast.LENGTH_LONG).show();
            }
        }

        }



       // Toast.makeText(ScanCodeActivity.this, "correct: "+String.valueOf(sh.getInt("correct",0))+"\n"+"wrong: "+String.valueOf(sh.getInt("wrong",0)),Toast.LENGTH_LONG).show();


    }

    void summarydialog(){
        //correct = quantity-wrong;

        AlertDialog.Builder builder = new AlertDialog.Builder(ScanCodeActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        final View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.summarydialog, viewGroup, false);
        builder.setView(dialogView);
        alertDialogSummary = builder.create();
        alertDialogSummary.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialogSummary.setCancelable(false);

        summary_date =dialogView.findViewById(R.id.summary_date);
        summary_vehiclenumber = dialogView.findViewById(R.id.summary_vehiclenumber);
        summary_invoicenumber = dialogView.findViewById(R.id.summary_invoicenumber);
        summary_partname = dialogView.findViewById(R.id.summary_partname);
        summary_partquantity = dialogView.findViewById(R.id.summary_partquantity);
        summary_correct = dialogView.findViewById(R.id.summary_correct);
        summary_wrong = dialogView.findViewById(R.id.summary_wrong);

        summary_date.setText(date);
        summary_vehiclenumber.setText(vehicle_number);
        summary_invoicenumber.setText(invoice_number);
        summary_partname.setText(selectedPartName);
        summary_partquantity.setText(String.valueOf(quantity));
        summary_correct.setText(String.valueOf(sh.getInt("correct",0)));
        summary_wrong.setText(String.valueOf(sh.getInt("wrong",0)));

//        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());


        dialogView.findViewById(R.id.ok_summary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //send to firestore
                Map<String, Object> scanner_data = new HashMap<>();
                scanner_data.put("date",date );
                scanner_data.put("vehicle_number", vehicle_number);
                scanner_data.put("invoice_number", invoice_number);
                scanner_data.put("part_code", selectedPartName);
                scanner_data.put("part_quantity", String.valueOf(quantity));
                //
                scanner_data.put("time",date);
                //
                scanner_data.put("user_email",loginemail);
                //
                scanner_data.put("correct_barcode", String.valueOf(sh.getInt("correct",0)));
                scanner_data.put("wrong_barcode", String.valueOf(sh.getInt("wrong",0)));
                String wrong = String.valueOf(sh.getInt("wrong",0));
                scanner_data.put("month",month_name);
                //Todo: get plant name from SharedPrefs
                scanner_data.put("plant",plants);
                scanner_data.put("part_name",part_name);

                //Todo: get plant name from SharedPrefs
                db.collection(plants)
                        .document("data")
                        .collection("all data")
                        .add(scanner_data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(@NonNull DocumentReference documentReference) {
                                Toast.makeText(ScanCodeActivity.this, "data added to database", Toast.LENGTH_LONG).show();
                                SmsManager mySmsManager = SmsManager.getDefault();
//                                //Todo: get phno from firebase of this plants supervisors and use here
//                                //Todo: get plant name from SharedPrefs
                                if(checkSmsPermission()){
                                    //Toast.makeText(ScanCodeActivity.this,"Sending SMs ",Toast.LENGTH_LONG).show();
                                    //mySmsManager.sendTextMessage(supervisor_phno.get(0), null, "!!Alert!!\n\n"+"Please note in vehicle no: "+"MH12BH3501"+"\n"+"Part no: "+"SJ18001"+"\n"+"Found Wrong barcode QTY: "+"3"+"\n\n"+"PLease act immediately before reaching to customer", null, null);
                                    if(sh.getInt("wrong",0)==0){
                                        for(String i : head_supervisor_phno) {
                                            new Sendsms_headsupervisor(i).execute();
                                        }
                                    }else{
                                        supervisor_phno.addAll(head_supervisor_phno);
                                        for(String i : supervisor_phno) {
                                            new Sendsms(i,wrong).execute();
                                        }
                                    }
                                }else{
                                    Toast.makeText(ScanCodeActivity.this,"Sms permission not given",Toast.LENGTH_LONG);
                                }
//                                for(String i: supervisor_phno){
//                                    mySmsManager.sendTextMessage(i.toString(), null, "Part with wrongly applied barcode found in vehicle no. "+vehicle_number.toString()+"\n\n"+"Scanning Summary: "+"\n\n"+"Plant Name: "+plants+"\n"+"Date: "+date+"\n"+"Vehicle Number: "+vehicle_number+"\n"+"Invoice Number: "+invoice_number+"\n"+"Selected Part: "+selectedPartName+"\n"+"Part Quantity: "+String.valueOf(quantity)+"\n"+"Correct Barcode: "+String.valueOf(correct)+"\n"+"Wrong Barcode: "+String.valueOf(wrong), null, null);
//
//                                }
//                                mySmsManager.sendTextMessage("+919168389278", null, "Part with wrongly applied barcode found in vehicle no. "+vehicle_number.toString()+"\n\n"+"Scanning Summary: "+"\n\n"+"Plant Name: "+plants+"\n"+"Date: "+date+"\n"+"Vehicle Number: "+vehicle_number+"\n"+"Invoice Number: "+invoice_number+"\n"+"Selected Part: "+selectedPartName+"\n"+"Part Quantity: "+String.valueOf(quantity)+"\n"+"Correct Barcode: "+String.valueOf(correct)+"\n"+"Wrong Barcode: "+String.valueOf(wrong), null, null);
//                                mySmsManager.sendTextMessage("+919325831422", null, "Part with wrongly applied barcode found in vehicle no. "+vehicle_number.toString()+"\n\n"+"Scanning Summary: "+"\n\n"+"Plant Name: "+plants+"\n"+"Date: "+date+"\n"+"Vehicle Number: "+vehicle_number+"\n"+"Invoice Number: "+invoice_number+"\n"+"Selected Part: "+selectedPartName+"\n"+"Part Quantity: "+String.valueOf(quantity)+"\n"+"Correct Barcode: "+String.valueOf(correct)+"\n"+"Wrong Barcode: "+String.valueOf(wrong), null, null);
//                                mySmsManager.sendTextMessage("+918956026663", null, "Part with wrongly applied barcode found in vehicle no. "+vehicle_number.toString()+"\n\n"+"Scanning Summary: "+"\n\n"+"Plant Name: "+plants+"\n"+"Date: "+date+"\n"+"Vehicle Number: "+vehicle_number+"\n"+"Invoice Number: "+invoice_number+"\n"+"Selected Part: "+selectedPartName+"\n"+"Part Quantity: "+String.valueOf(quantity)+"\n"+"Correct Barcode: "+String.valueOf(correct)+"\n"+"Wrong Barcode: "+String.valueOf(wrong), null, null);

                                //Toast.makeText(ScanCodeActivity.this, "SMS sent.", Toast.LENGTH_LONG).show();
                                myEdit.clear().commit();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ScanCodeActivity.this, "data NOT added to database", Toast.LENGTH_LONG).show();
                            }
                        });

                alertDialogSummary.dismiss();
               // onBackPressed();
                Intent intent = new Intent(ScanCodeActivity.this, vehicleinformation.class);
                startActivity(intent);
                finish();



            }
        });
        alertDialogSummary.show();





    }

    private Boolean checkSmsPermission() {

        boolean result = ContextCompat.checkSelfPermission(ScanCodeActivity.this, Manifest.permission.SEND_SMS) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }


    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        scannerView.setResultHandler(this);
//        scannerView.startCamera();
//    }


    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        Toast.makeText(ScanCodeActivity.this, "Cannot go back! Complete the scanning",Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    void correct(){

    }

    void wrong(){

    }

    private class MyTaskWrong extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            myEdit.putInt("start_count",s1);
            myEdit.apply();

            //wrong++;
            wrong = sh.getInt("wrong",0 );
            wrong += 1;
            //SharedPreferences.Editor myEdit = sh.edit();
            myEdit.putInt("wrong",wrong);
            myEdit.apply();

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            //Log.e("result","Task "+String.valueOf(s1)+": "+"correct: "+String.valueOf(sh.getInt("correct",0 ))+"wrong: "+String.valueOf(sh.getInt("wrong",0 )));
            Log.e("result","Task "+String.valueOf(s1)+": "+"\n\n\ncorrect: "+String.valueOf(sh.getInt("correct",0 ))+"wrong: "+String.valueOf(sh.getInt("wrong",0 ))+"\n\n\n");
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), sh.getAll().toString(), Toast.LENGTH_SHORT).show();
                }
            });
            //Toast.makeText(ScanCodeActivity.this, "correct: "+String.valueOf(sh.getInt("correct",0))+"\n"+"wrong: "+String.valueOf(sh.getInt("wrong",0)),Toast.LENGTH_LONG).show();
            alertDialogWrong.dismiss();
            //onBackPressed();
            finish();
            startActivity(getIntent());

        }
    }

    class Sendsms extends AsyncTask<Void,Void, Void>{
        String phno;
        String wrong;
        Sendsms(String phno, String wrong){
            this.phno = phno;
            this.wrong = wrong;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mySmsManager.sendTextMessage(phno, null, "!!Alert!!\n\n"+"Please note in vehicle no: "+vehicle_number.toString()+"\n"+"Part no: "+selectedPartName.toString()+"\n"+"Found Wrong barcode QTY: "+wrong.toString()+"\n\n"+"Please act immediately before reaching to customer", null, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            Toast.makeText(ScanCodeActivity.this, "Sms sent", Toast.LENGTH_SHORT).show();
            super.onPostExecute(unused);
        }
    }

    class Sendsms_headsupervisor extends AsyncTask<Void,Void, Void>{
        String phno;
        Sendsms_headsupervisor(String phno){
            this.phno = phno;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mySmsManager.sendTextMessage(phno, null, "!!Alert!!\n\n"+"Please note in vehicle no: "+vehicle_number.toString()+"\n"+"Part no: "+selectedPartName.toString()+"\n"+"\n\n"+"All parts have correct barcode", null, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            Toast.makeText(ScanCodeActivity.this, "Sms sent", Toast.LENGTH_SHORT).show();
            super.onPostExecute(unused);
        }
    }

    private class MyTaskWrongSummary extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            myEdit.putInt("start_count",s1);
            myEdit.apply();

            //wrong++;
            wrong = sh.getInt("wrong",0 );
            wrong += 1;
            //SharedPreferences.Editor myEdit = sh.edit();
            myEdit.putInt("wrong",wrong);
            myEdit.apply();

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Log.e("result","Task "+String.valueOf(s1)+": "+"\n\n\ncorrect: "+String.valueOf(sh.getInt("correct",0 ))+"wrong: "+String.valueOf(sh.getInt("wrong",0 ))+"\n\n\n");
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), sh.getAll().toString(), Toast.LENGTH_SHORT).show();
                }
            });
            //Toast.makeText(ScanCodeActivity.this, "correct: "+String.valueOf(sh.getInt("correct",0))+"\n"+"wrong: "+String.valueOf(sh.getInt("wrong",0)),Toast.LENGTH_LONG).show();
            alertDialogWrongSummary.dismiss();
            summarydialog();
        }
    }

    private class MyTaskCorrect extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            myEdit.putInt("start_count",s1);
            myEdit.apply();

            //correct++;
            correct = sh.getInt("correct",0 );
            correct += 1;
            //SharedPreferences.Editor myEdit = sh.edit();
            myEdit.putInt("correct",correct);
            myEdit.apply();

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            //Log.e("result","Task "+String.valueOf(s1)+": "+"correct: "+String.valueOf(sh.getInt("correct",0 ))+"wrong: "+String.valueOf(sh.getInt("wrong",0 )));
            Log.e("result","Task "+String.valueOf(s1)+": "+"\n\n\ncorrect: "+String.valueOf(sh.getInt("correct",0 ))+"wrong: "+String.valueOf(sh.getInt("wrong",0 ))+"\n\n\n");
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), sh.getAll().toString(), Toast.LENGTH_SHORT).show();
                }
            });
            //Toast.makeText(ScanCodeActivity.this, "correct: "+String.valueOf(sh.getInt("correct",0))+"\n"+"wrong: "+String.valueOf(sh.getInt("wrong",0)),Toast.LENGTH_LONG).show();
            alertDialogCorrect.dismiss();
            //onBackPressed();
            finish();
            startActivity(getIntent());
        }
    }

    private class MyTaskCorrectSummary extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            myEdit.putInt("start_count",s1);
            myEdit.apply();

            //correct++;
            correct = sh.getInt("correct",0 );
            correct += 1;
            //SharedPreferences.Editor myEdit = sh.edit();
            myEdit.putInt("correct",correct);
            myEdit.apply();

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            //Log.e("result","Task "+String.valueOf(s1)+": "+"correct: "+String.valueOf(sh.getInt("correct",0 ))+"wrong: "+String.valueOf(sh.getInt("wrong",0 )));
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), sh.getAll().toString(), Toast.LENGTH_SHORT).show();
                }
            });
            Log.e("result","Task "+String.valueOf(s1)+": "+"\n\n\ncorrect: "+String.valueOf(sh.getInt("correct",0 ))+"wrong: "+String.valueOf(sh.getInt("wrong",0 ))+"\n\n\n");
            //Toast.makeText(ScanCodeActivity.this, "correct: "+String.valueOf(sh.getInt("correct",0))+"\n"+"wrong: "+String.valueOf(sh.getInt("wrong",0)),Toast.LENGTH_LONG).show();
            alertDialogCorrectSummary.dismiss();
            summarydialog();
        }
    }
}
