package com.example.admin.barcodescanneractivity.Admin.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.barcodescanneractivity.Admin.Datamodel.view_user_datamodel;
import com.example.admin.barcodescanneractivity.R;

import java.util.ArrayList;

public class view_user_adapter extends RecyclerView.Adapter {

    ArrayList<view_user_datamodel> mArrayList;

    public view_user_adapter(ArrayList<view_user_datamodel> mArrayList) {
        this.mArrayList = mArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewuser, parent,false);
        return new viewuserviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        viewuserviewholder mviewuserviewholder = (viewuserviewholder) holder;
        view_user_datamodel view_user_datamodel = mArrayList.get(position);
        mviewuserviewholder.name.setText(view_user_datamodel.getName());
        mviewuserviewholder.phno.setText(view_user_datamodel.getPhno());
        mviewuserviewholder.email.setText(view_user_datamodel.getEmail());
        mviewuserviewholder.usertype.setText(view_user_datamodel.getUsertype());




    }


    public class viewuserviewholder extends RecyclerView.ViewHolder{
        TextView name,phno,email,usertype;

        public viewuserviewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.higherAuthority_name);
            phno = itemView.findViewById(R.id.higherAuthority_phnno);
            email = itemView.findViewById(R.id.higherAuthority_email);
            usertype = itemView.findViewById(R.id.higherAuthority_position);
        }
    }

    @Override
    public int getItemCount() {
        if (mArrayList.size() == 0){
            return  0;
        }
        return mArrayList.size();
    }
}
