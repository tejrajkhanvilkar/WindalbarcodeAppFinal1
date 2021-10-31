package com.example.admin.barcodescanneractivity.Admin;

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

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.admin.barcodescanneractivity.Loginactivity;
import com.example.admin.barcodescanneractivity.R;
import com.example.admin.barcodescanneractivity.vehicleinformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminLoginScreen extends AppCompatActivity {
    TextView recoverPaaword;
    EditText EMAIL,PASSWORD;
    Button Login;
    Spinner plant_selection;
    ProgressDialog progressDialog;
    SharedPreferences sh;
    SharedPreferences.Editor myEdit;
    ArrayList<String> array = new ArrayList<String>();
    String[] stringArray={};
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog progressBar;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminlogin);
        init();
        //spinner_plants();
        plant_spinner();
        Login.setOnClickListener(new btnloginonclicklisteneradmin());
        recoverPaaword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecoverPasswordDialog();
            }
        });
        sh = getSharedPreferences("LoginSharedPref", MODE_PRIVATE);
        myEdit = sh.edit();
    }

    void init(){
        recoverPaaword = findViewById(R.id.forgetpassword);
        progressBar = new ProgressDialog(AdminLoginScreen.this);
        EMAIL = findViewById(R.id.login_email_admin);
        PASSWORD = findViewById(R.id.login_password_admin);
        Login = findViewById(R.id.login_button_admin);
        plant_selection = findViewById(R.id.spinner_plant);

    }

    class btnloginonclicklisteneradmin implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if (EMAIL.getText().toString().isEmpty()){
                EMAIL.setError("Invalid email");
                EMAIL.setFocusable(true);
            }else if (PASSWORD.getText().toString().length() < 6 ){
                PASSWORD.setError("enter correct password");
                PASSWORD.setFocusable(true);
            }else if (plant_selection.getSelectedItem().toString().matches("--Select plants--")){

                Toast.makeText(AdminLoginScreen.this,"PLEASE SELECT PALNT",Toast.LENGTH_LONG).show();
                plant_selection.setFocusable(true);
            }else {
                SharedPreferences sharedPreferences = getSharedPreferences("PLANTADMIN",MODE_PRIVATE);
                SharedPreferences.Editor ADMINDATA = sharedPreferences.edit();
                ADMINDATA.putString("plant",plant_selection.getSelectedItem().toString());
                ADMINDATA.commit();
                progressDialog = new ProgressDialog(AdminLoginScreen.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Logging In");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                Admin_Login_auth(EMAIL.getText().toString(),PASSWORD.getText().toString());
            }
            }



        }

        void spinner_plants(){
        String[] parts = {"--Select plants--","chakan","bhpoal"};
        ArrayAdapter adapter_plants = new ArrayAdapter(this,android.R.layout.simple_spinner_item,parts);
        adapter_plants.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        plant_selection.setAdapter(adapter_plants);
    }

    private void showRecoverPasswordDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                AlertDialog.Builder builder = new AlertDialog.Builder(AdminLoginScreen.this);
                builder.setTitle("Recover Password");
                LinearLayout linearLayout = new LinearLayout(AdminLoginScreen.this);
                final EditText emailet = new EditText(AdminLoginScreen.this);//write your registered email
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
                            Toast.makeText(AdminLoginScreen.this, "Done sent", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(AdminLoginScreen.this, "Error Occured", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(AdminLoginScreen.this, "Error Failed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }






    void Admin_Login_auth(String email,String password){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(@NonNull AuthResult authResult) {

                if (authResult.getAdditionalUserInfo().isNewUser()){
                    Toast.makeText(AdminLoginScreen.this,"Register first",Toast.LENGTH_LONG).show();
                }

                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                firebaseFirestore.collection(plant_selection.getSelectedItem().toString()).document("admin").collection("all admin")
                        .whereEqualTo("email", email).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.getDocuments().isEmpty()) {

                                    progressDialog.dismiss();
                                    myEdit.putString("userType", "admin");
                                    myEdit.commit();
                                    Intent intent = new Intent(AdminLoginScreen.this, Adminbottomnavigation.class);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(AdminLoginScreen.this, "Registered User", Toast.LENGTH_LONG).show();
                                }else {
                                    progressDialog.dismiss();
                                    Toast.makeText(AdminLoginScreen.this, "Not Registered User", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast .makeText(AdminLoginScreen.this,"error",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AdminLoginScreen.this,"Login failed",Toast.LENGTH_LONG).show();

                    }
                });


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
                        ArrayAdapter adapter_plants = new ArrayAdapter(AdminLoginScreen.this,android.R.layout.simple_spinner_item,stringArray);
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
                            Toast.makeText(AdminLoginScreen.this, "Cant fetch plants", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



//        ArrayAdapter adapter_parts = new ArrayAdapter(ExportToExcel.this,android.R.layout.simple_spinner_item,plantArray);
//        adapter_parts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        select_plant.setAdapter(adapter_parts);
    }


}
