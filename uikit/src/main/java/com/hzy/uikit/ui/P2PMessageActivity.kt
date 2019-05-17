package com.hzy.uikit.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hzy.uikit.R
import com.netease.nimlib.sdk.msg.model.RecentContact

class P2PMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_p2p_message)
    }

    companion object {
        fun startActivity(context: Context, contactId: String) {
            val intent = Intent(context, P2PMessageActivity::class.java)
            intent.putExtra("contactId", contactId)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
        }
    }
}