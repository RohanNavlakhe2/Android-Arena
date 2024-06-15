package com.yog.androidarena.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.yog.androidarena.R;
import com.yog.androidarena.adapter.LinkAdapter;
import com.yog.androidarena.adapter.SomeMoreThingsAdapter;
import com.yog.androidarena.databinding.ActivityLibExpansionBinding;
import com.yog.androidarena.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import timber.log.Timber;

public class LibExpansionActivity extends AppCompatActivity {

    private ActivityLibExpansionBinding activityLibExpansionBinding;
    private HashMap<String, Object> map;
    private UnifiedNativeAd nativeAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLibExpansionBinding = DataBindingUtil.setContentView(this, R.layout.activity_lib_expansion);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        //load ad (Which at the end of the page)
        int randomAdUrl = new Random().nextInt(Constants.AD_TYPES.size());
        /*General.INSTANCE.loadNativeTemplateAd
                (this,activityLibExpansionBinding.mediumAdAtExpansion,Constants.AD_TYPES.get(randomAdUrl));*/
        fetchAd(Constants.AD_TYPES.get(randomAdUrl));

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

            String libraryName = "";
            if (map.get("0") != null)
                libraryName = map.get("0").toString();
            activityLibExpansionBinding.includeToolbar.libNameTitle.setText(libraryName);
        }


        //setting intro text

        if (introAndSectionsList != null) {
            Timber.d(introAndSectionsList.get(0).toString() + " size:" + introAndSectionsList.size());
            activityLibExpansionBinding.libDescTxt.setText(introAndSectionsList.get(0).toString());

        }



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
            /*int addAdAt = 1;
            while (!(addAdAt > sectionsList.size())) {
                if (addAdAt == sectionsList.size()) {
                    sectionsList.add(null);
                } else {
                    sectionsList.add(addAdAt, null);
                }
                addAdAt += 2;
            }*/
            activityLibExpansionBinding.someMoreThingsRec.setAdapter(new SomeMoreThingsAdapter(this, sectionsList));


        }

        //link section
        initLinkRec();

        //Demo Img
        if (!Objects.requireNonNull(map.get("5")).toString().equals(""))
            loadDemoImg();
    }

    private void loadDemoImg() {
        /*map.get("5") contains Demo img url*/
        Glide.with(this)
                .load(Objects.requireNonNull(map.get("5")).toString())
                .placeholder(getResources().getDrawable(R.drawable.placeholder_image2))
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

    private void fetchAd(String randomAdUrl) {
        AdLoader.Builder builder = new AdLoader.Builder(this, Constants.NATIVE_AD_PRODUCTION_ID);

        builder.forNativeAd(nativeAd -> {
            // If this callback occurs after the activity is destroyed, you must call
            // destroy and return or you may get a memory leak.
            if (isDestroyed()) {
                nativeAd.destroy();
                return;
            }
            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            if (nativeAd != null) {
                nativeAd.destroy();
            }
            nativeAd = nativeAd;
            FrameLayout frameLayout =
                    findViewById(R.id.fl_adplaceholder);
            NativeAdView adView = (NativeAdView) getLayoutInflater()
                    .inflate(R.layout.ad_unified, null);
            populateUnifiedNativeAdView(nativeAd, adView);
            frameLayout.removeAllViews();
            frameLayout.addView(adView);
        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader =
                builder
                        .withAdListener(new AdListener() {

                                            @Override
                                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                                super.onAdFailedToLoad(loadAdError);
                                                Timber.d("Ad Load Failed");
                                            }
                                        }
                        )
                        .build();

        adLoader.loadAd(new AdRequest.Builder().setContentUrl(randomAdUrl).build());
    }

    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getMediaContent().getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
           /* videoStatus.setText(String.format(Locale.getDefault(),
                    "Video status: Ad contains a %.2f:1 video asset.",
                    vc.getAspectRatio()));*/
            Timber.d("Video status: Ad contains a %.2f:1 video asset.", vc.getPlaybackState());

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    //refresh.setEnabled(true);
                    //videoStatus.setText("Video status: Video playback has ended.");
                    Timber.d("Video status: Video playback has ended.");
                    super.onVideoEnd();
                }
            });
        } else {
            //videoStatus.setText("Video status: Ad does not contain a video asset.");
            //refresh.setEnabled(true);
            Timber.d("Video status: Ad does not contain a video asset.");
        }
    }

    @Override
    protected void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        super.onDestroy();

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
