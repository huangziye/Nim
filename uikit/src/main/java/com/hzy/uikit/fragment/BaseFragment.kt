package com.hzy.uikit.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

/**
 *
 * @author: ziye_huang
 * @date: 2019/5/17
 */
abstract class BaseFragment : Fragment() {

    protected var destroyed = false
    protected val handler = Handler()

    abstract fun layoutId(): Int
    abstract fun initView()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId(), container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        destroyed = false
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyed = true
    }

    protected fun <T : View> findView(resId: Int): T {
        return view?.findViewById(resId) as T
    }

    protected fun postRunnable(runnable: Runnable) {
        handler.post(Runnable {
            // validate
            if (!isAdded) {
                return@Runnable
            }
            // run
            runnable.run()
        })
    }

    protected fun postRunnableDelayed(runnable: Runnable, delay: Long) {
        handler.postDelayed(Runnable {
            // validate
            if (!isAdded) {
                return@Runnable
            }
            // run
            runnable.run()
        }, delay)
    }

    /**
     * 显示输入法面板
     */
    protected fun showKeyboard(isShow: Boolean) {
        val activity = activity ?: return
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (isShow) {
            if (activity.currentFocus == null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            } else {
                imm.showSoftInput(activity.currentFocus, 0)
            }
        } else {
            if (activity.currentFocus != null) {
                imm.hideSoftInputFromWindow(
                    activity.currentFocus!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }
    }

    /**
     * 隐藏输入法面板
     */
    protected fun hideKeyboard(view: View) {
        val activity = activity ?: return
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
            view.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

}