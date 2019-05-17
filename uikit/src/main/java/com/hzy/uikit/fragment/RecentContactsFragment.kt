package com.hzy.uikit.fragment

import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hzy.uikit.R
import com.hzy.uikit.adapter.RecentContactsAdapter
import com.hzy.uikit.badger.Badger
import com.hzy.uikit.code.NimStatusCode
import com.hzy.uikit.drop.DropCover
import com.hzy.uikit.drop.DropManager
import com.hzy.uikit.listener.OnItemClickListener
import com.hzy.uikit.ui.P2PMessageActivity
import com.hzy.uikit.util.NimUtil
import com.hzy.utils.toast
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.msg.MsgServiceObserve
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.netease.nimlib.sdk.msg.model.RecentContact
import kotlinx.android.synthetic.main.fragment_recent_contacts.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * 最近联系人列表
 * @author: ziye_huang
 * @date: 2019/5/17
 */
class RecentContactsFragment : BaseFragment() {


    // 置顶功能可直接使用，也可作为思路，供开发者充分利用RecentContact的tag字段
    private val RECENT_TAG_STICKY: Long = 0x0000000000000001 // 联系人置顶tag
    private val mRecentContacts: MutableList<RecentContact> = mutableListOf()
    private lateinit var mAdapter: RecentContactsAdapter
    // 暂存消息，当RecentContact 监听回来时使用，结束后清掉
    private val cacheMessages = HashMap<String, MutableSet<IMMessage>>()
    private var cached: MutableMap<String, RecentContact> = HashMap(3)// 暂缓刷上列表的数据（未读数红点拖拽动画运行时用）

    override fun layoutId(): Int {
        return R.layout.fragment_recent_contacts
    }

    override fun initView() {
        mAdapter = RecentContactsAdapter(activity as FragmentActivity, mRecentContacts)
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        //设置增加或删除条目的动画
        recyclerView.itemAnimator = DefaultItemAnimator()
        mAdapter.setOnRecentContactsClickListener(object : OnItemClickListener {
            override fun setOnItemClickListener(view: View, position: Int) {
                P2PMessageActivity.startActivity(activity!!, mRecentContacts[position].contactId)
            }

            override fun setOnItemLongClickListener(view: View, position: Int) {

            }
        })
        recyclerView.adapter = mAdapter

        registerObservers(true)
        registerDropCompletedListener(true)
        queryRecentContacts()
    }

    /**
     * ********************** 收消息，处理状态变化 ************************
     */
    private fun registerObservers(register: Boolean) {
        val service = NIMClient.getService(MsgServiceObserve::class.java)
        service.observeReceiveMessage(receiverMessageObserver, register)
        service.observeRecentContact(recentContactObserver, register)
    }

    private val receiverMessageObserver = Observer<List<IMMessage>> { imMessages ->
        if (imMessages != null) {
            for (imMessage in imMessages) {
                var cacheMessageSet: MutableSet<IMMessage>? = cacheMessages[imMessage.sessionId]
                if (cacheMessageSet == null) {
                    cacheMessageSet = HashSet()
                    cacheMessages[imMessage.sessionId] = cacheMessageSet
                }
                cacheMessageSet.add(imMessage)
            }
        }
    }

    private val recentContactObserver: Observer<List<RecentContact>> =
        Observer { recentContacts ->
            if (!DropManager.getInstance().isTouchable()) {
                // 正在拖拽红点，缓存数据
                for (r in recentContacts) {
                    cached[r.contactId] = r
                }
                return@Observer
            }

            onRecentContactChanged(recentContacts)
        }

    private fun onRecentContactChanged(recentContacts: List<RecentContact>) {
        var index: Int
        for (r in recentContacts) {
            index = -1
            for (i in mRecentContacts.indices) {
                if (r.contactId == mRecentContacts[i].contactId && r.sessionType == mRecentContacts[i].sessionType) {
                    index = i
                    break
                }
            }

            if (index >= 0) {
                mRecentContacts.removeAt(index)
            }

            mRecentContacts.add(r)
            /*if (r.sessionType == SessionTypeEnum.Team && cacheMessages[r.contactId] != null) {
                TeamMemberAitHelper.setRecentContactAited(r, cacheMessages[r.contactId])
            }*/
        }

        cacheMessages.clear()

        refreshMessages(true)
    }

    private fun refreshMessages(unreadChanged: Boolean) {
        sortRecentContacts(mRecentContacts)
        mAdapter.notifyDataSetChanged()

        if (unreadChanged) {

            // 方式一：累加每个最近联系人的未读（快）

            var unreadNum = 0
            for (r in mRecentContacts) {
                unreadNum += r.unreadCount
            }

            // 方式二：直接从SDK读取（相对慢）
            //int unreadNum = NIMClient.getService(MsgService.class).getTotalUnreadCount();

            /*if (callback != null) {
                callback.onUnreadCountChange(unreadNum)
            }*/

            Badger.updateBadgerCount(activity!!, unreadNum)
        }
    }

    /**
     * **************************** 排序 ***********************************
     */
    private fun sortRecentContacts(list: List<RecentContact>) {
        if (list.isEmpty()) {
            return
        }
        Collections.sort(list, comp)
    }

    private val comp = Comparator<RecentContact> { o1, o2 ->
        // 先比较置顶tag
        val sticky = (o1.tag and RECENT_TAG_STICKY) - (o2.tag and RECENT_TAG_STICKY)
        if (sticky != 0L) {
            if (sticky > 0) -1 else 1
        } else {
            val time = o1.time - o2.time
            if (time == 0L) 0 else if (time > 0) -1 else 1
        }
    }

    /**
     * 查询最近联系人列表数据
     */
    private fun queryRecentContacts() {
        NimUtil.queryRecentContacts(object : RequestCallback<List<RecentContact>> {
            override fun onSuccess(param: List<RecentContact>?) {
                mRecentContacts.addAll(param!!)
                mAdapter.notifyDataSetChanged()
                refreshMessages(true)
            }

            override fun onFailed(code: Int) {
                NimStatusCode.getMessage(code).toast(activity!!)
            }

            override fun onException(exception: Throwable?) {
                exception?.message?.toast(activity!!)
            }
        })
    }

    private var dropCompletedListener: DropCover.IDropCompletedListener = object : DropCover.IDropCompletedListener {
        override fun onCompleted(id: Any, explosive: Boolean) {
            if (cached != null && !cached.isEmpty()) {
                // 红点爆裂，已经要清除未读，不需要再刷cached
                if (explosive) {
                    if (id is RecentContact) {
                        cached.remove(id.contactId)
                    } else if (id is String && id.contentEquals("0")) {
                        cached.clear()
                    }
                }

                // 刷cached
                if (cached.isNotEmpty()) {
                    val recentContacts = ArrayList<RecentContact>(cached.size)
                    recentContacts.addAll(cached.values)
                    cached.clear()

                    onRecentContactChanged(recentContacts)
                }
            }
        }
    }

    private fun registerDropCompletedListener(register: Boolean) {
        if (register) {
            DropManager.getInstance().addDropCompletedListener(dropCompletedListener)
        } else {
            DropManager.getInstance().removeDropCompletedListener(dropCompletedListener)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        registerObservers(false)
        registerDropCompletedListener(false)
//        registerOnlineStateChangeListener(false)
        DropManager.getInstance().setDropListener(null)
    }
}