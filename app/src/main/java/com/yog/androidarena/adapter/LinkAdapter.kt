package com.yog.androidarena.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.balloon.Balloon
import com.yog.androidarena.R
import com.yog.androidarena.activity.GoogleSignInActivity
import com.yog.androidarena.activity.LibExpansionActivity
import com.yog.androidarena.activity.OpenInWebviewActivity
import com.yog.androidarena.databinding.LinkRecBinding
import com.yog.androidarena.util.Constants
import com.yog.androidarena.util.General
import com.yog.androidarena.util.SendMailAsynchronously
import timber.log.Timber
import java.io.Serializable

class LinkAdapter
(private var context: Context, private val linkTypeList: List<String>, private val links: List<String>, private val linkSubjectForMailList: List<String>) :
        RecyclerView.Adapter<LinkAdapter.Holder>() {
    private lateinit var linkRecBinding: LinkRecBinding
    //private lateinit var sendMailAsyc: SendMailAsynchronously
    private var emailFromNewIntent: String? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        //sendMailAsyc = SendMailAsynchronously(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkAdapter.Holder {
        linkRecBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.link_rec, parent, false)
        return Holder(linkRecBinding.root)
    }

    override fun getItemCount(): Int = links.size


    override fun onBindViewHolder(holder: LinkAdapter.Holder, position: Int) {
        linkRecBinding.linkTypeTxt.text = linkTypeList[position]
        linkRecBinding.link.text = links[position]

        linkRecBinding.sendLinkIcon.setOnClickListener {
            sendMailOrSignIn(position)
            //sendLink(linkSubjectForMailList[position],links[position])
        }

        linkRecBinding.link.setOnClickListener {
            openLinkInWebView((it as TextView).text.toString())
        }

    }

    private fun sendLink(subject: String, body: String, receipent: String?) {
        try {
            //sendMailAsyc.execute(subject, body, receipent)
            //creating new instance everytime to avoid
            //IllegalStateException: Task can be executed only once
            //which is generated when we double click the email icon
            SendMailAsynchronously(context).execute(subject, body, receipent)
        } catch (e: IllegalStateException) {
            Timber.tag("expt").d(e)
        }

    }

    private fun openLinkInWebView(url: String) {
        Timber.d(url)
        context.startActivity(Intent(context, OpenInWebviewActivity::class.java).apply {
            putExtra(Constants.LINK, url)
        })


    }


    private fun sendMailOrSignIn(position: Int) {
        val email: String? = General.getStringSp(context, Constants.EMAIL)
        if (email != null) {
            sendLink(linkSubjectForMailList[position], links[position], email)
        } else {
            Timber.i("Not Signed In%:email is $email")
            (context as LibExpansionActivity).showBallonPopup(this)
        }


    }


    fun whichButton(balloon: Balloon, whichBtn: Int, map: HashMap<String, Any>) {
        when (whichBtn) {
            0 -> balloon.dismiss()
            1 -> {
                Timber.d(map["0"].toString())
                balloon.dismiss()
                General.deleteSP(Constants.NOT_NOW,context)
                context.startActivity(Intent(context, GoogleSignInActivity::class.java).apply {
                    putExtra(Constants.MAP, map as Serializable)
                })
                //(context as LibExpansionActivity).finish()
            }

        }
    }

    fun setEmailId(emailFromNewIntent: String) {
        this.emailFromNewIntent = emailFromNewIntent
    }

    open inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}