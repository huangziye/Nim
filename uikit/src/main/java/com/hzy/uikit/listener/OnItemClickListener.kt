package com.hzy.uikit.listener

import android.view.View

/**
 *
 * @author: ziye_huang
 * @date: 2019/5/17
 */
interface OnItemClickListener {

    fun setOnItemClickListener(view: View, position: Int)

    fun setOnItemLongClickListener(view: View, position: Int)

}