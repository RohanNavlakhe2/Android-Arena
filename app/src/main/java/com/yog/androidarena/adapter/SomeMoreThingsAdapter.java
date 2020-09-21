package com.yog.androidarena.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yog.androidarena.R;
import com.yog.androidarena.databinding.NativeRecAdviewBinding;
import com.yog.androidarena.databinding.NoTxtRecviewBinding;
import com.yog.androidarena.databinding.SomeMorethingsRecBinding;
import com.yog.androidarena.util.Constants;
import com.yog.androidarena.util.General;

import java.util.List;
import java.util.Map;
import java.util.Random;

import timber.log.Timber;

public class SomeMoreThingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private NoTxtRecviewBinding noTxtRecviewBinding;
    private SomeMorethingsRecBinding someMorethingsRecBinding;
    private List<Map<String, Object>> someMoreThingsSections;
    private Map<String, Object> someMoreThingsMap;
    private final int AD_VIEW_TYPE = -1;
    private NativeRecAdviewBinding nativeRecAdviewBinding;


    // List<Map<String, Object>> someMoreThingsSections
    public SomeMoreThingsAdapter(Context context, List<Map<String, Object>> someMoreThingsSections) {
        this.context = context;
        this.someMoreThingsSections = someMoreThingsSections;
        Timber.tag("list").d("adapter");
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == AD_VIEW_TYPE) {
            nativeRecAdviewBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.native_rec_adview, parent, false);
            return new Holder(nativeRecAdviewBinding.getRoot());
        } else {
            someMorethingsRecBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.some_morethings_rec, parent, false);
            return new Holder(someMorethingsRecBinding.getRoot());
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==AD_VIEW_TYPE)
        {
            int randomAdUrl=
                    new Random().nextInt(Constants.AD_TYPES.size());
            General.INSTANCE.loadNativeTemplateAd(context,nativeRecAdviewBinding.smallNativeTemplate,Constants.AD_TYPES.get(randomAdUrl));
            //loadNativeTemplateAd();
        }else {
            someMoreThingsMap = someMoreThingsSections.get(position);
            /*someMoreThingsMap.get("0") contains title of the section*/
            Timber.tag("list").d("title is: %s", someMoreThingsMap.get("0").toString());
            someMorethingsRecBinding.subPointTitle.setText(someMoreThingsMap.get("0").toString());
            someMorethingsRecBinding.subPointsRec.setLayoutManager(new LinearLayoutManager(context));
            /*someMoreThingsMap.get("1") contains List respective to title*/
            //someMorethingsRecBinding.subPointsRec.setAdapter(new PointAdapter(context, (List<String>) someMoreThingsMap.get("1")));
            someMorethingsRecBinding.subPointsRec.setAdapter(new PointSnippetAdapter(context, (List) someMoreThingsMap.get("1")));
        }

    }

    @Override
    public int getItemCount() {
        return someMoreThingsSections.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(someMoreThingsSections.get(position)==null)
            return AD_VIEW_TYPE;
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class Holder extends RecyclerView.ViewHolder {

        Holder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
