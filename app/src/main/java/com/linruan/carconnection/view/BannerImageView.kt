package com.linruan.carconnection.view

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class BannerImageView : AppCompatImageView {
    constructor(context: Context) : super(context) {}
    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
//        outlineProvider=
//        object :ViewOutlineProvider(){
//            override fun getOutline(view: View?, outline: Outline?) {
//                outline?.setOval(0,0, width, height);
//            }
//        }
//        clipToOutline=true
    }

    override fun onDraw(canvas: Canvas?) {
        val r=7*resources.displayMetrics.density
        var path=Path()
        var rect=RectF(0f,0f, this.width.toFloat(), this.height.toFloat())
        path.addRoundRect(rect,r,r,Path.Direction.CCW)
        canvas?.clipPath(path)
        super.onDraw(canvas)
    }
}