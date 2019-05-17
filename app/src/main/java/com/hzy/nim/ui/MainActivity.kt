package com.hzy.nim.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hzy.nim.R
import com.hzy.uikit.fragment.BaseFragment
import com.hzy.uikit.fragment.RecentContactsFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        switchFragmentContent(RecentContactsFragment())
    }

    protected fun switchFragmentContent(fragment: BaseFragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        try {
            transaction.commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
