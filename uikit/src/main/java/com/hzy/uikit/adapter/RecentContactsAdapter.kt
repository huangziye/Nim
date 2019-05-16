package com.hzy.uikit.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hzy.uikit.R
import com.hzy.uikit.viewholder.RecentContactsViewHolder
import com.netease.nimlib.sdk.msg.model.RecentContact

class RecentContactsAdapter(val context: Context, val recentContacts: MutableList<RecentContact>) :
    RecyclerView.Adapter<RecentContactsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentContactsViewHolder {
        return RecentContactsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_recent_contacts,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return recentContacts.size
    }

    override fun onBindViewHolder(holder: RecentContactsViewHolder, position: Int) {

    }

}