package com.yog.androidarena.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.yog.androidarena.R;
import com.yog.androidarena.databinding.NoTxtRecviewBinding;
import com.yog.androidarena.databinding.SomeMorethingsRecBinding;

import java.util.List;

public class PointAdapter extends RecyclerView.Adapter {
    private Context context;
    private NoTxtRecviewBinding noTxtRecviewBinding;
    private SomeMorethingsRecBinding someMorethingsRecBinding;
    private List<String> points;

    public PointAdapter(Context context, List<String> points) {
        this.context = context;
        this.points=points;
     }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        noTxtRecviewBinding= DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.no_txt_recview,parent,false);

        return new RecyclerView.ViewHolder(noTxtRecviewBinding.getRoot()) {
            @Override
            public String toString() {
                return super.toString();
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        noTxtRecviewBinding.serialNoTxt.setText(String.valueOf(position+1)+".");
        noTxtRecviewBinding.descTxt.setText(points.get(position));
     }

    @Override
    public int getItemCount() {
        return points.size();
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
