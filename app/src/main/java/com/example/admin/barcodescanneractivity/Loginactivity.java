package com.example.admin.barcodescanneractivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;

public class Loginactivity extends AppCompatActivity {
      EditText EMAIL,PASSWORD;

    Button Login;
    Spinner plant_selection;
    ProgressDialog progressDialog;
    Spinner select_month, select_plant;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> array = new ArrayList<String>();
    TextView recoverpassword;
    String[] stringArray={};
    SharedPreferences sh;
    SharedPreferences.Editor myEdit;
    FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
    FirebaseUser currentuser = firebaseAuth.getCurrentUser();
    ProgressDialog progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.login_activity);
         init();


        plant_selection.setEnabled(false);
        plant_spinner();
         Login.setOnClickListener(new btnloginonclicklistener());
         recoverpassword.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 showRecoverPasswordDialog();
             }
         });
        sh = getSharedPreferences("LoginSharedPref", MODE_PRIVATE);
        myEdit = sh.edit();



    }

    void init(){

        progressBar = new ProgressDialog(Loginactivity.this);
        Login = findViewById(R.id.login_button);
        plant_selection = findViewById(R.id.spinner_plant);
        EMAIL = findViewById(R.id.login_email);
        PASSWORD = findViewById(R.id.login_password);
        recoverpassword = findViewById(R.id.recoverpassword);
    }

    public class btnloginonclicklistener implements View.OnClickListener{
        @Override
        public void onClick(View view) {


            if(EMAIL.getText().toString().isEmpty()){
                EMAIL.setError("Invalid Email");
                PASSWORD.setFocusable(true);
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(EMAIL.getText().toString()).matches()) {
                EMAIL.setError("Invalid Email");
                PASSWORD.setFocusable(true);
            }
//            if (!EMAIL.getText().toString().matches(Patterns.EMAIL_ADDRESS.toString())){
//                EMAIL.setError("Invalid email");
//                EMAIL.setFocusable(true);
//            }
            else if (PASSWORD.length() < 6){
                PASSWORD.setError("PASSWORD SHOULD BE Atleast 6 digits ");
                PASSWORD.setFocusable(true);

            }else if(plant_selection.getSelectedItem().toString().matches("--Select plants--")){
                Toast.makeText(Loginactivity.this, "Please select plant", Toast.LENGTH_SHORT).show();
            }else{
                progressDialog = new ProgressDialog(Loginactivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Logging In");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                Login_auth(EMAIL.getText().toString(),PASSWORD.getText().toString());
            }
//            else if (PASSWORD.getText().toString().length() < 6 ){
//                PASSWORD.setError("enter correct password");
//                PASSWORD.setFocusable(true);
//            }



        }
    }

    void spinner_plants(){
        String[] parts = {"--Select plants--","Chakan","Bhpoal"};
        ArrayAdapter adapter_plants = new ArrayAdapter(this,android.R.layout.simple_spinner_item,parts);
        adapter_plants.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        plant_selection.setAdapter(adapter_plants);
    }

    void  Login_auth(String email,String password){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(@NonNull AuthResult authResult) {

              if (authResult.getAdditionalUserInfo().isNewUser()){
                  Toast.makeText(Loginactivity.this,"Register first",Toast.LENGTH_LONG).show();
              }

                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                 firebaseFirestore.collection(plant_selection.getSelectedItem().toString().toLowerCase(Locale.ROOT)).
                      document("users").
                      collection("all users")
                      .whereEqualTo("email", email).get()
                         .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                     @Override
                     public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                         if (!queryDocumentSnapshots.getDocuments().isEmpty()) {

                               progressDialog.dismiss();
                               myEdit.putString("userType", "user");
                               myEdit.commit();
                             SharedPreferences sharedPreferences = getSharedPreferences("PLANTADMIN",MODE_PRIVATE);
                             SharedPreferences.Editor ADMINDATA = sharedPreferences.edit();
                             ADMINDATA.putString("plant",plant_selection.getSelectedItem().toString());
                             ADMINDATA.putString("login_email",email);
                             ADMINDATA.commit();
                             Intent intent = new Intent(Loginactivity.this, vehicleinformation.class);
                             startActivity(intent);
                             finish();
                             Toast.makeText(Loginactivity.this, "Registered User", Toast.LENGTH_LONG).show();
                         }else {
                             progressDialog.dismiss();
                             Toast.makeText(Loginactivity.this, "Not Registered User", Toast.LENGTH_LONG).show();
                         }
                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                      progressDialog.dismiss();
                     Toast.makeText(Loginactivity.this,"error",Toast.LENGTH_LONG).show();
                     Log.e("failure","failure");
                     }
                 });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Loginactivity.this,"Login failed",Toast.LENGTH_LONG).show();

            }
        });


    }

    void plant_spinner(){

        array.add("--Select Plant--");

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
//                        ArrayAdapter adapter_parts = new ArrayAdapter(ExportToExcel.this,android.R.layout.simple_spinner_item,stringArray);
//                        adapter_parts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        select_plant.setAdapter(adapter_parts);
                        ArrayAdapter adapter_plants = new ArrayAdapter(Loginactivity.this,android.R.layout.simple_spinner_item,stringArray);
                        adapter_plants.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        plant_selection.setAdapter(adapter_plants);

                        plant_selection.setEnabled(true);
                        //progressDialog.dismiss();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e!=null){
                            Toast.makeText(Loginactivity.this, "Cant fetch plants", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



//        ArrayAdapter adapter_parts = new ArrayAdapter(ExportToExcel.this,android.R.layout.simple_spinner_item,plantArray);
//        adapter_parts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        select_plant.setAdapter(adapter_parts);
    }


    private void showRecoverPasswordDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                AlertDialog.Builder builder = new AlertDialog.Builder(Loginactivity.this);
                builder.setTitle("Recover Password");
                LinearLayout linearLayout = new LinearLayout(Loginactivity.this);
                final EditText emailet = new EditText(Loginactivity.this);//write your registered email
                emailet.setText("Email");
                emailet.setMinEms(16);
                emailet.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                linearLayout.addView(emailet);
                linearLayout.setPadding(10, 10, 10, 10);
                builder.setView(linearLayout);
                builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String emaill = emailet.getText().toString().trim();
                        beginRecovery(emaill);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

            }
        });
    }



    // begin recovery email (async task class)
    private void beginRecovery(String emaill) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {



                progressBar.setMessage("Sending Email....");
                progressBar.setCanceledOnTouchOutside(false);
                progressBar.show();
            }
        });

        // send reset password email
        firebaseAuth.sendPasswordResetEmail(emaill).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        progressBar.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(Loginactivity.this, "Done sent", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Loginactivity.this, "Error Occured", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        progressBar.dismiss();
                        Toast.makeText(Loginactivity.this, "Error Failed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Loginactivity.this, MainActivity.class));
        finish();
    }
}
