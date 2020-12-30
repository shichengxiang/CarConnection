package com.linruan.carconnection.view

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent

/**
 * author：shichengxiang on 2020/6/22 17:55
 * email：1328911009@qq.com
 */
class MoneyEditText : androidx.appcompat.widget.AppCompatEditText {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode==KeyEvent.KEYCODE_DEL && text?.length?:0<=1){
            return false
        }
        return super.onKeyDown(keyCode, event)
    }
}