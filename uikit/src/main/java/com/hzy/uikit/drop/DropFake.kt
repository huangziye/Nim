package com.hzy.uikit.drop

import android.content.Context
import android.graphics.Canvas
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ListView
import android.widget.ScrollView
import androidx.recyclerview.widget.RecyclerView

/**
 * 未读数红点View（自绘红色的圆和数字）
 * 触摸之产生DOWN/MOVE/UP事件（不允许父容器处理TouchEvent），回调给浮在上层的DropCover进行拖拽过程绘制。
 * View启动过程：Constructors -> onAttachedToWindow -> onMeasure() -> onSizeChanged() -> onLayout() -> onDraw()
 * @author: ziye_huang
 * @date: 2019/5/17
 */
class DropFake @JvmOverloads constructor(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var radius: Int = 0 // 圆形半径
    private var circleX: Float = 0.toFloat() // 圆心x坐标
    private var circleY: Float = 0.toFloat() // 圆心y坐标
    private var text: String? = null // 要显示的文本（数字）
    private var firstInit = true // params init once
    private var touchListener: ITouchListener? = null

    init {
        DropManager.getInstance().initPaint()
    }

    /**
     * 未读数红点检测触摸事件产生DOWN/MOVE/UP
     */
    interface ITouchListener {
        fun onDown()

        fun onMove(curX: Float, curY: Float)

        fun onUp()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (firstInit) {
            firstInit = false
            radius = DropManager.CIRCLE_RADIUS // 或者view.getWidth()/2
            circleX = (w / 2).toFloat()
            circleY = (h / 2).toFloat()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // circle
        canvas.drawCircle(circleX, circleY, radius.toFloat(), DropManager.getInstance().getCirclePaint())
        // text
        if (!TextUtils.isEmpty(text)) {
            canvas.drawText(
                text!!, circleX, circleY + DropManager.getInstance().getTextYOffset(),
                DropManager.getInstance().getTextPaint()
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 如果未初始化 DropManager，则默认任何事件都不处理
        if (!DropManager.getInstance().isEnable()) {
            return super.onTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (DropManager.getInstance().isTouchable()) {
                    if (touchListener != null) {
                        DropManager.getInstance().setTouchable(false)
                        // 不允许父控件处理TouchEvent，当父控件为ListView这种本身可滑动的控件时必须要控制
                        disallowInterceptTouchEvent(true)
                        touchListener!!.onDown()
                    }
                    return true // eat
                }

                return false
            }
            MotionEvent.ACTION_MOVE -> if (touchListener != null) {
                // getRaw:获取手指当前所处的相对整个屏幕的坐标
                touchListener!!.onMove(event.rawX, event.rawY)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> if (touchListener != null) {
                // 将控制权还给父控件
                disallowInterceptTouchEvent(false)
                touchListener!!.onUp()
            }
            else -> {
            }
        }
        return super.onTouchEvent(event)
    }

    fun setText(text: String) {
        this.text = text
        invalidate()
    }

    fun getText(): String? {
        return text
    }

    fun setTouchListener(listener: ITouchListener) {
        touchListener = listener
    }

    private fun disallowInterceptTouchEvent(disable: Boolean) {
        var parent: ViewGroup? = parent as ViewGroup
        parent!!.requestDisallowInterceptTouchEvent(disable)

        while (true) {
            if (parent == null) {
                return
            }

            if (parent is RecyclerView || parent is ListView || parent is GridView ||
                parent is ScrollView
            ) {
                parent.requestDisallowInterceptTouchEvent(disable)
                return
            }

            val vp = parent.parent
            if (vp is ViewGroup) {
                parent = parent.parent as ViewGroup
            } else {
                return  // DecorView
            }
        }
    }
}