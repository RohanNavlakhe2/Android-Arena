package com.yog.androidarena.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.yog.androidarena.R
import com.yog.androidarena.activity.OpenInWebviewActivity
import com.yog.androidarena.databinding.ArticlesRecBinding
import com.yog.androidarena.databinding.NativeRecAdviewBinding
import com.yog.androidarena.model.ArticleModel
import com.yog.androidarena.ui.articles.ArticlesFragment
import com.yog.androidarena.util.Constants
import com.yog.androidarena.util.General
import timber.log.Timber
import java.util.*

class ArticlePagingAdapter
         (private val articlesFragment: ArticlesFragment,
          private val context:Context,
          private val options:FirestorePagingOptions<ArticleModel>):FirestorePagingAdapter<ArticleModel,ArticlePagingAdapter.Holder>(options) {

    private lateinit var articleRecBinding: ArticlesRecBinding
    private lateinit var nativeRecAdviewBinding: NativeRecAdviewBinding
    private val AD_VIEW_TYPE = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        if(viewType==AD_VIEW_TYPE)
        {
            nativeRecAdviewBinding= DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.native_rec_adview,parent,false)
            return Holder(nativeRecAdviewBinding.root)
        }else {
            articleRecBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.articles_rec, parent, false)
            return Holder(articleRecBinding.root)
        }
     }

    override fun onBindViewHolder(holder: Holder, position: Int, model: ArticleModel) {
        if(getItemViewType(position)==AD_VIEW_TYPE)
        {
            val randomAdUrl = Random().nextInt(Constants.AD_TYPES.size)
            General.loadNativeTemplateAd(context,nativeRecAdviewBinding.smallNativeTemplate,Constants.AD_TYPES[randomAdUrl])
            //loadNativeTemplateAd()
        }else {
            val sourceStr = "Source:${model.source}"
            articleRecBinding.title.text = model.title
            articleRecBinding.source.text = sourceStr

            articleRecBinding.articleCard.setOnClickListener {
                openLinkInWebView(model.link)
            }

            Timber.tag("ar_frag").d("paging adapter: " + model.title)
        }
    }

    override fun getItemViewType(position: Int): Int{
        return if(position == 0 || position%7!=0)
            position;
        else
            AD_VIEW_TYPE

    }

    private fun openLinkInWebView(url: String) {
        Timber.d(url)
        //FinestWebView.Builder(context).show(url)
        context.startActivity(Intent(context, OpenInWebviewActivity::class.java).apply {
            putExtra(Constants.LINK, url)
        })


    }

    open inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}