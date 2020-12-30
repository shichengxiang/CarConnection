package com.linruan.carconnection.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.linruan.carconnection.R
import kotlinx.android.synthetic.main.layout_dialog_tip.view.*

abstract class CommonDialog :Dialog {
    constructor(context: Context,@LayoutRes layout:Int,style:Int=R.style.TipDialog) : super(context, style){
        var root=layoutInflater.inflate(layout,null)
        setContentView(root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        val dm = context.resources.displayMetrics
        window?.attributes?.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes?.height = WindowManager.LayoutParams.WRAP_CONTENT
        initView(root)
    }

    abstract fun initView(root:View)

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
    }
}