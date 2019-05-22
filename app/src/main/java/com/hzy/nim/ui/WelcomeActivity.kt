package com.hzy.nim.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.hzy.nim.R
import com.hzy.uikit.api.NimUIKit
import com.hzy.uikit.common.util.log.LogUtil
import com.hzy.utils.ACache
import com.netease.nimlib.sdk.NimIntent
import com.netease.nimlib.sdk.auth.LoginInfo
import com.netease.nimlib.sdk.msg.model.IMMessage
import java.util.*

/**
 *
 * @author: ziye_huang
 * @date: 2019/5/21
 */
class WelcomeActivity : AppCompatActivity() {

    private val TAG = "WelcomeActivity"

    private var customSplash = false

    private var firstEnter = true // 是否首次进入

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        if (savedInstanceState != null) {
            intent = Intent() // 从堆栈恢复，不再重复解析之前的intent
        }

        if (!firstEnter) {
            onIntent() // APP进程还在，Activity被重新调度起来
        } else {
            showSplashView() // APP进程重新起来
        }
    }

    private fun showSplashView() {
        // 首次进入，打开欢迎界面
        window.setBackgroundDrawableResource(R.drawable.splash_bg)
        customSplash = true
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        /*
         * 如果Activity在，不会走到onCreate，而是onNewIntent，这时候需要setIntent
         * 场景：点击通知栏跳转到此，会收到Intent
         */
        setIntent(intent)
        if (!customSplash) {
            onIntent()
        }
    }

    override fun onResume() {
        super.onResume()

        if (firstEnter) {
            firstEnter = false
            val runnable = object : Runnable {
                override fun run() {
                    if (!NimUIKit.isInitComplete()) {
                        LogUtil.i(TAG, "wait for uikit cache!")
                        Handler().postDelayed(this, 100)
                        return
                    }

                    customSplash = false
                    if (canAutoLogin()) {
                        onIntent()
                    } else {
                        LoginActivity.start(this@WelcomeActivity)
                        finish()
                    }
                }
            }
            if (customSplash) {
                Handler().postDelayed(runnable, 1000)
            } else {
                runnable.run()
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    // 处理收到的Intent
    private fun onIntent() {
        LogUtil.i(TAG, "onIntent...")

        val loginInfo = ACache.get(this).getAsObject("loginInfo")
        if (null == loginInfo) {
            LoginActivity.start(this)
            finish()
        } else {
            val info = loginInfo as LoginInfo
            NimUIKit.setAccount(info.account)
            // 已经登录过了，处理过来的请求
            val intent = intent
            if (intent != null) {
                if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
                    parseNotifyIntent(intent)
                    return
                }
            }

            if (!firstEnter && intent == null) {
                finish()
            } else {
                showMainActivity()
            }
        }
    }

    /**
     * 已经登陆过，自动登陆
     */
    private fun canAutoLogin(): Boolean {
        val loginInfo = ACache.get(this).getAsObject("loginInfo")
        if (null != loginInfo && loginInfo is LoginInfo) {
            return !TextUtils.isEmpty(loginInfo.account) && !TextUtils.isEmpty(loginInfo.token)
        }

        return false
    }

    private fun parseNotifyIntent(intent: Intent) {
        val messages = intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT) as ArrayList<IMMessage>
        if (messages == null || messages.size > 1) {
            showMainActivity(null)
        } else {
            showMainActivity(Intent().putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, messages[0]))
        }
    }

    private fun showMainActivity() {
        showMainActivity(null)
    }

    private fun showMainActivity(intent: Intent?) {
        MainActivity.start(this@WelcomeActivity, intent)
        finish()
    }

}