package com.hzy.nim

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hzy.uikit.business.recent.RecentContactsFragment
import com.hzy.uikit.common.fragment.TFragment

/**
 *
 * @author: ziye_huang
 * @date: 2019/5/23
 */
class RecentContactsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_contacts)
        switchFragmentContent(RecentContactsFragment())
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