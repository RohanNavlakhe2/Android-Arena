package com.yog.androidarena.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.yog.androidarena.R
import com.yog.androidarena.databinding.ActivityOpenInWebviewBinding
import com.yog.androidarena.util.Constants
import timber.log.Timber
import java.util.*


class OpenInWebviewActivity : AppCompatActivity() {

    private lateinit var activityOpenInWebviewBinding: ActivityOpenInWebviewBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityOpenInWebviewBinding = DataBindingUtil.setContentView(this, R.layout.activity_open_in_webview)
        //setting webview client (To open any link in webview only not in browser)
        activityOpenInWebviewBinding.webView.webViewClient = MyWebviewClient()
        //Enable JavaScript
        activityOpenInWebviewBinding.webView.settings.javaScriptEnabled = true

        //load url
        activityOpenInWebviewBinding.webView.loadUrl(intent.getStringExtra(Constants.LINK))

        //Progress on loading
        progressOnLoading()

        // Initialize the Mobile Ads SDK.
        loadBannerAd()
    }

    private fun loadBannerAd() {
        //MobileAds.initialize(this) {}
        val randomAdUrl = Random().nextInt(Constants.AD_TYPES.size)
        val adRequest = AdRequest.Builder().setContentUrl(Constants.AD_TYPES[randomAdUrl]).build()
        activityOpenInWebviewBinding.adView.loadAd(adRequest)
    }

    private fun progressOnLoading() {
        activityOpenInWebviewBinding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, progress: Int) {
                activityOpenInWebviewBinding.progressBar.progress = progress
                if (progress == 100) {
                    Timber.tag("loading").d("progress 100")
                    activityOpenInWebviewBinding.progressBar.visibility = View.GONE
                } else {
                    Timber.tag("loading").d("progress:$progress")
                    activityOpenInWebviewBinding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }


    private inner class MyWebviewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view?.loadUrl(request?.url?.toString())
            }
            return false
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            Timber.tag("web").d("page start")
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            Timber.tag("web").d("page finish")
            super.onPageFinished(view, url)
        }

    }

    override fun onBackPressed() {
        if (activityOpenInWebviewBinding.webView.canGoBack())
            activityOpenInWebviewBinding.webView.goBack()
        else
            super.onBackPressed()
    }


}
