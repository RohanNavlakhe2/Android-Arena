package com.yog.androidarena.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.yog.androidarena.R;
import com.yog.androidarena.databinding.ActivityMainBinding;
import com.yog.androidarena.util.Constants;
import com.yog.androidarena.util.General;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import timber.log.Timber;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "life";
    private ActivityMainBinding activityMainBinding;
    private FirebaseFirestore db;
    private List<Integer> fragmentOrder = new ArrayList<>();
    private UnifiedNativeAd nativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.tag(TAG).d("on create main activity TaskId:%s", this.getTaskId());
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        //setSupportActionBar(activityMainBinding.includeToolbar.toolbar);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_libs, R.id.navigation_articles)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //navView.setOnNavigationItemSelectedListener(this);

        //FCM
        fcmSubscribeToTopic();

        //Load AdTypes
        loadADType();

        //Firebase Catch
        General.INSTANCE.settingFirebaseCacheToFalse(db);


        //Control Navigation Destination change
        manageDestinationChange(navController);


        //loadAdType();
    }


    public void setTitleAccordingToFragment(int fragmentPos) {
        switch (fragmentPos) {
            case 0:
                activityMainBinding.includeToolbar.fragTitle.setText(getResources().getString(R.string.things_frag_title));
                break;
            case 1:
                activityMainBinding.includeToolbar.fragTitle.setText(getResources().getString(R.string.lib_frag_title));
                break;
            case 2:
                activityMainBinding.includeToolbar.fragTitle.setText(getResources().getString(R.string.article_frag_title));
                break;

        }
    }

    private void getFirebaseDeviceToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                Timber.tag("fcm_token").d(token);
            }
        });
    }

    private void fcmSubscribeToTopic() {
        //Topics
        FirebaseMessaging.getInstance().subscribeToTopic("all_devices")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            Timber.tag("FCM").d("task failed");
                            //msg = getString(R.string.msg_subscribe_failed);
                        } else {
                            Timber.tag("FCM").d("task success");
                        }
                        //Log.d(TAG, msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void loadADType() {
        db.collection("AdType")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Timber.i("task success");
                            if (task.getResult() != null) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    HashMap<String, Object> map = (HashMap<String, Object>) document.getData();
                                    Constants.AD_TYPES = (List<String>) map.get("0");
                                }
                            }

                            for (String s : Constants.AD_TYPES)
                                Timber.tag("ad_type").d(s);
                        }
                    }
                });
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public void loadAd(int index, List<Object> libAndAdList) {
        //Pass index and list to this function and load ad at the end and in onLoadListner
        //Call this function recursively
        if (index >= libAndAdList.size())
            return;
        TemplateView templateView = null;
        if(libAndAdList.get(index) instanceof TemplateView)
          templateView = (TemplateView) libAndAdList.get(index);
        int addAdAtEvery = 7;

        TemplateView finalTemplateView = templateView;

        AdLoader adLoader = new AdLoader.Builder(this, Constants.TEST_AD)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        if (isDestroyed())
                            unifiedNativeAd.destroy();
                        if (nativeAd != null)
                            nativeAd.destroy();

                        nativeAd = unifiedNativeAd;

                        NativeTemplateStyle styles = new NativeTemplateStyle.Builder()
                                .withMainBackgroundColor(new ColorDrawable(
                                        getResources().getColor(R.color.transperent)
                                )).build();

                        if(finalTemplateView != null) {
                            finalTemplateView.setStyles(styles);
                            finalTemplateView.setNativeAd(unifiedNativeAd);
                        }
                    }
                })
                .withAdListener(new AdListener() {

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        Timber.d("Ad Loaded");
                        loadAd(index+addAdAtEvery,libAndAdList);
                    }

                    @Override
                    public void onAdFailedToLoad(int i) {
                        super.onAdFailedToLoad(i);
                        Timber.d("Ad Failed to Load: %s", i);
                        loadAd(index+addAdAtEvery,libAndAdList);
                    }
                }).build();

        int randomAdUrl = new Random().nextInt(Constants.AD_TYPES.size());
        adLoader.loadAd(new AdRequest.Builder()
                .setContentUrl(Constants.AD_TYPES.get(randomAdUrl)).
                        build()
        );


    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.tag(TAG).d("on resume main activity TaskId:%s", this.getTaskId());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Timber.tag(TAG).d("on Restart main activity TaskId:%s", this.getTaskId());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.tag(TAG).d("on start main activity TaskId:%s", this.getTaskId());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Timber.tag(TAG).d("on stop main activity TaskId:%s", this.getTaskId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.tag(TAG).d("on destroy main activity TaskId:%s", this.getTaskId());
    }

    private void manageDestinationChange(NavController navController) {
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                Timber.d("On Destination Changed");
                switch (destination.getId()) {
                    case R.id.navigation_home:
                        fragmentOrder.add(0);
                        break;
                    case R.id.navigation_libs:
                        if (fragmentOrder.get(fragmentOrder.size() - 1) != 1)
                            fragmentOrder.add(1);
                        break;
                    case R.id.navigation_articles:
                        if (fragmentOrder.get(fragmentOrder.size() - 1) != 2)
                            fragmentOrder.add(2);
                        break;
                }
                Timber.d("Fragment Order Size:" + fragmentOrder.size());
            }
        });

    }


    @Override
    public void onBackPressed() {
        Timber.d("Back Pressed");
        if (fragmentOrder.get(fragmentOrder.size() - 1) != 0) {
            Timber.d("if - Back Pressed");
            //Means Current Fragment is not ThingsFragment (HomeFragment)
            //Remove Last index
            fragmentOrder.remove(fragmentOrder.size() - 1);
            Timber.d("Fragment Order Size:" + fragmentOrder.size());
            //And set bottom navigation's item selected to the second last index means the last index
            //after removing the last object
            switch (fragmentOrder.get(fragmentOrder.size() - 1)) {
                case 0:
                    activityMainBinding.navView.setSelectedItemId(R.id.navigation_home);
                    break;
                case 1:
                    activityMainBinding.navView.setSelectedItemId(R.id.navigation_libs);
                    break;
                case 2:
                    activityMainBinding.navView.setSelectedItemId(R.id.navigation_articles);
                    break;
            }
        } else {
            //Means Current fragment is HomeFragment so exit the activity
            Timber.d("else - Back Pressed");
            super.onBackPressed();
        }

    }

}
