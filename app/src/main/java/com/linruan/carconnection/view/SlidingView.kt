package com.linruan.carconnection.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.R

class SlidingView : LinearLayout {
    private var startY = 0f
    private var recyclerView: RecyclerView? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun onFinishInflate() {
        super.onFinishInflate()
        recyclerView = findViewById(R.id.rvHomeMasterList)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (visibility != View.GONE) {
            when (ev?.action) {
                MotionEvent.ACTION_DOWN -> startY = ev.y
                MotionEvent.ACTION_MOVE -> {
                    var dis = ev.y - startY
                    return dis > 100 && recyclerView?.scrollY == 0
                }
                MotionEvent.ACTION_UP -> {
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}