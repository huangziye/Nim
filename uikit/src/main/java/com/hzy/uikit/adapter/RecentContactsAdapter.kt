package com.hzy.uikit.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hzy.uikit.R
import com.hzy.uikit.drop.DropFake
import com.hzy.uikit.drop.DropManager
import com.hzy.uikit.listener.OnItemClickListener
import com.hzy.uikit.viewholder.RecentContactsViewHolder
import com.netease.nimlib.sdk.msg.model.RecentContact
import java.text.SimpleDateFormat

class RecentContactsAdapter(private val context: Context, private val recentContacts: MutableList<RecentContact>) :
    RecyclerView.Adapter<RecentContactsViewHolder>() {

    private var mRecentContactsListener: OnItemClickListener? = null

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
        val recent = recentContacts[position]
        holder.tvName.text = recent.fromNick
        holder.tvTime.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(recent.time)
        holder.tvContent.text = recent.content

        updateNewIndicator(holder, recent)

        holder.llRoot.setOnClickListener { v -> mRecentContactsListener?.setOnItemClickListener(v, position) }
        holder.unreadNumberTip.setTouchListener(object : DropFake.ITouchListener {
            override fun onDown() {
                DropManager.getInstance().setCurrentId(recent)
                DropManager.getInstance().down(holder.unreadNumberTip, holder.unreadNumberTip.getText().toString())
            }

            override fun onMove(curX: Float, curY: Float) {
                DropManager.getInstance().move(curX, curY)
            }

            override fun onUp() {
                DropManager.getInstance().up()
            }
        })
    }

    /**
     * 更新未读消息
     */
    private fun updateNewIndicator(holder: RecentContactsViewHolder, recent: RecentContact) {
        val unreadNum = recent.unreadCount
        holder.unreadNumberTip.visibility = if (unreadNum > 0) View.VISIBLE else View.GONE
        holder.unreadNumberTip.setText(unreadCountShowRule(unreadNum))
    }

    /**
     * 设置未读消息显示规则
     */
    private fun unreadCountShowRule(unread: Int): String {
        var unread = unread
        unread = Math.min(unread, 99)
        return unread.toString()
    }

    fun setOnRecentContactsClickListener(listener: OnItemClickListener) {
        mRecentContactsListener = listener
    }
}