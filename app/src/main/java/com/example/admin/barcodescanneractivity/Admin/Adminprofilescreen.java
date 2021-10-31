package com.example.admin.barcodescanneractivity.Admin;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.admin.barcodescanneractivity.ExportToExcel;
import com.example.admin.barcodescanneractivity.LoginoptionsActivity;
import com.example.admin.barcodescanneractivity.R;
import com.example.admin.barcodescanneractivity.ScanCodeActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Adminprofilescreen extends Fragment {
    StringBuilder data;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    View view;
    SharedPreferences shLogin;
    SharedPreferences.Editor myEditLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = LayoutInflater.from(getActivity()).inflate(R.layout.adminprofilefragment, container, false);

        FirebaseAuth firebaseAuth;
        FirebaseUser currentuser;
        TextView Logout = view.findViewById(R.id.logoutadmin);
        TextView editprofile = view.findViewById(R.id.Editprofileadmin);
        editprofile.setVisibility(View.INVISIBLE);

        TextView exporttoexcel = view.findViewById(R.id.export_to_excel_admin);
        firebaseAuth = FirebaseAuth.getInstance();

        shLogin = getActivity().getSharedPreferences("LoginSharedPref", MODE_PRIVATE);
        myEditLogin = shLogin.edit();

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                myEditLogin.putString("userType", "none");
                myEditLogin.commit();
                startActivity(new Intent(getActivity(), LoginoptionsActivity.class));
                getActivity().finish();
            }
        });



        exporttoexcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("onclicked", "exporting" );
                //export_to_excel();
                Intent intent = new Intent(getActivity(), ExportToExcel.class);
                startActivity(intent);

            }
        });


        return view;
    }

    public void abd(){
        Log.i("finallist",data.toString());
        try {
            FileOutputStream out = getActivity().openFileOutput("windal_data.csv", MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();

            Context context = getContext();
            File filelocation = new File(getActivity().getFilesDir(), "windal_data.csv");
            Uri path = FileProvider.getUriForFile(context, "com.example.admin.barcodescanneractivity", filelocation);
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


        data = new StringBuilder();
        data.append("Plant, Date, Vehicle Number, Invoice Number, Part Name, Part Code, Part Quantity, Correct Barcode, Wrong Barcode"+"\n");

        db.collection("chakan")
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
