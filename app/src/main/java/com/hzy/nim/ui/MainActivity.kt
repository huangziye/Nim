package com.hzy.nim.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hzy.nim.R
import com.hzy.uikit.business.recent.RecentContactsFragment
import com.hzy.uikit.common.fragment.TFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        switchFragmentContent(RecentContactsFragment())
    }


    companion object {
        fun start(context: Context) {
            start(context, null)
        }

        fun start(context: Context, extras: Intent?) {
            val intent = Intent()
            intent.setClass(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            if (extras != null) {
                intent.putExtras(extras)
            }
            context.startActivity(intent)
        }

    }

    protected fun switchFragmentContent(fragment: TFragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        try {
            transaction.commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
