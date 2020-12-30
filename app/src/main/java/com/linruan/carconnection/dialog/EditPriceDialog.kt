package com.linruan.carconnection.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import com.blankj.utilcode.util.KeyboardUtils
import com.linruan.carconnection.MCallBack
import com.linruan.carconnection.R
import com.linruan.carconnection.utils.KeyBoardUtil
import kotlinx.android.synthetic.main.layout_dialog_editprice.*
import kotlinx.android.synthetic.main.layout_dialog_editprice.view.*

/**
 * author：shichengxiang on 2020/6/15 18:12
 * email：1328911009@qq.com
 */
class EditPriceDialog : CommonDialog {
    companion object{
        private var mInstance:EditPriceDialog?=null
        private var mDefaultText=""
        fun show(context: Context?,callback: MCallBack<String>?,defaultText:String?=""){
            mDefaultText=defaultText?:""
            if(mInstance==null){
                mInstance= EditPriceDialog(context!!)
            }
            mInstance?.setCallback(callback)
            mInstance?.show()
            KeyboardUtils.showSoftInput()
        }
    }
    constructor(context: Context) : super(context, R.layout.layout_dialog_editprice)

    override fun initView(root: View) {
        window?.setWindowAnimations(R.style.BottomDialogAnim)
        window?.setGravity(Gravity.BOTTOM)
        var etInput=root.etInput
        if(!mDefaultText.isNullOrBlank()){
            etInput.setText(mDefaultText)
            etInput.requestFocus()
        }
        btnOk.setOnClickListener {
            KeyboardUtils.hideSoftInput(etInput)
            var price=etInput.text.toString()
            etInput.setText("")
            mCallBack?.call(price,0)
            dismiss()
        }
    }

    override fun dismiss() {
        KeyboardUtils.hideSoftInput(window!!)
        super.dismiss()
    }

    private var mCallBack: MCallBack<String>? = null
    public fun setCallback(callback: MCallBack<String>?) {
        this.mCallBack = callback
    }
}