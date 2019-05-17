package com.hzy.uikit.viewholder

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hzy.uikit.R
import com.hzy.uikit.drop.DropFake

class RecentContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val llRoot = itemView.findViewById<LinearLayout>(R.id.ll_root)
    val tvName = itemView.findViewById<TextView>(R.id.tv_name)
    val tvTime = itemView.findViewById<TextView>(R.id.tv_time)
    val tvContent = itemView.findViewById<TextView>(R.id.tv_content)
    val unreadNumberTip = itemView.findViewById<DropFake>(R.id.unread_number_tip)
}