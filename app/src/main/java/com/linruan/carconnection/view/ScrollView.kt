package com.linruan.carconnection.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView

class ScrollView : NestedScrollView {


    private var mOnScrollChanged: OnScrollChanged? = null
    var isDraging = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
                                                                                  )

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (mOnScrollChanged != null) {
            mOnScrollChanged!!.onScroll(l, t, oldl, oldt)
        }
    }

    fun setOnScrollChanged(onScrollChanged: OnScrollChanged?) {
        mOnScrollChanged = onScrollChanged
    }

    interface OnScrollChanged {
        fun onScroll(l: Int, t: Int, oldl: Int, oldt: Int)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_MOVE -> isDraging = true
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> isDraging = false
        }
        return super.onTouchEvent(ev)

    }
}