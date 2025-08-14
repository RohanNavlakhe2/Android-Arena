package com.yog.androidarena.ui.articles;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.paging.LoadStates;
import androidx.paging.PagedList;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.Query;
import com.yog.androidarena.R;
import com.yog.androidarena.activity.MainActivity;
import com.yog.androidarena.adapter.ArticlePagingAdapter;
import com.yog.androidarena.databinding.FragmentArticlesBinding;
import com.yog.androidarena.model.ArticleModel;

import timber.log.Timber;

public class ArticlesFragment extends Fragment {

    private  FragmentArticlesBinding fragmentArticlesBinding;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        fragmentArticlesBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_articles,container,false);
        return fragmentArticlesBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.tag("ar_frag").d("on create view");
        //setting title
        ((MainActivity) context).setTitleAccordingToFragment(2);
        //start shimmer
        showShimmer();
        //load
        loadDataFromCloud();
    }

    private void showShimmer()
    {
        Timber.d("show shimmer");
        fragmentArticlesBinding.shimmer.startShimmer();
        fragmentArticlesBinding.shimmer.setVisibility(View.VISIBLE);

    }

    public void hideShimmer()
    {
        Timber.d("hide shimmer");
        fragmentArticlesBinding.shimmer.stopShimmer();
        fragmentArticlesBinding.shimmer.setVisibility(View.GONE);
    }


    private void loadDataFromCloud() {
        Query query = ((MainActivity)context).getDb()
                .collection("Articles")
                .orderBy("no", Query.Direction.DESCENDING);



        // This configuration comes from the Paging Support Library
        PagingConfig config = new PagingConfig(20,10,false);

        FirestorePagingOptions<ArticleModel> options = new FirestorePagingOptions.Builder<ArticleModel>()
                .setLifecycleOwner(this)
                .setQuery(query, config, ArticleModel.class)
                .build();


        //init rec
        ArticlePagingAdapter articlePagingAdapter = new ArticlePagingAdapter(this,context, options);
        fragmentArticlesBinding.articleRec.setLayoutManager(new LinearLayoutManager(context));
        fragmentArticlesBinding.articleRec.setAdapter(articlePagingAdapter);

        articlePagingAdapter.addLoadStateListener(combinedLoadStates -> {
            LoadState loadStates = combinedLoadStates.getSource().getRefresh();
            if (loadStates instanceof LoadState.Loading) {
                showShimmer();
            } else {
                hideShimmer();
            }
            return null;
        });


    }


}