package com.example.admin.barcodescanneractivity.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.admin.barcodescanneractivity.Admin.Adapter.view_user_adapter;
import com.example.admin.barcodescanneractivity.Admin.Datamodel.view_data_datamodel;
import com.example.admin.barcodescanneractivity.Admin.Datamodel.view_user_datamodel;
import com.example.admin.barcodescanneractivity.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewUser extends AppCompatActivity {

    RecyclerView user_recyclerview;
    LinearLayout loading;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<view_user_datamodel> view_user_datamodelArrayList = new ArrayList<view_user_datamodel>();
    String plants;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        loading = findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

        user_recyclerview = findViewById(R.id.user_recyclerview);
        user_recyclerview.setHasFixedSize(true);
        user_recyclerview.setLayoutManager(new LinearLayoutManager(getParent()));
        @SuppressLint("WrongConstant") SharedPreferences sh = getSharedPreferences("PLANTADMIN",MODE_APPEND);
        plants = sh.getString("plant","");
        //Todo: get plant name from SharedPrefs
        //Todo: With users we need to extract supervisors also
        loaddata();
//        db.collection(plants)
//                .document("users")
//                .collection("all users")
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
//                        for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots.getDocuments()){
//                            view_user_datamodel child = documentSnapshot.toObject(view_user_datamodel.class);
//                            Log.e("child", child.getEmail());
//                            view_user_datamodelArrayList.add(child);
//                            view_user_adapter adapter = new view_user_adapter(view_user_datamodelArrayList);
//                            user_recyclerview.setAdapter(adapter);
//                            loading.setVisibility(View.INVISIBLE);
//                            // fetchSupervisor();
//                        }
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(ViewUser.this, "Cannot fetch users", Toast.LENGTH_SHORT).show();
//            }
//        });




    }

    private void loaddata() {
        Task task1 = db.collection(plants)
                .document("users")
                .collection("all users")
                .get();

        Task task2 = db.collection(plants)
                .document("supervisor")
                .collection("all supervisors")
                .get();

        //we choosed whenAllSucces beacause we just wnated successlisterner but when we need failure listener we need to use the whenAllComplete
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2);
        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                String data = "";

                for (QuerySnapshot queryDocumentSnapshots : querySnapshots) {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        view_user_datamodel child = document.toObject(view_user_datamodel.class);
                        Log.e("child", child.getEmail());
                        view_user_datamodelArrayList.add(child);
//                        view_user_adapter adapter = new view_user_adapter(view_user_datamodelArrayList);
//                        user_recyclerview.setAdapter(adapter);
//                        loading.setVisibility(View.INVISIBLE);
                    }
                }
                view_user_adapter adapter = new view_user_adapter(view_user_datamodelArrayList);
                user_recyclerview.setAdapter(adapter);
                loading.setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ViewUser.this, "cannot get the data", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchSupervisor() {
        db.collection(plants)
                .document("supervisor")
                .collection("all supervisors")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots.getDocuments()){
                            view_user_datamodel child = documentSnapshot.toObject(view_user_datamodel.class);
                            Log.e("child", child.getEmail());
                            view_user_datamodelArrayList.add(child);
                            view_user_adapter adapter = new view_user_adapter(view_user_datamodelArrayList);
                            user_recyclerview.setAdapter(adapter);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ViewUser.this, "Cannot fetch supervisor", Toast.LENGTH_SHORT).show();
            }
        });

    }
}