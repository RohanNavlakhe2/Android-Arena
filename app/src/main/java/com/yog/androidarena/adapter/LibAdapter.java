package com.yog.androidarena.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yog.androidarena.LibExpansionActivity;
import com.yog.androidarena.R;
import com.yog.androidarena.databinding.LibRecviewBinding;
import com.yog.androidarena.model.LibList;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class LibAdapter extends RecyclerView.Adapter {
    private Context context;
    private FirebaseFirestore db;
    private LibRecviewBinding libRecBinding;
    private List<LibList> libList;
    private List<DocumentSnapshot> documentSnapshotList;

    public LibAdapter(Context context, List<LibList> libList,List<DocumentSnapshot> documentSnapshotList) {
        this.context = context;
        this.libList=libList;
        this.documentSnapshotList=documentSnapshotList;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        libRecBinding= DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.lib_recview,parent,false);

        return new Holder(libRecBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        libRecBinding.libName.setText(libList.get(position).getLibName());
        libRecBinding.libShortDesc.setText(libList.get(position).getLibShortDesc());

        holder.itemView.setOnClickListener(view->{
                 //configFirebaseCloud(position);
            getRespectiveLibData(position);
        });
     }

    @Override
    public int getItemCount() {
        return libList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void getRespectiveLibData(int libNumber)
    {
        DocumentSnapshot document=documentSnapshotList.get(libNumber);
        HashMap<String, Object> map = (HashMap<String, Object>) document.getData();
        Intent intent = new Intent(context, LibExpansionActivity.class);
        intent.putExtra("map", (Serializable) map);
        context.startActivity(intent);
    }

    private static class Holder extends RecyclerView.ViewHolder{

        Holder(@NonNull View itemView) {
            super(itemView);
        }
    }
}


