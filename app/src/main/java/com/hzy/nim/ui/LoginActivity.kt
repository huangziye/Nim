package com.hzy.nim.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hzy.nim.R
import com.hzy.uikit.api.NimUIKit
import com.hzy.uikit.code.NimStatusCode
import com.hzy.uikit.util.NimUtil
import com.hzy.utils.ACache
import com.hzy.utils.toast
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.auth.LoginInfo
import kotlinx.android.synthetic.main.activity_login.*

/**
 *
 * @author: ziye_huang
 * @date: 2019/5/10
 */
class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_login.setOnClickListener(this)

        val loginInfo = ACache.get(this@LoginActivity).getAsObject("loginInfo")
        if (null != loginInfo && loginInfo is LoginInfo) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    companion object {
        private val KICK_OUT = "KICK_OUT"

        fun start(context: Context) {
            start(context, false)
        }

        fun start(context: Context, kickOut: Boolean) {
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.putExtra(KICK_OUT, kickOut)
            context.startActivity(intent)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login -> {
                val account = et_account.text.toString().toLowerCase().trim()
                val password = et_password.text.toString().trim()
                if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                    "用户名或密码不能为空".toast(this@LoginActivity)
                    return
                }
                val callback = object : RequestCallback<LoginInfo> {
                    override fun onSuccess(loginInfo: LoginInfo?) {
                        loginInfo?.account?.toast(this@LoginActivity)
                        ACache.get(this@LoginActivity).put("loginInfo", loginInfo)
                        // APP 直接调用 SDK 登陆方法成功之后，需要调用该方法通知UIKit
                        NimUIKit.loginSuccess(loginInfo!!.account)
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }

                    override fun onFailed(code: Int) {
                        NimStatusCode.getMessage(code).toast(this@LoginActivity)
                    }

                    override fun onException(exception: Throwable?) {
                        exception.toString().toast(this@LoginActivity)
                    }
                }
                NimUtil.login(LoginInfo(account, password), callback)
            }
        }
    }
}