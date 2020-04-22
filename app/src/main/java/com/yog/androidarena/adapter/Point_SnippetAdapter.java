package com.yog.androidarena.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yog.androidarena.R;
import com.yog.androidarena.databinding.CodeSnippetRecviewBinding;
import com.yog.androidarena.databinding.NoTxtRecviewBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Point_SnippetAdapter extends RecyclerView.Adapter {

    private Context context;
    private NoTxtRecviewBinding noTxtRecviewBinding;
    private CodeSnippetRecviewBinding codeSnippetRecviewBinding;
    private List howToImplementSectionList;
    private String whatView;

   /*private List howToImplementSectionList may contain map or string*/
    /*If map means 0 key will contain heading and 1 key will contain the code snippet*/
    /*If only String means it contains instruction text*/

    public Point_SnippetAdapter(Context context, List howToImplementSectionList) {
        this.context = context;
        this.howToImplementSectionList=howToImplementSectionList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(whatView.equalsIgnoreCase("number_point_view"))
        {
            noTxtRecviewBinding= DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.no_txt_recview,parent,false);
            return new RecyclerView.ViewHolder(noTxtRecviewBinding.getRoot()) {
                @Override
                public String toString() {
                    return super.toString();
                }
            };
        }else if(whatView.equalsIgnoreCase("code_snippet_view")){
            codeSnippetRecviewBinding= DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.code_snippet_recview,parent,false);
            return new RecyclerView.ViewHolder(codeSnippetRecviewBinding.getRoot()) {
                @Override
                public String toString() {
                    return super.toString();
                }
            };
        }

       return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(whatView.equalsIgnoreCase("number_point_view"))
        {
            noTxtRecviewBinding.serialNoTxt.setText((position + 1) +".");
            noTxtRecviewBinding.descTxt.setText(howToImplementSectionList.get(position).toString());
        }else if(whatView.equalsIgnoreCase("code_snippet_view"))
        {
            codeSnippetRecviewBinding.includeNoTxtRecView.serialNoTxt.setText(String.valueOf(position+1));
            HashMap map= (HashMap) howToImplementSectionList.get(position);
            /*map.get("0") contains title of the code Ex.(Add this to gradle)*/
            /*map.get("1") contains the codesnippet img url*/
            codeSnippetRecviewBinding.includeNoTxtRecView.descTxt.setText(Objects.requireNonNull(map.get("0")).toString());
            Glide.with(context)
                    .load(Objects.requireNonNull(map.get("1")).toString())
                    //.placeholder(context.getResources().getDrawable(R.drawable.loading_gif))
                    .into(codeSnippetRecviewBinding.codeSnippet);
            //codeSnippetRecviewBinding.codeSnippet.setText(map.get("1").toString());
        }
    }

    @Override
    public int getItemCount() {
        return howToImplementSectionList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(howToImplementSectionList.get(position) instanceof Map)
             whatView="code_snippet_view";
        else
            whatView="number_point_view";
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
