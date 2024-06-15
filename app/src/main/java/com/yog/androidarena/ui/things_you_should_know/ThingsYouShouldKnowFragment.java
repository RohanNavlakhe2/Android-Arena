package com.yog.androidarena.ui.things_you_should_know;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yog.androidarena.R;
import com.yog.androidarena.activity.MainActivity;
import com.yog.androidarena.adapter.LibAdapter;
import com.yog.androidarena.databinding.FragmentThingsYouShouldKnowBinding;
import com.yog.androidarena.model.LibList;
import com.yog.androidarena.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ThingsYouShouldKnowFragment extends Fragment {

    private static final String TAG = "life";
    private FragmentThingsYouShouldKnowBinding fragmentThingsYouShouldKnowBinding;
    private Context context;
    private FirebaseFirestore db;
    private List<Map> mapList;
    private List<Object> allThingsAndAdList;
    private List<DocumentSnapshot> documentSnapshotList;
    private boolean isLastPage = false;
    private boolean isLoading = false;
     private boolean isScrolling = false;
    private int latestLoadedThingsListSize;
    private static final int PAGE_SIZE = 10;
    private DocumentSnapshot lastLoadedListItem;
    private DocumentSnapshot lastLoadedItemExpansion;
    private LibAdapter libAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Timber.tag(TAG).d("on create frag id:%s", this.getId());
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fragmentThingsYouShouldKnowBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_things_you_should_know, container, false);
        return fragmentThingsYouShouldKnowBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db = ((MainActivity) context).getDb();
        //setting title
        ((MainActivity) context).setTitleAccordingToFragment(0);
        showShimmer();
        //getThingsYouShouldKnowListFromCloud();
        initLibRec();
        loadThingsList();

    }

    private void loadThingsList() {
        db.collection(Constants.THINGS_LIST)
                .orderBy("orderBy", Query.Direction.DESCENDING)
                .limit(PAGE_SIZE)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Timber.d("Listen Failed in snapshot listner :%s", e.getMessage());
                            return;
                        }
                        if (queryDocumentSnapshots != null) {
                            allThingsAndAdList.addAll(queryDocumentSnapshots.toObjects(LibList.class));
                            latestLoadedThingsListSize = queryDocumentSnapshots.getDocuments().size();
                            lastLoadedListItem = queryDocumentSnapshots.getDocuments()
                                    .get(latestLoadedThingsListSize - 1);

                            if (latestLoadedThingsListSize < PAGE_SIZE)
                                isLastPage = true;

                            getLibDataFromCloud();
                        }

                    }
                });
    }

    private void getThingsYouShouldKnowListFromCloud() {
        Timber.i("method");


        //db = ((MainActivity) context).getDb();

        db.collection("ThingsYouShouldKnowList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                Timber.i("task success");
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    HashMap<String, Object> map = (HashMap<String, Object>) document.getData();
                                    //getting List of Map
                                    mapList = (List<Map>) map.get("0");
                                    //Each Map contains data for each Library name and description

                                    //Getting Lib complete Detail
                                    getLibDataFromCloud();
                                }
                            } else
                                Timber.d("Task Null");
                        } else {
                            Timber.i(task.getException(), "task fail");
                            //show some error image
                        }
                    }
                });
    }


    private void getLibDataFromCloud() {
        //Log.i("libDetailName", "getLibData");
        // documentSnapshotList = new ArrayList<>();

        //Fetch List in Desc orderCo
        db.collection(Constants.THINGS_EXPANSION)
                .orderBy("7", Query.Direction.DESCENDING)
                .limit(PAGE_SIZE)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //Log.i("libDetailName", "on complete lib data");
                        if (task.isSuccessful()) {
                            //Log.i("fr", "task success libs");
                            QuerySnapshot querySnapshot = task.getResult();
                            //Log.i("libDetailName", "on comp for");
                            //Each Document Contains Data for Each Library Complete detail
                            if (querySnapshot != null) {
                                documentSnapshotList.addAll(querySnapshot.getDocuments());
                                lastLoadedItemExpansion = querySnapshot.getDocuments()
                                        .get(querySnapshot.getDocuments().size() - 1);
                            }
                            addAdsToList();
                            hideShimmer();
                            //extracting data from the map
                            //extractDataFromMapList(documentSnapshotList);
                            //Log.i("libDetailName","document list size at complete:"+documentSnapshotList.size());
                        } else {
                            Timber.d("Things Expansion Not Successful");
                            //Log.i("fr", "task fail libs", task.getException());
                            //show some error image
                        }
                    }


                });
    }


/*
    private void extractDataFromMapList(List<DocumentSnapshot> documentSnapshotList) {
        List<LibList> allThingsNameAndDescList = new ArrayList<>();
        List<LibList> listToDisplay = new ArrayList<>();
        //Log.i("lib","Map List Size"+mapList.size());
        for (Map eachLibInformation : mapList) {
            //adding each lib name and short desc to model class
            if(eachLibInformation.get("0")  != null && eachLibInformation.get("1") != null)
            allThingsNameAndDescList.add(new LibList(eachLibInformation.get("0").toString(), eachLibInformation.get("1").toString(),0));
        }

        List<LibList> tempAllThingsNameAndDescList = new ArrayList<>(allThingsNameAndDescList);
        if (allThingsNameAndDescList.size() > 20) {
            listToDisplay = tempAllThingsNameAndDescList.subList(20, allThingsNameAndDescList.size());
            Collections.reverse(listToDisplay);
        }

        listToDisplay.addAll(allThingsNameAndDescList.subList(0, 20));
        this.documentSnapshotList = documentSnapshotList;
        addAdsToList(listToDisplay);
        //initLibRec(listToDisplay, documentSnapshotList);

    }
*/

    private void initLibRec() {
        allThingsAndAdList = new ArrayList<>();
        documentSnapshotList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        libAdapter = new LibAdapter(context, allThingsAndAdList, documentSnapshotList);
        fragmentThingsYouShouldKnowBinding.thingsYouShouldKnowRec.setLayoutManager(layoutManager);
        fragmentThingsYouShouldKnowBinding.thingsYouShouldKnowRec.setAdapter(libAdapter);
        //hideShimmer();

        fragmentThingsYouShouldKnowBinding.thingsYouShouldKnowRec.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemsOnScreen = layoutManager.getChildCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();

                boolean isLastItemVisibleOnTheScreen = visibleItemsOnScreen + firstVisibleItemPosition
                        >= totalItemCount;
                boolean isFirstVisibleItemNotAtBeginning = firstVisibleItemPosition >= 0;
                boolean isTotalMoreThanVisible = totalItemCount >= PAGE_SIZE;
                boolean shouldPaginate = isLastItemVisibleOnTheScreen && isFirstVisibleItemNotAtBeginning &&
                        isTotalMoreThanVisible && !isLoading && !isLastPage && isScrolling;


                if (shouldPaginate) {
                    fragmentThingsYouShouldKnowBinding.paginationProgressbar.setVisibility(View.VISIBLE);
                    loadMoreListItems();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling = true;
            }
        });
    }

    private void loadMoreListItems() {
        isLoading = true;
        isScrolling = false;
        //Load List Items
        db.collection(Constants.THINGS_LIST)
                .orderBy("orderBy", Query.Direction.DESCENDING)
                .limit(PAGE_SIZE)
                .startAfter(lastLoadedListItem)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        isLoading = false;
                        if (task.isSuccessful()) {
                            fragmentThingsYouShouldKnowBinding.paginationProgressbar.setVisibility(View.GONE);
                            if (task.getResult() != null) {
                                latestLoadedThingsListSize = task.getResult().getDocuments().size();
                                if(latestLoadedThingsListSize > 0) {
                                    allThingsAndAdList.addAll(task.getResult().toObjects(LibList.class));
                                    lastLoadedListItem = task.getResult().getDocuments().
                                            get(latestLoadedThingsListSize - 1);

                                    if (latestLoadedThingsListSize < PAGE_SIZE)
                                        isLastPage = true;
                                    loadMoreExpansionItem();
                                }else {
                                    isLastPage = true;
                                }
                                //addAdsToList();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isLoading = false;
                        fragmentThingsYouShouldKnowBinding.paginationProgressbar.setVisibility(View.GONE);
                    }
                });

    }

    private void loadMoreExpansionItem() {
        isLoading = true;
        //Load List Items
        db.collection(Constants.THINGS_EXPANSION)
                .orderBy("7", Query.Direction.DESCENDING)
                .limit(PAGE_SIZE)
                .startAfter(lastLoadedItemExpansion)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        isLoading = false;
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                documentSnapshotList.addAll(task.getResult().getDocuments());
                                lastLoadedItemExpansion = task.getResult().getDocuments().
                                        get(task.getResult().getDocuments().size() - 1);
                                addAdsToList();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isLoading = false;
                    }
                });

    }

    private void addAdsToList() {
        TemplateView templateView;
        //allThingsAndAdList = new ArrayList<>(allThingsList);
        int addAdAfterEvery = 7;
        int startLoadingAdsFromThisIndexInMainActivity = 7;
        boolean indexFound = false;

        for (int i = 7; i < allThingsAndAdList.size(); i += addAdAfterEvery) {
            if (isAdded()) {
                Timber.d("Fragment Added");
                if (!(allThingsAndAdList.get(i) instanceof TemplateView)) {
                    //Add TemplateView at some position only if it is not already there
                    templateView = getLayoutInflater().inflate(R.layout.native_rec_adview, null)
                            .findViewById(R.id.smallNativeTemplate);
                    allThingsAndAdList.add(i, templateView);
                    documentSnapshotList.add(i, null);

                    if (!indexFound) {
                        startLoadingAdsFromThisIndexInMainActivity = i;
                        indexFound = true;
                    }
                }
            } else
                Timber.d("Fragment Not Added");
        }


        loadAds(startLoadingAdsFromThisIndexInMainActivity);

    }

    private void loadAds(int startLoadingAdsFromThisIndexInMainActivity) {
        ((MainActivity) context).loadAd(startLoadingAdsFromThisIndexInMainActivity, allThingsAndAdList);
        libAdapter.notifyDataSetChanged();
        //initLibRec();
    }


    private void showShimmer() {
        Timber.d("show shimmer");
        fragmentThingsYouShouldKnowBinding.shimmer.startShimmer();
        fragmentThingsYouShouldKnowBinding.shimmer.setVisibility(View.VISIBLE);
    }

    private void hideShimmer() {
        Timber.d("hide shimmer");
        fragmentThingsYouShouldKnowBinding.shimmer.stopShimmer();
        fragmentThingsYouShouldKnowBinding.shimmer.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.tag(TAG).d("on resume frag id:%s", this.getId());
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.tag(TAG).d("on start frag id:%s", this.getId());
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.tag(TAG).d("on stop frag id:%s", this.getId());
    }

    @Override
    public void onDestroy() {
        /*for(Object item:allThingsAndAdList)
        {
            if(item instanceof TemplateView)
                ((TemplateView)item).getNativeAdView().destroy();
        }*/
        super.onDestroy();
        Timber.tag(TAG).d("on destroy frag id:%s", this.getId());
    }


}
