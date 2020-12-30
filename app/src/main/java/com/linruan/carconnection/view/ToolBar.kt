package com.linruan.carconnection.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import com.linruan.carconnection.R
import kotlinx.android.synthetic.main.layout_toolbar.view.*

class ToolBar : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    fun initView(context: Context) {
        var root = LayoutInflater.from(context).inflate(R.layout.layout_toolbar, null)
        addView(root)
        ivBack.setOnClickListener { (context as Activity).finish() }
    }

    public fun getLeftImageView(): ImageView {
        return ivBack
    }

    public fun setTitle(title: String) {
        tvTitle.text = title
    }

    public fun getRightImageView(): ImageView {
        return ivRight
    }

}