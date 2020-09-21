package com.yog.androidarena.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yog.androidarena.R;
import com.yog.androidarena.activity.LibExpansionActivity;
import com.yog.androidarena.databinding.LibRecviewBinding;
import com.yog.androidarena.databinding.NativeRecAdviewBinding;
import com.yog.androidarena.model.LibList;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class LibAdapter extends RecyclerView.Adapter {
    private Context context;
    private FirebaseFirestore db;
    private List<LibList> libList;
    private List<Object> libAndAdList;
    private List<DocumentSnapshot> documentSnapshotList;
    private final int AD_VIEW_TYPE=-1;
    //private boolean adView=false;
    private static final int LIST_AD_DELTA = 5;
    private static final int CONTENT = 0;
    private static final int AD = 1;

    /*public LibAdapter(Context context, List<LibList> libList, List<DocumentSnapshot> documentSnapshotList) {
        this.context = context;
        this.libList=libList;
        this.documentSnapshotList=documentSnapshotList;
    }*/

    public LibAdapter(Context context, List<Object> libAndAdList,List<DocumentSnapshot> documentSnapshotList) {
        this.context = context;
        this.libAndAdList=libAndAdList;
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
        if(viewType==AD_VIEW_TYPE) {
            NativeRecAdviewBinding nativeRecAdviewBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.native_rec_adview, parent, false);
            return new AdViewHolder(nativeRecAdviewBinding);
        }
        else {
            LibRecviewBinding libRecBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.lib_recview, parent, false);
            return new Holder(libRecBinding);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof AdViewHolder)
        {
            TemplateView templateView = (TemplateView) libAndAdList.get(position);
            AdViewHolder adViewHolder = (AdViewHolder) holder;

            if(adViewHolder.nativeRecAdviewBinding.nativeAdCard.getChildCount() > 0)
                adViewHolder.nativeRecAdviewBinding.nativeAdCard.removeAllViews();
            if(templateView.getParent() != null)
                ((ViewGroup)templateView.getParent()).removeView(templateView);

            adViewHolder.nativeRecAdviewBinding.nativeAdCard.addView(templateView);


           // ((AdViewHolder) holder).nativeRecAdviewBinding.smallNativeTemplate.se
           /* AdViewHolder adViewHolder = (AdViewHolder)holder;
            int randomAdUrl=new Random().nextInt(Constants.AD_TYPES.size());
            General.INSTANCE.loadNativeTemplateAd
                    (context,adViewHolder.nativeRecAdviewBinding.smallNativeTemplate,Constants.AD_TYPES.get(randomAdUrl));*/
           //loadNativeTemplateAd();
        }else {
            Holder libHolder = (Holder)holder;
            LibList lib = (LibList) libAndAdList.get(position);
            libHolder.libRecviewBinding.libName.setText(lib.getLibName());
            libHolder.libRecviewBinding.libShortDesc.setText(lib.getLibShortDesc());

            holder.itemView.setOnClickListener(view -> {
                //configFirebaseCloud(position);
                getRespectiveLibData(position);
            });
        }
     }

    @Override
    public int getItemCount() {
        return libAndAdList.size();
    }


    @Override
    public int getItemViewType(int position) {
        /*if(libList.get(position)==null)
            return AD_VIEW_TYPE;*/
        if(libAndAdList.get(position) instanceof TemplateView)
            return AD_VIEW_TYPE;
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
        //intent.putExtra(Constants.EMAIL,((MainActivity)context).getIntent().getStringExtra(Constants.EMAIL));
        context.startActivity(intent);
    }


    private static class AdViewHolder extends RecyclerView.ViewHolder{

        protected NativeRecAdviewBinding nativeRecAdviewBinding;
        AdViewHolder(NativeRecAdviewBinding nativeRecAdviewBinding) {
            super(nativeRecAdviewBinding.getRoot());
            this.nativeRecAdviewBinding = nativeRecAdviewBinding;
        }
    }

    private static class Holder extends RecyclerView.ViewHolder{

        protected LibRecviewBinding libRecviewBinding;
        Holder(LibRecviewBinding libRecviewBinding) {
            super(libRecviewBinding.getRoot());
            this.libRecviewBinding = libRecviewBinding;
        }
    }
}


