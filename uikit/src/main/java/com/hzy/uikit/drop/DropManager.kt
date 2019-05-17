package com.hzy.uikit.drop

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.view.View
import com.hzy.uikit.R
import com.hzy.utils.AppUtil
import com.hzy.utils.DensityUtil

/**
 *
 * @author: ziye_huang
 * @date: 2019/5/17
 */
class DropManager {

    interface IDropListener {
        fun onDropBegin()
        fun onDropEnd()
    }

    companion object {
        // single instance
        private var instance: DropManager? = null
        var TEXT_SIZE: Int = 0
        var CIRCLE_RADIUS: Int = 0

        @Synchronized
        fun getInstance(): DropManager {
            if (instance == null) {
                instance = DropManager()
            }
            return instance!!
        }
    }

    // field
    private var isTouchable: Boolean = false // 是否响应按键事件，如果一个红点已经在响应，其它红点就不响应，同一界面始终最多只有一个红点响应触摸
    private var statusBarHeight: Int = 0 // 状态栏(通知栏)高度
    private var dropCover: DropCover? = null // Drop全屏动画
    private var currentId: Any? = null // 当前正在执行动画的业务节点
    private var textPaint: TextPaint? = null // 文本画笔共享
    private var textYOffset: Float = 0.toFloat() // 文本y轴居中需要的offset
    private var circlePaint: Paint? = null // 圆形画笔共享
    private var innerListener: IDropListener? = null // 红点拖拽动画监听器
    private var enable: Boolean = false
    private val explosionResIds = intArrayOf(
        R.drawable.nim_explosion_one,
        R.drawable.nim_explosion_two,
        R.drawable.nim_explosion_three,
        R.drawable.nim_explosion_four,
        R.drawable.nim_explosion_five
    )

    // interface
    fun init(context: Context, dropCover: DropCover, listener: DropCover.IDropCompletedListener) {
        TEXT_SIZE = DensityUtil.sp2px(context, 12f) // 12sp
        CIRCLE_RADIUS = DensityUtil.dp2px(context, 10) // 10dip
        this.isTouchable = true
        this.statusBarHeight = AppUtil.getStatusBarHeight(context)
        this.dropCover = dropCover
        this.dropCover!!.addDropCompletedListener(listener)
        this.innerListener = null
        this.enable = true
    }

    fun initPaint() {
        getCirclePaint()
        getTextPaint()
    }

    fun destroy() {
        this.isTouchable = false
        this.statusBarHeight = 0
        if (this.dropCover != null) {
            this.dropCover!!.removeAllDropCompletedListeners()
            this.dropCover = null
        }
        this.currentId = null
        this.textPaint = null
        this.textYOffset = 0f
        this.circlePaint = null
        this.enable = false
    }

    fun isEnable(): Boolean {
        return enable
    }

    fun isTouchable(): Boolean {
        return if (!enable) {
            true
        } else isTouchable
    }

    fun setTouchable(isTouchable: Boolean) {
        this.isTouchable = isTouchable
        if (innerListener == null) {
            return
        }

        if (!isTouchable) {
            innerListener!!.onDropBegin() // touchable = false
        } else {
            innerListener!!.onDropEnd() // touchable = true
        }
    }

    fun getTop(): Int {
        return statusBarHeight
    }

    fun down(fakeView: View, text: String) {
        if (dropCover == null) {
            return
        }
        dropCover!!.down(fakeView, text)
    }

    fun move(curX: Float, curY: Float) {
        if (dropCover == null) {
            return
        }
        dropCover!!.move(curX, curY)
    }

    fun up() {
        if (dropCover == null) {
            return
        }
        dropCover!!.up()
    }

    fun addDropCompletedListener(listener: DropCover.IDropCompletedListener) {
        if (dropCover != null) {
            dropCover!!.addDropCompletedListener(listener)
        }
    }

    fun removeDropCompletedListener(listener: DropCover.IDropCompletedListener) {
        if (dropCover != null) {
            dropCover!!.removeDropCompletedListener(listener)
        }
    }

    fun setCurrentId(currentId: Any) {
        this.currentId = currentId
    }

    fun getCurrentId(): Any? {
        return currentId
    }

    fun getCirclePaint(): Paint {
        if (circlePaint == null) {
            circlePaint = Paint()
            circlePaint!!.color = Color.RED
            circlePaint!!.isAntiAlias = true
        }

        return circlePaint!!
    }

    fun getTextPaint(): TextPaint {
        if (textPaint == null) {
            textPaint = TextPaint()
            textPaint!!.isAntiAlias = true
            textPaint!!.color = Color.WHITE
            textPaint!!.textAlign = Paint.Align.CENTER
            textPaint!!.textSize = TEXT_SIZE.toFloat()
            val textFontMetrics = textPaint!!.fontMetrics

            /*
             * drawText从baseline开始，baseline的值为0，baseline的上面为负值，baseline的下面为正值，
             * 即这里ascent为负值，descent为正值。
             * 比如ascent为-20，descent为5，那需要移动的距离就是20 - （20 + 5）/ 2
             */
            textYOffset = -textFontMetrics.ascent - (-textFontMetrics.ascent + textFontMetrics.descent) / 2
        }

        return textPaint!!
    }

    fun getTextYOffset(): Float {
        getTextPaint()
        return textYOffset
    }

    fun getExplosionResIds(): IntArray {
        return explosionResIds
    }

    fun setDropListener(listener: IDropListener?) {
        this.innerListener = listener
    }
}