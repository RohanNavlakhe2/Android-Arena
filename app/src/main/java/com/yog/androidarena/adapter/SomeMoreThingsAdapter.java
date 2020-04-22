package com.yog.androidarena.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yog.androidarena.R;
import com.yog.androidarena.databinding.NoTxtRecviewBinding;
import com.yog.androidarena.databinding.SomeMorethingsRecBinding;

import java.util.List;
import java.util.Map;

public class SomeMoreThingsAdapter extends RecyclerView.Adapter {
    private Context context;
    private NoTxtRecviewBinding noTxtRecviewBinding;
    private SomeMorethingsRecBinding someMorethingsRecBinding;
    private List<Map<String,Object>> someMoreThingsSections;
    private Map<String,Object> someMoreThingsMap;

    public SomeMoreThingsAdapter(Context context, List<Map<String, Object>> someMoreThingsSections) {
        this.context = context;
        this.someMoreThingsSections = someMoreThingsSections;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        //removing first element because this is introduction string we already have shown it in
        //LibExpansionActivity.
        someMoreThingsSections.remove(0);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

         someMorethingsRecBinding= DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.some_morethings_rec,parent,false);

        return new RecyclerView.ViewHolder(someMorethingsRecBinding.getRoot()) {
            @Override
            public String toString() {
                return super.toString();
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
         someMoreThingsMap=someMoreThingsSections.get(position);
         /*someMoreThingsMap.get("0") contains title of the section*/
           someMorethingsRecBinding.subPointTitle.setText(someMoreThingsMap.get("0").toString());
           someMorethingsRecBinding.subPointsRec.setLayoutManager(new LinearLayoutManager(context));
        /*someMoreThingsMap.get("1") contains List respective to title*/
            //someMorethingsRecBinding.subPointsRec.setAdapter(new PointAdapter(context, (List<String>) someMoreThingsMap.get("1")));
        someMorethingsRecBinding.subPointsRec.setAdapter(new Point_SnippetAdapter(context, (List)someMoreThingsMap.get("1")));

    }

    @Override
    public int getItemCount() {
        return someMoreThingsSections.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
