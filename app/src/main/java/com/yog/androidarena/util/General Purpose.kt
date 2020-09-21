package com.yog.androidarena.util

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.yog.androidarena.R
import timber.log.Timber

public object General {
    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var newSharedPref: Boolean = true

    private fun init(context: Context) {
        if (newSharedPref) {
            this.context = context
            sharedPreferences = context.getSharedPreferences(Constants.SP, Context.MODE_PRIVATE)
            editor = sharedPreferences.edit()
            newSharedPref = false
        }
    }

    /*operator fun invoke(context: Context) {

        this.context = context

    }*/

    fun settingFirebaseCacheToFalse(db: FirebaseFirestore) {
        val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        db.firestoreSettings = settings
    }

    fun createBooleanSP(boolean: Boolean, context: Context) {
        init(context)
        editor.putBoolean(Constants.NOT_NOW, boolean)
        editor.apply()
    }

    fun createStringSP(context: Context, key: String, value: String?) {
        init(context)
        editor.putString(key, value)
        editor.apply()
    }

    fun getStringSp(context: Context, key: String): String? {
        init(context)
        return sharedPreferences.getString(key, null)
    }

    fun getBooleanSp(context: Context): Boolean {
        init(context)
        return sharedPreferences.getBoolean(Constants.NOT_NOW, false)
    }

    fun deleteBooleanSP(context: Context) {
        init(context)
        editor.remove(Constants.NOT_NOW)
        editor.apply()
    }

    fun loadNativeTemplateAd(context: Context,templateView: TemplateView, contentUrl: String) {
        val adLoader = AdLoader.Builder(context, Constants.TEST_AD)
                .forUnifiedNativeAd { unifiedNativeAd ->
                    val styles = NativeTemplateStyle.Builder()
                            .withMainBackgroundColor(
                                    ColorDrawable(context.resources.getColor(R.color.transperent))).build()
                    templateView.setStyles(styles)
                    templateView.setNativeAd(unifiedNativeAd)
                }
                .build()
        val adRequest = AdRequest.Builder().setContentUrl(contentUrl).build()
        adLoader.loadAd(adRequest)
        Timber.tag("ad_url").d(contentUrl)
    }


}