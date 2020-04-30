package com.yog.androidarena.ui.things_you_should_know;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yog.androidarena.R;
import com.yog.androidarena.activity.MainActivity;
import com.yog.androidarena.adapter.LibAdapter;
import com.yog.androidarena.databinding.FragmentThingsYouShouldKnowBinding;
import com.yog.androidarena.model.LibList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class ThingsYouShouldKnowFragment extends Fragment {

    private static final String TAG="life";
    private FragmentThingsYouShouldKnowBinding fragmentThingsYouShouldKnowBinding;
    private Context context;
    private FirebaseFirestore db;
    private List<Map> mapList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Timber.tag(TAG).d("on create frag id:%s", this.getId());
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fragmentThingsYouShouldKnowBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_things_you_should_know, container, false);
        //setting title
        ((MainActivity)context).setTitleAccordingToFragment(0);
        showShimmer();
        getThingsYouShouldKnowListFromCloud();
        return fragmentThingsYouShouldKnowBinding.getRoot();
    }

    private void getThingsYouShouldKnowListFromCloud() {
        Timber.i("method");
        /*db = FirebaseFirestore.getInstance();
        //making Firebase cache disabled
        General.INSTANCE.settingFirebaseCacheToFalse(db);*/

        db=((MainActivity)context).getDb();

        db.collection("ThingsYouShouldKnowList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Timber.i("task success");
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                                HashMap<String, Object> map = (HashMap<String, Object>) document.getData();
                                //getting List of Map
                                mapList = (List<Map>) map.get("0");
                                //Each Map contains data for each Library name and description

                                //Getting Lib complete Detail
                                getLibDataFromCloud();

                            }
                        } else {
                            Timber.i(task.getException(), "task fail");
                            //show some error image
                        }
                    }
                });
    }


    private void getLibDataFromCloud() {
        //Log.i("libDetailName", "getLibData");
        final List<DocumentSnapshot> documentSnapshotList = new ArrayList<>();
        //db=FirebaseFirestore.getInstance();
        db.collection("ThingsYouShouldKnowExpansion")
                .orderBy("7")
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
                            assert querySnapshot != null;
                            documentSnapshotList.addAll(querySnapshot.getDocuments());

                            //extracting data from the map
                            extractDataFromMapList(documentSnapshotList);
                            //Log.i("libDetailName","document list size at complete:"+documentSnapshotList.size());
                        } else {
                            //Log.i("fr", "task fail libs", task.getException());
                            //show some error image
                        }
                    }


                });
    }

    private void extractDataFromMapList(List<DocumentSnapshot> documentSnapshotList) {
        List<LibList> allThingsNameAndDescList = new ArrayList<>();
        List<LibList> listToDisplay=new ArrayList<>();
        //Log.i("lib","Map List Size"+mapList.size());
        for (Map eachLibInformation : mapList) {
            //adding each lib name and short desc to model class
            allThingsNameAndDescList.add(new LibList(Objects.requireNonNull(eachLibInformation.get("0")).toString(), eachLibInformation.get("1").toString()));
        }

        List<LibList> tempAllThingsNameAndDescList=new ArrayList<>(allThingsNameAndDescList);
        if(allThingsNameAndDescList.size()>20) {
            listToDisplay = tempAllThingsNameAndDescList.subList(20, allThingsNameAndDescList.size());
            Collections.reverse(listToDisplay);
        }

        listToDisplay.addAll(allThingsNameAndDescList.subList(0,20));
        initLibRec(listToDisplay, documentSnapshotList);

    }

    private void initLibRec(List<LibList> allThingsNameAndDescList, List<DocumentSnapshot> documentSnapshotList) {
        int addNullAtForAd=5;
        while (addNullAtForAd<allThingsNameAndDescList.size())
        {
            allThingsNameAndDescList.add(addNullAtForAd,null);
            documentSnapshotList.add(addNullAtForAd,null);
            addNullAtForAd+=5;
        }
        fragmentThingsYouShouldKnowBinding.thingsYouShouldKnowRec.setLayoutManager(new LinearLayoutManager(context));
        fragmentThingsYouShouldKnowBinding.thingsYouShouldKnowRec.setAdapter(new LibAdapter(context, allThingsNameAndDescList, documentSnapshotList));
        hideShimmer();
    }



    private void showShimmer()
    {
        Timber.d("show shimmer");
        fragmentThingsYouShouldKnowBinding.shimmer.startShimmer();
        fragmentThingsYouShouldKnowBinding.shimmer.setVisibility(View.VISIBLE);
     }

    private void hideShimmer()
    {
        Timber.d("hide shimmer");
        fragmentThingsYouShouldKnowBinding.shimmer.stopShimmer();
        fragmentThingsYouShouldKnowBinding.shimmer.setVisibility(View.GONE);
     }

    @Override
    public void onResume() {
        super.onResume();
        Timber.tag(TAG).d("on resume frag id:%s",this.getId());
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.tag(TAG).d("on start frag id:%s",this.getId());
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.tag(TAG).d("on stop frag id:%s",this.getId());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.tag(TAG).d("on destroy frag id:%s",this.getId());
    }

}
