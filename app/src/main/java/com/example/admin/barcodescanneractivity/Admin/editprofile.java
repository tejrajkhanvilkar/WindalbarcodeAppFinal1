package com.example.admin.barcodescanneractivity.Admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.admin.barcodescanneractivity.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Locale;

public class editprofile extends AppCompatActivity {
   Button duser,dsupervisor,DELETEUSER,DELETESUPERVISIOR;
   EditText useremail,supervisioremail;
   String plants;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.edit_profile);
         init();
             duser.setOnClickListener(new deleteuserbtnonclicklistener());
             dsupervisor.setOnClickListener(new deletesupervisorbtnonclicklistener());

    }

    void init(){
        duser = findViewById(R.id.deleteuser);
        dsupervisor = findViewById(R.id.deleteSupervisor);
        @SuppressLint("WrongConstant") SharedPreferences sh = getSharedPreferences("PLANTADMIN",MODE_APPEND);
        plants = sh.getString("plant","");
    }

   private class  deleteuserbtnonclicklistener implements View.OnClickListener{
       @Override
       public void onClick(View view) {
           AlertDialog.Builder builder = new AlertDialog.Builder(editprofile.this);
           ViewGroup viewGroup = findViewById(android.R.id.content);
           final View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.delete_user_alertdialog, viewGroup, false);
           builder.setView(dialogView);
           final AlertDialog alertDialog = builder.create();
           alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
           DELETEUSER = dialogView.findViewById(R.id.delteuserButton);
           useremail = dialogView.findViewById(R.id.deleteuseremail);
           DELETEUSER.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                 if (useremail.getText().toString().isEmpty()){
                     Toast.makeText(editprofile.this, "Enter Email First ", Toast.LENGTH_SHORT).show();
                 }else {
                     deleteuser(useremail.getText().toString().trim());
                 }


               }
           });


            alertDialog.show();
       }
   }


    private class  deletesupervisorbtnonclicklistener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(editprofile.this);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            final View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.delete_supervisor_alertdialog, viewGroup, false);
            builder.setView(dialogView);
            final AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            DELETESUPERVISIOR = dialogView.findViewById(R.id.deletesupervisorButton);
            supervisioremail = dialogView.findViewById(R.id.deletesupervisoremail);

            DELETESUPERVISIOR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  if (supervisioremail.getText().toString().isEmpty()){
                      Toast.makeText(editprofile.this, "Enter Email First ", Toast.LENGTH_SHORT).show();
                  }else {
                      deletesupervisor(supervisioremail.getText().toString().trim());
                  }
                }
            });
               alertDialog.show();
        }
    }



    void deleteuser(String email){

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(plants).document("users")
                .collection("all users").whereEqualTo("email",email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){
                    Toast.makeText(editprofile.this,"User with" + email + "deleted",Toast.LENGTH_LONG).show();

                    firebaseFirestore.collection(plants).document("users")
                            .collection("all users").document(snapshot.getId().toString()).delete();

                    Intent intent = new Intent(editprofile.this,ViewUser.class);
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(editprofile.this,"ERROR OCCURED",Toast.LENGTH_LONG).show();
            }
        });

    }

    void deletesupervisor(String email){

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(plants).document("supervisor").collection("all supervisors").whereEqualTo("email",email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){
                    Toast.makeText(editprofile.this,"Supervisor with" + email + "deleted",Toast.LENGTH_LONG).show();

                    firebaseFirestore.collection(plants).document("supervisor").collection("all supervisors").document(snapshot.getId().toString()).delete();
                    Intent intent = new Intent(editprofile.this,ViewUser.class);
                    startActivity(intent);
                     finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(editprofile.this,"ERROR OCCURED",Toast.LENGTH_LONG).show();
            }
        });

    }

}
