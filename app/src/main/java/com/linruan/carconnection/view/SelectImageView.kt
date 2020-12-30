package com.linruan.carconnection.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.view.View

/**
 * 项目名称：CarConnection
 * 类描述：
 * 创建人：shichengxiang
 * 创建时间：2020/9/2 18:44
 * @version
 */
class SelectImageView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setBG(normal: Drawable, selected: Drawable) {
        var bg = StateListDrawable()
        bg.addState(View.ENABLED_FOCUSED_STATE_SET, selected)
        bg.addState(View.ENABLED_STATE_SET, normal)
        bg.addState(View.FOCUSED_STATE_SET, selected)
        bg.addState(View.EMPTY_STATE_SET, normal)
        setBackgroundDrawable(bg)
    }
}
