package com.linruan.carconnection.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * author：shichengxiang on 2020/6/24 16:00
 * email：1328911009@qq.com
 */
class CheckImageView: AppCompatImageView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    fun initView(){
        this.setOnClickListener {
            this.isSelected=!this.isSelected
        }
    }
}