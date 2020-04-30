package com.yog.androidarena.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
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

import java.util.HashMap;
import java.util.List;

import timber.log.Timber;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "life";
    private ActivityMainBinding activityMainBinding;
    private FirebaseFirestore db;

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

        //FCM
        fcmSubscribeToTopic();

        //Load AdTypes
        loadADType();
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

    private void loadAdType() {
        //General.INSTANCE.settingFirebaseCacheToFalse(db);
        db.collection("AdType")
                .document("Ad")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Constants.AD_TYPES = (List<String>) task.getResult().get("0");
                        if (Constants.AD_TYPES != null) {
                            for (String adType : Constants.AD_TYPES)
                                Timber.tag("ad_tyep").d(adType);
                        }
                    }
                });


    }

    private void loadADType() {
        General.INSTANCE.settingFirebaseCacheToFalse(db);
        db.collection("AdType")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Timber.i("task success");
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                HashMap<String, Object> map = (HashMap<String, Object>) document.getData();
                                Constants.AD_TYPES = (List<String>) map.get("0");
                            }

                            for(String s:Constants.AD_TYPES)
                                Timber.tag("ad_type").d(s);
                        }
                    }
                });
    }

    public FirebaseFirestore getDb() {
        return db;
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
}
