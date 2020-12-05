package com.yog.androidarena.util

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
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

    fun settingFirebaseCache(db: FirebaseFirestore) {
        val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        db.firestoreSettings = settings
    }

    fun createBooleanSP(key:String,booleanValue: Boolean, context: Context) {
        init(context)
        editor.putBoolean(key, booleanValue)
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

    fun createIntSP(context: Context, key: String, value: Int) {
        init(context)
        editor.putInt(key, value)
        editor.apply()
    }

    fun getIntSp(context: Context, key: String): Int {
        init(context)
        return sharedPreferences.getInt(key, -1)
    }

    fun getBooleanSp(key:String,context: Context): Boolean {
        init(context)
        return sharedPreferences.getBoolean(key,false)
    }

    fun deleteSP(key: String, context: Context) {
        init(context)
        editor.remove(key)
        editor.apply()
    }

    fun loadNativeTemplateAd(context: Context,templateView: TemplateView, contentUrl: String) {
        val adLoader = AdLoader.Builder(context, Constants.NATIVE_AD_TEST_ID)
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

    private fun hasInternetConnection(context: Context): Boolean {
        val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        /*val connectivityManager = context.getApplication<MainApplication>().getSystemService(
                Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager*/
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }


}