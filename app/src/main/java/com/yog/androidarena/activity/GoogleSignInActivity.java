package com.yog.androidarena.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.yog.androidarena.R;
import com.yog.androidarena.util.Constants;
import com.yog.androidarena.util.General;

import timber.log.Timber;

public class GoogleSignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "SignIn";
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.yog.androidarena.databinding.ActivityGoogleSignInBinding activityGoogleSignInBinding = DataBindingUtil.setContentView(this, R.layout.activity_google_sign_in);

        //Initialize Ads
       /* MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });*/

        //loadNativeTemplateAd();
        //buildAd();

        activityGoogleSignInBinding.signInButton.setOnClickListener(this);
        activityGoogleSignInBinding.notNowBtn.setOnClickListener(this);
        configureGooglSignin();
    }



    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void updateUI(GoogleSignInAccount account) {
        Intent intent = null;
        if (getIntent().getSerializableExtra(Constants.MAP) != null) {
            //means user is coming from LibExpansionActivity and not signed in
            //if(account!=null||General.INSTANCE.getBooleanSp(this)) {
            if (account != null) {
                //user pressed sign in btn
                intent = new Intent(this, LibExpansionActivity.class);
                intent.putExtra(Constants.MAP, getIntent().getSerializableExtra(Constants.MAP));
                General.INSTANCE.createStringSP(this, Constants.EMAIL, account.getEmail());
                //intent.putExtra(Constants.EMAIL, account.getEmail());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else if (General.INSTANCE.getBooleanSp(this)) {
                //means user has pressed not now btn
                onBackPressed();
            }
               /* startActivity(intent);
                finish();*/
            //}
        } else if (account != null) {
            //means user is launching the app and user is already signed in
            intent = new Intent(this, MainActivity.class);
            General.INSTANCE.createStringSP(this, Constants.EMAIL, account.getEmail());
            startActivity(intent);
            finish();

        } else if (General.INSTANCE.getBooleanSp(this)) {
            //means user has presssed not now button
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void configureGooglSignin() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Timber.tag(TAG).w("signInResult:success");
            /**
             * Making user signed in with google visible in firebase users section
             */
            if (account != null) {
                AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseAuth.getInstance().signInWithCredential(authCredential);
            }
            /**
             * Making user signed in with google visible in firebase users section
             * If we don't put above two lines then user will be signed in with google but won't be
             * shown in firebase users section.
             */
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Timber.tag(TAG).w("signInResult:failed code=%s", e.getStatusCode());
            updateUI(null);
        }

    }


    /*private void buildAd() {
        AdLoader adLoader = new AdLoader.Builder(this, Constants.TEST_AD)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // Assumes you have a placeholder FrameLayout in your View layout
                        // (with id fl_adplaceholder) where the ad is to be placed.
                        Timber.tag("native_ad").d("on unified");
                        LinearLayout frameLayout =
                                findViewById(R.id.adContainerFrameLayout);
                        UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
                                .inflate(R.layout.native_ad_at_sign_in, null);
                        //displayUnifiedNativeAd(frameLayout,unifiedNativeAd);
                        populateAd(unifiedNativeAd, adView);

                        frameLayout.removeAllViews();
                        frameLayout.addView(adView);
                    }
                }).withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Handle the failure by logging, altering the UI, and so on.
                        Timber.tag("native_ad").d("ad load failed,Erroe code:" + errorCode);
                    }

                    @Override
                    public void onAdClicked() {
                        // Log the click event or other custom behavior.
                        Timber.tag("native_ad").d("ad clicked");

                    }
                }).build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }*/

   /* private void displayUnifiedNativeAd(ViewGroup parent, UnifiedNativeAd ad) {
        Timber.tag("native_ad").d("display ad");
        // Inflate a layout and add it to the parent ViewGroup.
        LayoutInflater inflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        UnifiedNativeAdView adView = (UnifiedNativeAdView) inflater
                .inflate(R.layout.native_ad_at_sign_in, null);

        // Locate the view that will hold the headline, set its text, and call the
        // UnifiedNativeAdView's setHeadlineView method to register it.


        TextView headlineView = adView.findViewById(R.id.adText);
        headlineView.setText(ad.getHeadline());
        adView.setHeadlineView(headlineView);


        // Repeat the above process for the other assets in the UnifiedNativeAd
        // using additional view objects (Buttons, ImageViews, etc).


        // If the app is using a MediaView, it should be
        // instantiated and passed to setMediaView. This view is a little different
        // in that the asset is populated automatically, so there's one less step.
        MediaView mediaView = (MediaView) adView.findViewById(R.id.adImg);
        adView.setMediaView(mediaView);

        // Call the UnifiedNativeAdView's setNativeAd method to register the
        // NativeAdObject.
        adView.setNativeAd(ad);

        // Ensure that the parent view doesn't already contain an ad view.
        parent.removeAllViews();

        // Place the AdView into the parent.
        parent.addView(adView);
    }*/

    /*private void populateAd(UnifiedNativeAd unifiedNativeAd, UnifiedNativeAdView adView) {
        TextView headlineView = adView.findViewById(R.id.adText);
        headlineView.setText(unifiedNativeAd.getHeadline());
        adView.setHeadlineView(headlineView);


        // Repeat the above process for the other assets in the UnifiedNativeAd
        // using additional view objects (Buttons, ImageViews, etc).


        // If the app is using a MediaView, it should be
        // instantiated and passed to setMediaView. This view is a little different
        // in that the asset is populated automatically, so there's one less step.
        MediaView mediaView = (MediaView) adView.findViewById(R.id.adImg);
        adView.setMediaView(mediaView);

        // Call the UnifiedNativeAdView's setNativeAd method to register the
        // NativeAdObject.
        adView.setNativeAd(unifiedNativeAd);
    }*/

    /*private void loadNativeTemplateAd() {
        //MobileAds.initialize(this, "[_app-id_]");
        AdLoader adLoader = new AdLoader.Builder(this, Constants.TEST_AD)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        NativeTemplateStyle styles = new
                                NativeTemplateStyle.Builder().
                                withMainBackgroundColor(new ColorDrawable(getResources().getColor(R.color.transperent))).build();

                        TemplateView template = findViewById(R.id.my_template);
                        template.setStyles(styles);
                        template.setNativeAd(unifiedNativeAd);

                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }
*/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.notNowBtn:
                General.INSTANCE.createBooleanSP(true, this);
                updateUI(null);
                break;
        }
    }
}
