package com.linruan.carconnection.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.LayoutRes
import com.linruan.carconnection.R

abstract class BasePopupWindow : PopupWindow {

    constructor(context: Context, @LayoutRes layout: Int) : super(context) {
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true
        isFocusable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var mRoot = LayoutInflater.from(context).inflate(layout, null)
        contentView = mRoot
        initView(mRoot)
    }
    abstract fun clear()
    abstract fun initView(content: View)
}