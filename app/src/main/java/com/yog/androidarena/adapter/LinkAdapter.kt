package com.yog.androidarena.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.yog.androidarena.R
import com.yog.androidarena.databinding.LinkRecBinding
import com.yog.androidarena.util.SendMailAsynchronously

class LinkAdapter
(private val context: Context, private val linkTypeList: List<String>, private val links: List<String>,private val linkSubjectForMailList: List<String>) :
        RecyclerView.Adapter<LinkAdapter.Holder>() {
    private lateinit var linkRecBinding: LinkRecBinding
    private lateinit var sendMailAsyc:SendMailAsynchronously

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        sendMailAsyc= SendMailAsynchronously(context)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkAdapter.Holder {
        linkRecBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.link_rec, parent, false)
        return Holder(linkRecBinding.root)
    }

    override fun getItemCount(): Int=links.size


    override fun onBindViewHolder(holder: LinkAdapter.Holder, position: Int) {
        linkRecBinding.linkTypeTxt.text=linkTypeList[position]
        linkRecBinding.link.text=links[position]

        linkRecBinding.sendLinkIcon.setOnClickListener {
            sendLink(linkSubjectForMailList[position],links[position])
        }

     }

    private fun sendLink(subject:String,body:String)
    {
        sendMailAsyc.execute(subject,body)
    }

    open inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}