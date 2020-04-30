package com.yog.androidarena.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.yog.androidarena.R;
import com.yog.androidarena.adapter.LinkAdapter;
import com.yog.androidarena.adapter.SomeMoreThingsAdapter;
import com.yog.androidarena.databinding.ActivityLibExpansionBinding;
import com.yog.androidarena.util.Constants;
import com.yog.androidarena.util.General;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import timber.log.Timber;

public class LibExpansionActivity extends AppCompatActivity {

    private ActivityLibExpansionBinding activityLibExpansionBinding;
    private HashMap<String, Object> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLibExpansionBinding = DataBindingUtil.setContentView(this, R.layout.activity_lib_expansion);
        //makingAdVisibilityGoneByDefault();
        setData();

    }

    private void setData() {
        //extract map from intent
        map = (HashMap<String, Object>) getIntent().getSerializableExtra("map");

        //setting intro and subpoints(if any ex.(some more things,cons sections))
        /*map.get("1") contains List of intro and sections map*/

        /*introAndSectionsList.get(0) contains intro text*/
        /*introAndSectionsList.get(1),(2).. are map (Ex. Cons)*/
        List introAndSectionsList = null;
        if (map != null) {
            introAndSectionsList = (List) map.get("1");
            //setting Toolbar Title
            String libraryName = Objects.requireNonNull(map.get("0")).toString();
            activityLibExpansionBinding.includeToolbar.libNameTitle.setText(libraryName);
        }




        //setting intro text

        if (introAndSectionsList != null) {
            Timber.d(introAndSectionsList.get(0).toString() + " size:" + introAndSectionsList.size());
            activityLibExpansionBinding.libDescTxt.setText(introAndSectionsList.get(0).toString());

        }

        //load ad
        int randomAdUrl=new Random().nextInt(Constants.AD_TYPES.size());
        General.INSTANCE.loadNativeTemplateAd
                (this,activityLibExpansionBinding.includeNativeSmallAd.smallNativeTemplate,Constants.AD_TYPES.get(randomAdUrl));

        /*if introAndSectionsList.size()>1 means we have sub sections like (cons,someMoreThings)*/
        //introAndSectionsList[0] will always contain introduction to the lib
        if (introAndSectionsList != null && introAndSectionsList.size() > 1) {
            Timber.tag("list").d("list size:%d", introAndSectionsList.size());
            //means sections are available
            activityLibExpansionBinding.someMoreThingsRec.setVisibility(View.VISIBLE);
            activityLibExpansionBinding.someMoreThingsRec.setLayoutManager(new LinearLayoutManager(this));
            //In firebae cloud firestore we cannot create nested lists,that's why we're passing the complete
            //list to the adapter where we will remove the first elment from the list so remaining will be the
            //list o somemoresections.

            //creating new instance of list and passing into the adapter so that when we remove

            //passing sublist to adapter which is from 1 to size-1 because index contains the intro string.
            List<Map<String, Object>> sectionsList = introAndSectionsList.subList(1, introAndSectionsList.size());
            int addAdAt = 1;
            while (!(addAdAt > sectionsList.size())) {
                if (addAdAt == sectionsList.size()) {
                    sectionsList.add(null);
                } else {
                    sectionsList.add(addAdAt, null);
                }
                addAdAt += 2;
            }
            activityLibExpansionBinding.someMoreThingsRec.setAdapter(new SomeMoreThingsAdapter(this, sectionsList));


        }

        //link section
        initLinkRec();

        //Demo Img
        loadDemoImg();
    }

    private void loadDemoImg() {
        /*map.get("5") contains Demo img url*/
        Glide.with(this)
                .load(Objects.requireNonNull(map.get("5")).toString())
                //.placeholder(getResources().getDrawable(R.drawable.loading_gif))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //Log.i("img_res","Failed :"+e.getMessage());
                        Timber.d("Failed :" + e.getMessage());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //Log.i("img_res","ready");
                        Timber.d("ready");
                        return false;
                    }
                })
                .into(activityLibExpansionBinding.demoImg);

    }

    private void initLinkRec() {
        //map.get("3") contains List<String> of type of link (Library Link)ac
        //map.get("3") contains List<String> links
        //map.get("3") contains List<String> link Subject for mail
        if (!((List<String>) map.get("3")).get(0).equals("")) {
            Timber.i("linkRec");
            activityLibExpansionBinding.linkRec.setLayoutManager(new LinearLayoutManager(this));
            activityLibExpansionBinding.linkRec.setAdapter(new LinkAdapter(this, (List<String>) map.get("3"), (List<String>) map.get("4"), (List<String>) map.get("6")));

        }

    }

    public void showBallonPopup(LinkAdapter linkAdapter) {
        Balloon balloon = new Balloon.Builder(this)
                .setLayout(R.layout.send_mail_popup)
                .setPadding(5)
                .setCornerRadius(4f)
                .setBackgroundColor(ContextCompat.getColor(this, R.color.light_green))
                .setBalloonAnimation(BalloonAnimation.CIRCULAR)
                .setArrowVisible(true)
                .build();
        balloon.show(activityLibExpansionBinding.linkRec, 100, 2);
        //balloon.show(activityLibExpansionBinding.parent);


        setClickOnPopupButton(balloon, linkAdapter);


    }

    private void setClickOnPopupButton(Balloon balloon, LinkAdapter linkAdapter) {
        Timber.tag("ballon").d("method");
        View view = balloon.getContentView();

        view.findViewById(R.id.notNowBtn).setOnClickListener(v -> {
            Timber.tag("ballon").d("not now");
            linkAdapter.whichButton(balloon, 0, map);
        });

        view.findViewById(R.id.okBtn).setOnClickListener(v -> {
            Timber.tag("ballon").d("ok");
            linkAdapter.whichButton(balloon, 1, map);
        });


    }

    /*private void makingAdVisibilityGoneByDefault()
    {
        activityLibExpansionBinding.includeNativeSmallAd2.smallNativeTemplate.setVisibility(View.GONE);
        activityLibExpansionBinding.includeNativeSmallAd3.smallNativeTemplate.setVisibility(View.GONE);

    }*/


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //onNewIntent method is called when we set the launchMode to singleTask or singleInstance
        //in manifest file and start the activity by setting clear_top and new_task.

        //We use the above described settings to start the activity when an activity is already there
        //in BackStack and we don't want to open the new instance of the same activity.

        //So when we start the activity in place of onCreate() method onNewIntent() method is called
        //means activity is opened with its previous instance only.

        //In manifest we set launchMode to singleTask or singleInstance which indicates that in the
        //application there can only be one instance created of that activity at a time means in
        //Backstack the same activity can't be twice.
    }
}
