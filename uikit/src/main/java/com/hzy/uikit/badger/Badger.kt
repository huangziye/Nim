package com.hzy.uikit.badger

import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.Log
import me.leolin.shortcutbadger.ShortcutBadger

/**
 * APP图标未读数红点更新接口
 *
 * https://github.com/leolin310148/ShortcutBadger
 *
 * @author: ziye_huang
 * @date: 2019/5/17
 */
object Badger {
    private val TAG = "Badger"
    private var handler: Handler = Handler()
    private var support = Build.VERSION.SDK_INT < Build.VERSION_CODES.O

    fun updateBadgerCount(context: Context, unreadCount: Int) {
        if (!support) {
            return  // O版本及以上不再支持
        }

        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            var badgerCount = unreadCount
            if (badgerCount < 0) {
                badgerCount = 0
            } else if (badgerCount > 99) {
                badgerCount = 99
            }

            val res = ShortcutBadger.applyCount(context, badgerCount)
            if (!res) {
                support = false // 如果失败就不要再使用了!
            }
            Log.i(TAG, "update badger count " + if (res) "success" else "failed")
        }, 200)
    }
}