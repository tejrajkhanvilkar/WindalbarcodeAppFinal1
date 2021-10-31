package com.example.admin.barcodescanneractivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import com.example.admin.barcodescanneractivity.Admin.Addparts;
import com.example.admin.barcodescanneractivity.Admin.Adminbottomnavigation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
      FirebaseAuth firebaseAuth;
      ProgressDialog progressDialog;
      EditText NAME,EMAIL,PASSWORD,PHONE;
      //TextView Sign_in;
      Spinner DESIGNATION;
      Button Create_account;
      ActionBar actionBar;
       String plants;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regestration_activity);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        init();
             Create_account.setOnClickListener(new CreateonclickListener());
//             OrganizationSpinner();
             DesignationSpinner();
    }

   // initializing view items
    void init() {
        ActionBar actionBar= getSupportActionBar();
        actionBar.hide();
        PHONE = findViewById(R.id.register_phone);
        Create_account = findViewById(R.id.create_account);
        //Sign_in = findViewById(R.id.sign_login_in);
        DESIGNATION = findViewById(R.id.registration_designation);
//        ORGANIZATION = findViewById(R.id.registration_organization);
         NAME = findViewById(R.id.register_name);
         EMAIL = findViewById(R.id.register_email);
         PASSWORD =findViewById(R.id.register_password);
         progressDialog = new ProgressDialog(RegistrationActivity.this);
         progressDialog.setMessage("Register");
         firebaseAuth = FirebaseAuth.getInstance();

       @SuppressLint("WrongConstant") SharedPreferences sh = getSharedPreferences("PLANTADMIN",MODE_APPEND);
       plants = sh.getString("plant","");
    }


    // Spinner to select organization
//    void OrganizationSpinner(){
//        String  organization [] = { "VI" };
//        ArrayAdapter arrayAdapter = new ArrayAdapter(RegistrationActivity.this,R.layout.support_simple_spinner_dropdown_item,organization);
//        ORGANIZATION.setAdapter(arrayAdapter);
//    }


    // Spinner to designation
    void DesignationSpinner(){
     String designation [] = {"--SELECT DESIGNATION--","USER","ADMIN","SUPERVISOR","HEAD SUPERVISOR"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(RegistrationActivity.this,R.layout.support_simple_spinner_dropdown_item,designation);
        DESIGNATION.setAdapter(arrayAdapter);
    }


    // create button on click listener
    private  class CreateonclickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
//            String organization = (String) ORGANIZATION.getSelectedItem();
            String phone = PHONE.getText().toString().trim();
            String designation = DESIGNATION.getSelectedItem().toString();
            String email = EMAIL.getText().toString().trim();
            String password = PASSWORD.getText().toString().trim();
            String name = NAME.getText().toString().trim();
            if (name.isEmpty()){
               NAME.setError("Enter your name");
               NAME.setFocusable(true);
            }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
              EMAIL.setError("INVALID MAIL");
              EMAIL.setFocusable(true);
            }else if (password.length() < 6){
                PASSWORD.setError("PASSWORD SHOULD BE Atleast 6 digits ");
                PASSWORD.setFocusable(true);

            }else if (!Patterns.PHONE.matcher(phone).matches() && phone.length() ==10){
                PHONE.setError("Enter Correct Phone Number");
                PHONE.setFocusable(true);
            }else if (DESIGNATION.getSelectedItem().toString().matches("--SELECT DESIGNATION--")){
                Toast.makeText(RegistrationActivity.this,"Select role",Toast.LENGTH_LONG).show();
                DESIGNATION.setFocusable(true);
            }
            else {
                registeruser(name,email,password,designation,phone);
            }

        }
    }

    // Sign in Text on click listener
    private class Signin implements View.OnClickListener{
        @Override
        public void onClick(View v) {
             Intent intent = new Intent(RegistrationActivity.this, Adminbottomnavigation.class);
             startActivity( intent);
        }
    }


    //Register user method (add to async task class)


    private void registeruser(final String Name, final String Email , String Password,  final String Designation , final String Phone){

     runOnUiThread(new Runnable() {
         @Override
         public void run() {


       progressDialog.show();
         }
     });

        //Todo: What if we Nilesh sir wants to add admin for another plant? we have not given plant name option But we cannot give that option to everyone
        firebaseAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull  Task<AuthResult> task) {
               // if Email is valid
               if (task.isSuccessful()){
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {


                    progressDialog.dismiss();
                       }

                   });

                   if (Designation.matches("USER")){
                         register_data_users(Email,Phone,Name);
                   }else if (Designation.matches("ADMIN")){
                        register_data_admin(Email,Phone,Name);
                   }else if (Designation.matches("SUPERVISOR")){
                        register_data_supervisior(Email,Phone,Name);
                   }else if (Designation.matches("HEAD SUPERVISOR")){
                       register_data_Headsupervisior(Email,Phone,Name);
                   }

               }
             }
         }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull  Exception e) {
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {


               progressDialog.dismiss();
               Toast.makeText(RegistrationActivity.this, "Error Occured", Toast.LENGTH_LONG).show();

                   }
               });
                   }
         });
    }

    private void register_data_admin(String Email,String Phone,String Name){
        Map<String, Object> register_data = new HashMap<>();
        register_data.put("email",Email );
        register_data.put("phno",Phone );
        register_data.put("name", Name );
        register_data.put("usertype", DESIGNATION.getSelectedItem().toString());

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(plants).document("admin").collection("all admin")
                .add(register_data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(@NonNull DocumentReference documentReference) {
                register_data_supervisior(Email,Phone,Name);
                Toast.makeText(RegistrationActivity.this,"Admin added",Toast.LENGTH_LONG).show();

                Intent mainIntent = new Intent(RegistrationActivity.this, Adminbottomnavigation.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });



    }

    private void register_data_Headsupervisior(String Email,String Phone,String Name){
        Map<String, Object> register_data = new HashMap<>();
        register_data.put("email",Email );
        register_data.put("phno",Phone );
        register_data.put("name", Name );
        register_data.put("usertype", DESIGNATION.getSelectedItem().toString());

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(plants).document("head supervisor").collection("all head supervisor")
                .add(register_data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(@NonNull DocumentReference documentReference) {
                //register_data_supervisior(Email,Phone,Name);
                Toast.makeText(RegistrationActivity.this,"Admin added",Toast.LENGTH_LONG).show();

                Intent mainIntent = new Intent(RegistrationActivity.this, Adminbottomnavigation.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });



    }

    private void register_data_users(String Email,String Phone,String Name){
        Map<String, Object> register_data = new HashMap<>();
        register_data.put("email",Email );
        register_data.put("phno",Phone );
        register_data.put("name", Name );
        register_data.put("usertype", DESIGNATION.getSelectedItem().toString());


        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(plants).document("users").collection("all users")
                .add(register_data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(@NonNull DocumentReference documentReference) {

                Toast.makeText(RegistrationActivity.this,"User added",Toast.LENGTH_LONG).show();

                Intent mainIntent = new Intent(RegistrationActivity.this, Adminbottomnavigation.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }

    private void register_data_supervisior(String Email,String Phone,String Name){
        Map<String, Object> register_data = new HashMap<>();
        register_data.put("email",Email );
        register_data.put("phno",Phone );
        register_data.put("name", Name );
        register_data.put("usertype", DESIGNATION.getSelectedItem().toString().toLowerCase(Locale.ROOT));


        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(plants).document("supervisor").collection("all supervisors")
                .add(register_data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(@NonNull DocumentReference documentReference) {

                Toast.makeText(RegistrationActivity.this,"User added",Toast.LENGTH_LONG).show();

                Intent mainIntent = new Intent(RegistrationActivity.this, Adminbottomnavigation.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


}
