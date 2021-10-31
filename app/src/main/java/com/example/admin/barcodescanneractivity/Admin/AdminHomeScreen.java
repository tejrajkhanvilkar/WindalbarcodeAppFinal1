package com.example.admin.barcodescanneractivity.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.admin.barcodescanneractivity.LoginoptionsActivity;
import com.example.admin.barcodescanneractivity.Masteradminlogin;
import com.example.admin.barcodescanneractivity.R;
import com.example.admin.barcodescanneractivity.ScanCodeActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminHomeScreen extends Fragment {
      Button VIEWDATA,USERDATA,EDITUSERDATA,ADDPLANT,ADDPARTS,CONFIRM;
      AlertDialog passcodedialog;
      TextInputEditText pascode;

      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.adminhomefragment, container, false);
        VIEWDATA =view.findViewById(R.id.btn_viewdata);
        USERDATA=view.findViewById(R.id.btn_viewuser);
        EDITUSERDATA=view.findViewById(R.id.btn_edituserdata);
        ADDPLANT=view.findViewById(R.id.btn_addpartdata);
        ADDPARTS=view.findViewById(R.id.btn_addplantdata);
        VIEWDATA.setOnClickListener(new btnviewdata());
        ADDPARTS.setOnClickListener(new btnaddplant());
        ADDPLANT.setOnClickListener(new btnaddparts());
        USERDATA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ViewUser.class);
                startActivity(intent);
            }
        });
        EDITUSERDATA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),editprofile.class);
                startActivity(intent);
            }
        });

        return view;
      }

      private class btnviewdata implements View.OnClickListener{
     @Override
     public void onClick(View view) {
         Intent intent = new Intent(getActivity(),ViewData.class);
         startActivity(intent);
       }
     }
     private class btnaddplant implements View.OnClickListener{
         @Override
         public void onClick(View view) {
//             Intent intent = new Intent(getActivity(), Masteradminlogin.class);
//             startActivity(intent);
             AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
             ViewGroup viewGroup = view.findViewById(android.R.id.content);
             final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.passcodedialog, viewGroup, false);
             builder.setView(dialogView);
             passcodedialog = builder.create();
             passcodedialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
             CONFIRM = dialogView.findViewById(R.id.btn_confirm);
             pascode = dialogView.findViewById(R.id.et_passcode);

             CONFIRM.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     if(pascode.getText().toString().isEmpty()){
                         Toast.makeText(getActivity(), "Please enter passcode", Toast.LENGTH_SHORT).show();
                     }
                     if(pascode.getText().toString().matches("1234")){
                         Intent intent = new Intent(getActivity(), Addplants.class);
                         startActivity(intent);
                         passcodedialog.dismiss();
                     }else{
                         Toast.makeText(getActivity(), "Invalid Passcode", Toast.LENGTH_SHORT).show();
                     }
                 }
             });


             passcodedialog.show();

         }

     }

    private class btnaddparts implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(),Addparts.class);
            startActivity(intent);
        }
    }
}
