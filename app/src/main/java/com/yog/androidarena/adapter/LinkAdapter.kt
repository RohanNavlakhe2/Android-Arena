package com.yog.androidarena.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.balloon.Balloon
import com.yog.androidarena.R
import com.yog.androidarena.activity.GoogleSignInActivity
import com.yog.androidarena.activity.LibExpansionActivity
import com.yog.androidarena.activity.OpenInWebviewActivity
import com.yog.androidarena.databinding.LinkRecBinding
import com.yog.androidarena.java_mail_api.GMailSender
import com.yog.androidarena.util.Constants
import com.yog.androidarena.util.General
import com.yog.androidarena.util.SendMailAsynchronously
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.single
import timber.log.Timber
import java.io.Serializable
import java.lang.Exception
import java.util.ArrayList

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
            //SendMailAsynchronously(context).execute(subject, body, receipent)

            Toast.makeText(context, "Wait We're sending mail", Toast.LENGTH_SHORT).show()


            //Observer is used when there are one or more events to be emitted by observable
            //Single is used when there is only one event

            Single.just(arrayOf(subject,body,receipent))
                .map { emailContent ->

                    val sender = GMailSender(
                        "anroidartsdevelopers@gmail.com",
                        "supercoder2@"
                    )
                    sender.sendMail(
                        emailContent[0], emailContent[1],
                        "anroidartsdevelopers@gmail.com", emailContent[2]
                    )

                   return@map "Email Successfully sent"
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<String> {

                    override fun onSuccess(t: String?) {
                        Toast.makeText(context, t, Toast.LENGTH_SHORT).show()
                        Timber.tag("Email").d("on success : $t")
                    }

                    override fun onError(e: Throwable?) {
                        Toast.makeText(context,"Email Send failed", Toast.LENGTH_SHORT).show()
                        Timber.tag("Email").d("on error : ${e?.message}")

                    }

                    override fun onSubscribe(d: Disposable?) {
                        Timber.tag("Email").d("on subscribe")
                    }

                })


            //Observable alternative
           /* Observable.just(arrayOf(subject,body,receipent))
                .map { emailContent ->

                    val sender = GMailSender(
                        "anroidartsdevelopers@gmail.com",
                        "supercoder2@"
                    )
                    sender.sendMail(
                        emailContent[0], emailContent[1],
                        "anroidartsdevelopers@gmail.com", emailContent[2]
                    )

                    return@map "Email Successfully sent"
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableObserver<String>() {
                    override fun onNext(t: String?) {
                        Toast.makeText(context, t , Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(e: Throwable?) {
                        Toast.makeText(context,"Email Send failed", Toast.LENGTH_SHORT).show()

                    }

                    override fun onComplete() {

                    }

                })
*/

        } catch (e: IllegalStateException) {
            Timber.tag("Email").d(e)
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