package com.yog.androidarena.ui.libs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.yog.androidarena.MainActivity;
import com.yog.androidarena.R;
import com.yog.androidarena.adapter.LibAdapter;
import com.yog.androidarena.databinding.FragmentLibsBinding;
import com.yog.androidarena.model.LibList;
import com.yog.androidarena.util.General;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class LibsFragment extends Fragment {

    private FragmentLibsBinding fragmentLibsBinding;
    private Context context;

    //Firebase
    String TAG = "firebase result";
    private FirebaseFirestore db;

    List<LibList> sortedAllLibsNameAndDescList = new ArrayList<>();
    private LibListComparator libListComparator;
    //List<DocumentSnapshot> documentSnapshotList;
    List<DocumentSnapshot> sortedDocumentSnapshotList = new ArrayList<>();
    List<Map> mapList;
    private int compareResult;
    private int noOfTimesSortMethodShouldBeCalled;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        fragmentLibsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_libs, container, false);
        //setting title
        ((MainActivity)context).setTitleAccordingToFragment(1);
        Timber.d("hide shimmer");
        showShimmer();
        //getting LibList (Name and short description)
        getLibListFromCloud();
        return fragmentLibsBinding.getRoot();
    }

    /* private void configFirebaseCloud()
     {
         // Access a Cloud Firestore instance from your Activity
         db = FirebaseFirestore.getInstance();

         fragmentLibsBinding.loadDataBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 db.collection("Libs")
                         .get()
                         .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                             @Override
                             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                 if (task.isSuccessful()) {
                                     for (QueryDocumentSnapshot document : task.getResult()) {
                                         if(document.getId().equals("0"))
                                         //Log.i("cloudData", document.getId() + " => " + document.getData());
                                         {
                                             HashMap<String,Object> map= (HashMap<String, Object>) document.getData();
                                             Intent intent = new Intent(context, LibExpansionActivity.class);
                                             intent.putExtra("map", (Serializable)map);
                                             startActivity(intent);
                                         }
                                         //fragmentLibsBinding.title.setText(document.get("Library Title").toString());


                                     }
                                 } else {
                                     Log.i(TAG, "cloud", task.getException());
                                 }
                             }
                         });
             }
         });

     }
 */
    private void initLibRec(List<LibList> sortedLibList, List<DocumentSnapshot> sortedDocumentSnapshotList) {
        fragmentLibsBinding.libRec.setLayoutManager(new LinearLayoutManager(context));
        fragmentLibsBinding.libRec.setAdapter(new LibAdapter(context, sortedLibList, sortedDocumentSnapshotList));
        hideShimmer();
    }

    private void getLibListFromCloud() {

        Timber.i("method");
        db = FirebaseFirestore.getInstance();
        //making Firebase cache disabled
        General.INSTANCE.settingFirebaseCacheToFalse(db);

        db.collection("LibList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.i("fr", "task success");
                            for (QueryDocumentSnapshot document : task.getResult()) {

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
        db.collection("Libs")
                .orderBy("0")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //Log.i("libDetailName", "on complete lib data");
                        if (task.isSuccessful()) {
                            //Log.i("fr", "task success libs");
                            QuerySnapshot querySnapshot = task.getResult();
                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                Timber.tag("lib_de").i(documentSnapshot.getData().get("0").toString());
                                //Each Document Contains Data for Each Library Complete detail
                                documentSnapshotList.add(documentSnapshot);
                            }

                            //extracting data from the map
                            extractDataFromMapList(mapList, documentSnapshotList);
                            //Log.i("libDetailName","document list size at complete:"+documentSnapshotList.size());
                        } else {
                            //Log.i("fr", "task fail libs", task.getException());
                            //show some error image
                        }
                    }


                });
    }

    private void extractDataFromMapList(List<Map> mapList, List<DocumentSnapshot> documentSnapshotList) {
        List<LibList> allLibsNameAndDescList = new ArrayList<>();
        //Log.i("lib","Map List Size"+mapList.size());
        for (Map eachLibInformation : mapList) {
            //adding each lib name and short desc to model class
            allLibsNameAndDescList.add(new LibList(eachLibInformation.get("0").toString(), eachLibInformation.get("1").toString()));
        }

        //Log.i("lib","all Lib Size"+allLibsNameAndDescList.size());

        //size of the list to constrain recursion count
        noOfTimesSortMethodShouldBeCalled = allLibsNameAndDescList.size();

        //Getting Sorted Lib Name and Short Desc List
        //This method sorts LibList class objects list and DocumentSnapshot objects list based
        //on the libName of each LibList object.
        List<LibList> sortedLibList = sortListBasedOnLibName(allLibsNameAndDescList);
        /**
         *  change on 19-4-20
         * List<LibList> sortedLibList = sortListBasedOnLibName(allLibsNameAndDescList, documentSnapshotList);
         */

        //Putting data(libName and short desc) on the ui
        //initLibRec(sortedLibList, sortedDocumentSnapshotList);
        initLibRec(sortedLibList,documentSnapshotList);
        for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
            //documentSnapshot.getData().get("0");
            Timber.i(Objects.requireNonNull(Objects.requireNonNull(documentSnapshot.getData()).get("0")).toString());
        }
    }

    private List<LibList> sortListBasedOnLibName(List<LibList> allLibsNameAndDescList) {
        //Log.i("lib",allLibsNameAndDescList.size()+" all lib size before while");
        //noOfTimesSortMethodShouldBeCalled>1 because in case of 1, no need for comparison
        while (noOfTimesSortMethodShouldBeCalled > 1)
            /*if(noOfTimesSortMethodShouldBeCalled>1)*/ {
            //int i = 0, j = 1;
            //making Comparator to compare strings (libName) from the LibList object
            libListComparator = new LibListComparator();

            //This method finds smallest from the given list and adds to the new list
            findSmallestAndAddToTheList(allLibsNameAndDescList);
            //findSmallestAndAddToTheList(allLibsNameAndDescList, documentSnapshotList, i, j);
            //Log.i("lib",sortedAllLibsNameAndDescList.get(sortedAllLibsNameAndDescList.size()-1).getLibName());
            //Log.i("lib",result+" re");


            /*List<LibList> tempLibList = new ArrayList<>(allLibsNameAndDescList);
            List<DocumentSnapshot> tempDocumentSnapshotList = new ArrayList<>(documentSnapshotList);*/
            if (sortedAllLibsNameAndDescList.size() > 0) {
                //Removes newly added object (to sortedList) from the temp list so that it can find smallest
                //from the remaining list now
               /* tempLibList.remove(sortedAllLibsNameAndDescList.get(sortedAllLibsNameAndDescList.size() - 1));
                tempDocumentSnapshotList.remove(sortedDocumentSnapshotList.get(sortedDocumentSnapshotList.size() - 1));*/
                allLibsNameAndDescList.remove(sortedAllLibsNameAndDescList.get(sortedAllLibsNameAndDescList.size() - 1));
                //documentSnapshotList.remove(sortedDocumentSnapshotList.get(sortedDocumentSnapshotList.size() - 1));
            }
            //Log.i("lib",tempLibList.size()+" temp size");
            //Log.i("lib",allLibsNameAndDescList.size()+" all lib size");
            //k++;
            noOfTimesSortMethodShouldBeCalled--;

            //passing temp list (Which contains remaining objects)
            //sortListBasedOnLibName(tempLibList,tempDocumentSnapshotList);

        }


        //Adding the only element to the sortedList
        if (allLibsNameAndDescList.size() == 1) {
            sortedAllLibsNameAndDescList.add(allLibsNameAndDescList.get(0));
            //sortedDocumentSnapshotList.add(documentSnapshotList.get(0));

        }
        return sortedAllLibsNameAndDescList;
    }

    private void findSmallestAndAddToTheList(List<LibList> tempLibAndDescList) {
        //Log.i("libDetailName","document list size:"+tempDocmentSnapShotList.size());
        //calling comparatore compare method
        int i=0;
        for (int j = 1; j < tempLibAndDescList.size(); j++) {
            compareResult = libListComparator.compare(tempLibAndDescList.get(i), tempLibAndDescList.get(j));
            if (compareResult > 0)
                i = j;
            //j++;
        }
        sortedAllLibsNameAndDescList.add(tempLibAndDescList.get(i));
        //sortedDocumentSnapshotList.add(tempDocmentSnapShotList.get(i));

        //--------------------------------------------------------------------------------------//
       /* compareResult=libListComparator.compare(tempLibAndDescList.get(i), tempLibAndDescList.get(j));
        if(compareResult>0)
        {
            //means libName of first object is grater than libName of second object.
            //1
            i = j;
            j++;
            //j value should be less than the list size
             if (j < tempLibAndDescList.size())
            {
                findSmallestAndAddToTheList(tempLibAndDescList,tempDocmentSnapShotList,i,j);
            }
             else {
                 //adding the smallest object
                //adding the j-1 th element because it is smaller than i th element
                // j-1 th element because j value will cross the index of the list.
                sortedAllLibsNameAndDescList.add(tempLibAndDescList.get(j - 1));
                sortedDocumentSnapshotList.add(tempDocmentSnapShotList.get(j-1));
                Log.i("lib",sortedAllLibsNameAndDescList.get(0).getLibName()+" 1");
                }
        }
        else if(compareResult<0)
        {
            //means libName of first object is smaller than libName of second object.
            //-1
            j++;
            if (j < tempLibAndDescList.size()) {
                findSmallestAndAddToTheList(tempLibAndDescList,tempDocmentSnapShotList,i,j);
            }
             else {
                //adding the smallest object
                //i th element because it is smaller than j th element
                sortedAllLibsNameAndDescList.add(tempLibAndDescList.get(i));
                sortedDocumentSnapshotList.add(tempDocmentSnapShotList.get(i));
                Timber.i("%s -1", sortedAllLibsNameAndDescList.get(0).getLibName());
            }

        }else {
            //0
            //means libName of first object is equal to libName of second object.
            Timber.i("0");
        }*/
    }

    private void showShimmer()
    {
        Timber.d("show shimmer");
        fragmentLibsBinding.includeShimmer.setVisibility(View.VISIBLE);
        fragmentLibsBinding.shimmer.startShimmer();
        fragmentLibsBinding.libRec.setVisibility(View.GONE);
    }

    private void hideShimmer()
    {
        Timber.d("hide shimmer");
        fragmentLibsBinding.includeShimmer.setVisibility(View.GONE);
        fragmentLibsBinding.shimmer.stopShimmer();
        fragmentLibsBinding.libRec.setVisibility(View.VISIBLE);
    }

    private class LibListComparator implements Comparator<LibList> {


        @Override
        public int compare(LibList libList, LibList libList2) {
            if (libList.getLibName().compareToIgnoreCase(libList2.getLibName()) > 0)
                //libList libName should come after libList2 libName
                return 1;
            else if (libList.getLibName().compareToIgnoreCase(libList2.getLibName()) < 0)
                //libList libName should come before libList2 libName
                return -1;
            else
                //libList libName and libList2 libName are same
                return 0;
        }


    }

}




