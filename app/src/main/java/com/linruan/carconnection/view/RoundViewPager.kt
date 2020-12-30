package com.linruan.carconnection.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

/**
 * 圆角viewpager
 */
class RoundViewPager : ViewPager {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onDraw(canvas: Canvas?) {
        val r = 7 * resources.displayMetrics.density
        var path = Path()
        var rect = RectF(0f, 0f, this.width.toFloat(), this.height.toFloat())
        path.addRoundRect(rect, r, r, Path.Direction.CCW)
        canvas?.clipPath(path)
        super.onDraw(canvas)
    }
}