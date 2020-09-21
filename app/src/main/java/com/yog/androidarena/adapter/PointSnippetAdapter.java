package com.yog.androidarena.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.yog.androidarena.R;
import com.yog.androidarena.databinding.CodeSnippetRecviewBinding;
import com.yog.androidarena.databinding.NoTxtRecviewBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class PointSnippetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List howToImplementSectionList;
    private String whatView;
    private static final int CODE_SNIPPET_VIEW_TYPE = 0;
    private static final int NO_TXT_VIEW_TYPE = 1;



    /*private List howToImplementSectionList may contain map or string*/
    /*If map means 0 key will contain heading and 1 key will contain the code snippet*/
    /*If only String means it contains instruction text*/

    public PointSnippetAdapter(Context context, List howToImplementSectionList) {
        this.context = context;
        this.howToImplementSectionList = howToImplementSectionList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*if (whatView.equalsIgnoreCase("number_point_view"))*/
        if (viewType == NO_TXT_VIEW_TYPE) {
            NoTxtRecviewBinding noTxtRecviewBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.no_txt_recview, parent, false);
            return new NoTxtViewHolder(noTxtRecviewBinding);
        } else   {
            //View type is CODE_SNIPPET
            CodeSnippetRecviewBinding codeSnippetRecviewBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.code_snippet_recview, parent, false);
            return new CodeSnippetViewHolder(codeSnippetRecviewBinding);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        /*if (whatView.equalsIgnoreCase("number_point_view"))*/
        if (holder instanceof NoTxtViewHolder) {
            ((NoTxtViewHolder) holder).noTxtRecviewBinding.serialNoTxt.setText((position + 1) + ".");
            ((NoTxtViewHolder) holder).noTxtRecviewBinding.descTxt.setText(howToImplementSectionList.get(position).toString());
        } else  {
            ((CodeSnippetViewHolder) holder).codeSnippetRecviewBinding.includeNoTxtRecView.serialNoTxt.setText(String.valueOf(position + 1));
            HashMap map = (HashMap) howToImplementSectionList.get(position);
            //map.get("0") contains title of the code Ex.(Add this to gradle)
            // map.get("1") contains the codesnippet img url
            ((CodeSnippetViewHolder) holder).codeSnippetRecviewBinding.includeNoTxtRecView.descTxt.setText(Objects.requireNonNull(map.get("0")).toString());
            loadImageToImageView(((CodeSnippetViewHolder)holder).codeSnippetRecviewBinding,
                    Objects.requireNonNull(map.get("1")).toString());
      }
    }


    private void loadImageToImageView(CodeSnippetRecviewBinding codeSnippetRecviewBinding, String imgUrl) {

        CustomTarget<Bitmap> customTarget = new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                codeSnippetRecviewBinding.codeSnippet.setImageBitmap(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        };

        Glide.with(context)
                .asBitmap()
                .load(imgUrl)
                .placeholder(context.getResources().getDrawable(R.drawable.placeholder_image2))
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Timber.d("Image Load Failed:" + e.getMessage());
                        e.logRootCauses("Point_Snippet");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Timber.d("Image Load Success");
                        return false;
                    }
                })
                .into(customTarget);


    }

    @Override
    public int getItemCount() {
        return howToImplementSectionList.size();
    }

    @Override
    public int getItemViewType(int position) {
        /** Changed on 15-09
        if (howToImplementSectionList.get(position) instanceof Map)
            whatView = "code_snippet_view";
        else
            whatView = "number_point_view";
        return super.getItemViewType(position);
         **/

        if (howToImplementSectionList.get(position) instanceof Map)
            return CODE_SNIPPET_VIEW_TYPE;
        else
            return NO_TXT_VIEW_TYPE;
    }



    @Override
    public long getItemId(int position) {
        return position;
    }


    protected static class CodeSnippetViewHolder extends RecyclerView.ViewHolder {

        private CodeSnippetRecviewBinding codeSnippetRecviewBinding;

        public CodeSnippetViewHolder(CodeSnippetRecviewBinding codeSnippetRecviewBinding) {
            super(codeSnippetRecviewBinding.getRoot());
            this.codeSnippetRecviewBinding = codeSnippetRecviewBinding;
        }

    }

    protected static class NoTxtViewHolder extends RecyclerView.ViewHolder {

        protected NoTxtRecviewBinding noTxtRecviewBinding;

        public NoTxtViewHolder(NoTxtRecviewBinding noTxtRecviewBinding) {
            super(noTxtRecviewBinding.getRoot());
            this.noTxtRecviewBinding = noTxtRecviewBinding;
        }
    }


}
