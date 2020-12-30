package com.linruan.carconnection.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.view.WindowManager
import com.linruan.carconnection.R
import java.lang.Exception

object LoadingDialog {
    private var mDialog: Dialog? = null

    fun loading(context: Context?) {
        if (context == null || context !is Activity) {
            return
        }
        if (mDialog == null) {
            mDialog = object : CommonDialog(context, R.layout.layout_dialog_loading, R.style.LoadingDialog) {
                override fun initView(root: View) {
                    var attr = window?.attributes
                    attr?.width = WindowManager.LayoutParams.WRAP_CONTENT
                    window?.attributes = attr
                    setCanceledOnTouchOutside(false)
                    setCancelable(true)
                }
            }
        }
        try {
            if (mDialog?.isShowing == false) {
                mDialog?.show()
            }
        } catch (e: Exception) {
        }
    }

    fun dismiss() {
        mDialog?.dismiss()
    }

}