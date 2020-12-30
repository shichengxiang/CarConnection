package com.linruan.carconnection.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat


public class FitSystemWindowFrameLayout : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun dispatchApplyWindowInsets(insets: WindowInsets?): WindowInsets {
        var result = super.dispatchApplyWindowInsets(insets);
        if (insets?.isConsumed != true) {
            val count = childCount;
            for (i in 0 until count) {
                result = getChildAt(i).dispatchApplyWindowInsets(insets);
            }
        }
        return result;
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        ViewCompat.requestApplyInsets(child!!)
    }
}
