package com.linruan.carconnection.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.linruan.carconnection.R
import com.linruan.carconnection.logE
import kotlinx.android.synthetic.main.layout_tab_talk.view.*

class TabLayout : LinearLayout {
    var margin = 5 * resources.displayMetrics.density
    var fillWidth = 0;
    var currentView: TextView? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView(context!!)
    }

    fun initView(context: Context) {
        addView(LayoutInflater.from(context).inflate(R.layout.layout_tab_talk, null))
//        lp_line.width = fillWidth / 3 - margin * 2
//        line.animate().translationX(margin.toFloat()).start()//line移动位置
        tvTab1.setOnClickListener(mListener)
        tvTab2.setOnClickListener(mListener)
        tvTab3.setOnClickListener(mListener)
        currentView = tvTab1
    }
    fun setTab(tabs:Array<String>){

    }

    public fun setCurrent(position: Int) {
        postInvalidate()
        when (position) {
            0 -> tvTab1.performClick()
            1 -> tvTab2.performClick()
            2 -> tvTab3.performClick()
        }
    }

    interface OnItemSelectedListener {
        fun onSelected(position: Int)
    }

    private var mOnItemSelectedListener: OnItemSelectedListener? = null
    public fun setOnItemSelectedListener(onItemSelectedListener: OnItemSelectedListener?) {
        this.mOnItemSelectedListener = onItemSelectedListener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val lp_line = line.layoutParams
//        margin = (r - l) / 2 / 2
        fillWidth = r - l
        lp_line.width = (r - l) / 3 / 2
        line.layoutParams = lp_line
    }

    var mListener = OnClickListener { v ->
        when (v?.id) {
            R.id.tvTab1 -> {
                ViewCompat.animate(line).translationX(0f).start()
                if (currentView != tvTab1 && currentView != null) {
                    ViewCompat.animate(currentView!!).scaleX(1.0f).scaleY(1.0f).start()
                }
                currentView = tvTab1
                ViewCompat.animate(currentView!!).scaleX(1.2f).scaleY(1.2f).start()
                mOnItemSelectedListener?.onSelected(0)
            }
            R.id.tvTab2 -> {
                line.animate().translationX((fillWidth / 3*1).toFloat()).start()
                if (currentView != tvTab2 && currentView != null) {
                    ViewCompat.animate(currentView!!).scaleX(1.0f).scaleY(1.0f).start()
                }
                currentView = tvTab2
                ViewCompat.animate(currentView!!).scaleX(1.2f).scaleY(1.2f).start()
                mOnItemSelectedListener?.onSelected(1)
            }
            R.id.tvTab3 -> {
                line.animate().translationX((fillWidth / 3 * 2).toFloat()).start()
                if (currentView != tvTab3 && currentView != null) {
                    ViewCompat.animate(currentView!!).scaleX(1.0f).scaleY(1.0f).start()
                }
                currentView = tvTab3
                ViewCompat.animate(currentView!!).scaleX(1.2f).scaleY(1.2f).start()
                mOnItemSelectedListener?.onSelected(2)
            }
        }
    }
}