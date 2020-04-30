package com.yog.androidarena.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yog.androidarena.R;
import com.yog.androidarena.activity.LibExpansionActivity;
import com.yog.androidarena.databinding.LibRecviewBinding;
import com.yog.androidarena.databinding.NativeRecAdviewBinding;
import com.yog.androidarena.model.LibList;
import com.yog.androidarena.util.Constants;
import com.yog.androidarena.util.General;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class LibAdapter extends RecyclerView.Adapter {
    private Context context;
    private FirebaseFirestore db;
    private LibRecviewBinding libRecBinding;
    private NativeRecAdviewBinding nativeRecAdviewBinding;
    private List<LibList> libList;
    private List<DocumentSnapshot> documentSnapshotList;
    private final int AD_VIEW_TYPE=-1;
    //private boolean adView=false;
    private static final int LIST_AD_DELTA = 5;
    private static final int CONTENT = 0;
    private static final int AD = 1;

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
        if(viewType==AD_VIEW_TYPE) {
            nativeRecAdviewBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.native_rec_adview, parent, false);
            return new Holder(nativeRecAdviewBinding.getRoot());
        }
        else {
            libRecBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.lib_recview, parent, false);
            return new Holder(libRecBinding.getRoot());
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==AD_VIEW_TYPE)
        {
            int randomAdUrl=new Random().nextInt(Constants.AD_TYPES.size());
            General.INSTANCE.loadNativeTemplateAd
                    (context,nativeRecAdviewBinding.smallNativeTemplate,Constants.AD_TYPES.get(randomAdUrl));
           //loadNativeTemplateAd();
        }else {
            libRecBinding.libName.setText(libList.get(position).getLibName());
            libRecBinding.libShortDesc.setText(libList.get(position).getLibShortDesc());

            holder.itemView.setOnClickListener(view -> {
                //configFirebaseCloud(position);
                getRespectiveLibData(position);
            });
        }
     }

    @Override
    public int getItemCount() {
        return libList.size();
    }


    @Override
    public int getItemViewType(int position) {
        if(libList.get(position)==null)
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

    private void loadNativeTemplateAd() {
        //MobileAds.initialize(this, "[_app-id_]");
        AdLoader adLoader = new AdLoader.Builder(context, Constants.TEST_AD)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        NativeTemplateStyle styles = new
                                NativeTemplateStyle.Builder().
                                withMainBackgroundColor(new ColorDrawable(context.getResources().getColor(R.color.transperent))).build();

                        TemplateView template =nativeRecAdviewBinding.smallNativeTemplate;
                        template.setStyles(styles);
                        template.setNativeAd(unifiedNativeAd);

                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }


    private static class Holder extends RecyclerView.ViewHolder{

        Holder(@NonNull View itemView) {
            super(itemView);
        }
    }
}


