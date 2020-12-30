package com.linruan.carconnection.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.linruan.carconnection.logE
import com.linruan.carconnection.utils.Util

class ResizableImageView : AppCompatImageView {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val d = drawable
        if (d != null) {
            // ceil not round - avoid thin vertical gaps along the left/right edges
            val width = MeasureSpec.getSize(widthMeasureSpec)
            logE("宽度 width==$width    ${Util.getScreenWidth()}")
            //高度根据使得图片的宽度充满屏幕计算而得
            val height = Math.ceil((width * d.intrinsicHeight).toDouble() / d.intrinsicWidth).toInt()
            setMeasuredDimension(width, height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}