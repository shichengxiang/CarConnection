package com.linruan.carconnection.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import com.linruan.carconnection.Constant
import com.linruan.carconnection.MCallBack
import com.linruan.carconnection.R
import kotlinx.android.synthetic.main.dialog_choose_paytype.view.*

/**
 * author：shichengxiang on 2020/6/15 18:12
 * email：1328911009@qq.com
 */
class PayDialog:CommonDialog {
    private var mCurrentPayway=Constant.PAY_TYPE_WEIXIN
    constructor(context: Context) : super(context, R.layout.dialog_choose_paytype)

    override fun initView(root: View) {
        window?.setWindowAnimations(R.style.BottomDialogAnim)
        window?.setGravity(Gravity.BOTTOM)
        var llChooseWeixin=root.llChooseWeixin
        var llChooseAlipay=root.llChooseAliPay
        var llChooseBalance=root.llChooseBalance
        var cbWeixin=root.cbWeiXin
        var cbAlipay=root.cbZhiFuBao
        var cbBalance=root.cbBalance
        var btnPay=root.btnPay
        var tvCloseDialog=root.tvCloseDialog
        llChooseWeixin.setOnClickListener {
            mCurrentPayway=Constant.PAY_TYPE_WEIXIN
            cbWeixin.isChecked=true
            cbAlipay.isChecked=false
            cbBalance.isChecked=false
        }
        llChooseAlipay.setOnClickListener {
            mCurrentPayway=Constant.PAY_TYPE_ZHIFUBAO
            cbWeixin.isChecked=false
            cbAlipay.isChecked=true
            cbBalance.isChecked=false
        }
        llChooseBalance.setOnClickListener {
            mCurrentPayway=Constant.PAY_TYPE_BALANCE
            cbWeixin.isChecked=false
            cbAlipay.isChecked=false
            cbBalance.isChecked=true
        }
        btnPay.setOnClickListener {
            dismiss()
            mCallBack?.call(mCurrentPayway,0)
        }
        tvCloseDialog.setOnClickListener {
            dismiss()
        }
    }
    private var mCallBack:MCallBack<String>?=null
    public fun setCallback(callback:MCallBack<String>){
        this.mCallBack=callback
    }
}
